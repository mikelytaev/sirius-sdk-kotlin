package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.state_machines

import com.sirius.library.agent.aries_rfc.SchemasNonSecretStorage
import com.sirius.library.agent.aries_rfc.feature_0015_ack.Ack
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.*
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.errors.StateMachineTerminatedWithError
import com.sirius.library.errors.indy_exceptions.WalletItemNotFoundException
import com.sirius.library.errors.sirius_exceptions.SiriusValidationError
import com.sirius.library.hub.Context
import com.sirius.library.hub.coprotocols.CoProtocolP2P
import com.sirius.library.messaging.Type
import com.sirius.library.utils.Base64
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger

class Holder(context: Context, issuer: Pairwise, masterSecretId: String?, locale: String?) :
    BaseIssuingStateMachine(context) {
    var log: Logger = Logger.getLogger(Holder::class.simpleName)
    var issuer: Pairwise
    var masterSecretId: String?
    var locale: String?

    constructor(context: Context, issuer: Pairwise, masterSecretId: String?) : this(
        context,
        issuer,
        masterSecretId,
        BaseIssueCredentialMessage.DEF_LOCALE
    ) {
    }

    fun accept(offer: OfferCredentialMessage, comment: String?): Pair<Boolean, String> {
        try {
            CoProtocolP2P(context, issuer, protocols(), timeToLiveSec).use { coprotocol ->
                val docUri: String = Type.fromStr(offer.getType()).getDocUri()
                try {
                    try {
                        offer.validate()
                    } catch (e: SiriusValidationError) {
                        throw StateMachineTerminatedWithError(REQUEST_NOT_ACCEPTED, e.message)
                    }
                    // Step-1: Process Issuer Offer
                    val (credRequest, credMetadata) = context.getAnonCreds().proverCreateCredentialReq(
                        issuer.me.did, offer.offer(), offer.credDef(), masterSecretId
                    )

                    // Step-2: Send request to Issuer
                    val requestMsg: RequestCredentialMessage =
                        RequestCredentialMessage.builder().setComment(comment).setLocale(locale)
                            .setCredRequest(credRequest).build()
                    val (_, second) = coprotocol.sendAndWait(requestMsg)
                    if (second !is IssueCredentialMessage) {
                        throw StateMachineTerminatedWithError(
                            REQUEST_NOT_ACCEPTED,
                            "Unexpected @type:" + second.getType()
                        )
                    }
                    val issueMsg: IssueCredentialMessage = second as IssueCredentialMessage
                    try {
                        issueMsg.validate()
                    } catch (e: SiriusValidationError) {
                        throw StateMachineTerminatedWithError(REQUEST_NOT_ACCEPTED, e.message)
                    }

                    // Step-3: Store credential
                    val credId =
                        storeCredential(credMetadata, issueMsg.cred(), offer.credDef(), null, issueMsg.credId())
                    storeMimeTypes(credId, offer.credentialPreview)
                    SchemasNonSecretStorage.storeCredSchemaNonSecret(context.nonSecrets, offer.schema())
                    SchemasNonSecretStorage.storeCredDefNonSecret(context.nonSecrets, offer.credDef())
                    val ack: Ack = Ack.builder().setStatus(Ack.Status.OK).setDocUri(docUri).build()
                    ack.setThreadId(issueMsg.getAckMessageId())
                    coprotocol.send(ack)
                    log.info("100% - Credential stored successfully")
                    return Pair(true, credId)
                } catch (ex: StateMachineTerminatedWithError) {
                    problemReport =
                        IssueProblemReport.builder().setProblemCode(ex.problemCode).setExplain(ex.explain)
                            .setDocUri(docUri).build()
                    log.info("100% - Terminated with error. " + ex.problemCode.toString() + " " + ex.explain)
                    if (ex.isNotify()) coprotocol.send(problemReport)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            log.info("100% - Terminated with error")
        }
        return Pair(false, "")
    }

    fun accept(offer: OfferCredentialMessage): Pair<Boolean, String> {
        return accept(offer, null)
    }

    private fun storeCredential(
        credMetadata: JSONObject,
        cred: JSONObject,
        credDef: JSONObject,
        revRegDef: String?,
        credId: String
    ): String {
        var credId = credId
        val credOrder: String?
        credOrder = try {
            context.getAnonCreds().proverGetCredential(credId)
        } catch (ex: WalletItemNotFoundException) {
            null
        }
        if (credOrder != null) {
            context.getAnonCreds().proverDeleteCredential(credId)
        }
        credId = context.getAnonCreds().proverStoreCredential(credId, credMetadata, cred, credDef, revRegDef)
        return credId
    }

    private fun storeMimeTypes(credId: String, preview: List<ProposedAttrib>) {
        if (!preview.isEmpty()) {
            val mimeTypes = JSONObject()
            for (attr in preview) {
                if (attr.has("mime-type")) {
                    mimeTypes.put(attr.optString("name"), attr.optString("mime-type"))
                }
            }
            context.nonSecrets.addWalletRecord(
                "mime-types", credId,
                String(
                    Base64.getEncoder()
                        .encode(mimeTypes.toString().toByteArray(java.nio.charset.StandardCharsets.UTF_8))
                )
            )
        }
    }

    companion object {
        fun getMimeTypes(c: Context, credId: String?): JSONObject {
            val record: String? =
                c.nonSecrets.getWalletRecord("mime-types", credId, RetrieveRecordOptions(true, true, false))
            if (record != null) {
                val rec = JSONObject(record)
                val b64: String = rec.optString("value") ?:""
                val vals = String(
                    Base64.getDecoder().decode(b64.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
                )
                return JSONObject(vals)
            }
            return JSONObject()
        }
    }

    init {
        this.context = context
        this.issuer = issuer
        this.masterSecretId = masterSecretId
        this.locale = locale
    }
}
