package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractAnonCreds
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import java.util.concurrent.TimeUnit

actual class AnonCredsMobile actual constructor(val wallet: LocalWallet) : AbstractAnonCreds() {

    actual var timeoutSec : Long = 60
    override fun issuerCreateSchema(
        issuerDid: String?,
        name: String?,
        version: String?,
        attrs: List<String?>?
    ): Pair<String?, AnonCredSchema?> {
        try {
            val res: AnoncredsResults.IssuerCreateSchemaResult =
                Anoncreds.issuerCreateSchema(issuerDid, name, version, JSONArray(attrs).toString())
                    .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            val anonCredSchema = AnonCredSchema(res.getSchemaJson())
            return Pair(res.getSchemaId(), anonCredSchema)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Pair(null,null)
    }

    override fun issuerCreateAndStoreCredentialDef(
        issuerDid: String?,
        schema: Any?,
        tag: String?,
        signatureType: String?,
        config: Any?
    ): Pair<String?, String?> {
        try {
            val res: AnoncredsResults.IssuerCreateAndStoreCredentialDefResult =
                Anoncreds.issuerCreateAndStoreCredentialDef(
                    wallet,
                    issuerDid,
                    schema.toString(),
                    tag,
                    signatureType,
                    config.toString()
                ).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            return Pair(res.getCredDefId(), res.getCredDefJson())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Pair(null,null)
    }


    override fun issuerRotateCredentialDefStart(credDefId: String?, config: String?): String? {
        try {
            return Anoncreds.issuerRotateCredentialDefStart(wallet, credDefId, config)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun issuerRotateCredentialDefApply(credDefId: String?) {
        try {
            Anoncreds.issuerRotateCredentialDefApply(wallet, credDefId)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun issuerCreateAndStoreRevocReg(
        issuerDid: String?,
        revocDefType: String?,
        tag: String?,
        credDefId: String?,
        config: String?,
        tailsWriterHandle: Int
    ): Triple<String?, String?, String?> {
        return Triple(null,null,null)
    }

    override fun issuerCreateCredentialOffer(credDefId: String?): JSONObject? {
        try {
            return JSONObject(
                Anoncreds.issuerCreateCredentialOffer(wallet, credDefId)
                    .get(timeoutSec, TimeUnit.SECONDS)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }



override fun issuerCreateCredential(
    credOffer: JSONObject?,
    credReq: JSONObject?,
    credValues: JSONObject?,
    revRegId: String?,
    blobStorageReaderHandle: Int?
): Triple<JSONObject, String?, JSONObject?> {
        try {
            val res: AnoncredsResults.IssuerCreateCredentialResult = Anoncreds.issuerCreateCredential(
                wallet,
                credOffer.toString(),
                credReq.toString(),
                credValues.toString(),
                revRegId,
                blobStorageReaderHandle ?: 0
            ).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            return Triple(
                JSONObject(res.getCredentialJson()),
                res.getRevocId(),
                JSONObject(res.getRevocRegDeltaJson())
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Triple(JSONObject(),null, null)
    }

    override fun issuerRevokeCredential(blobStorageReaderHandle: Int?, revRegId: String?, credRevocId: String?): String? {
        return null
    }

    override fun issuerMergeRevocationRegistryDeltas(revRegDelta: String?, otherRevRegDelta: String?): String? {
        return null
    }

    override fun proverCreateMasterSecret(masterSecretName: String?): String? {
        try {
            return Anoncreds.proverCreateMasterSecret(wallet, masterSecretName)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            if(e.message!=null){
                if (!e.message!!.contains("DuplicateMasterSecretNameException")) e.printStackTrace()
            }else{
                e.printStackTrace()
            }

        }
        return null
    }


override fun proverCreateCredentialReq(
    proverDid: String?,
    credOffer: JSONObject?,
    credDef: JSONObject?,
    masterSecretId: String?
): Pair<JSONObject?, JSONObject?>? {
        try {
            val res: AnoncredsResults.ProverCreateCredentialRequestResult = Anoncreds.proverCreateCredentialReq(
                wallet,
                proverDid,
                credOffer.toString(),
                credDef.toString(),
                masterSecretId
            ).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            return Pair(
                JSONObject(res.getCredentialRequestJson()),
                JSONObject(res.getCredentialRequestMetadataJson())
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun proverSetCredentialAttrTagPolicy(credDefId: String?, taAttrs: String?, retroactive: Boolean) {}
    override fun proverGetCredentialAttrTagPolicy(credDefId: String?): String? {
        return null
    }



override fun proverStoreCredential(
    credId: String?,
    credReqMetadata: JSONObject?,
    cred: JSONObject?,
    credDef: JSONObject?,
    revReqDef: String?
): String? {
        try {
            return Anoncreds.proverStoreCredential(
                wallet,
                credId,
                credReqMetadata.toString(),
                cred.toString(),
                credDef.toString(),
                revReqDef
            ).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun proverGetCredential(credDefId: String?): String? {
        try {
            return Anoncreds.proverGetCredential(wallet, credDefId)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            if(e.message!=null){
                if (!e.message!!.contains("WalletItemNotFoundException")) e.printStackTrace()
            }else{
                e.printStackTrace()
            }

        }
        return null
    }

    override fun proverDeleteCredential(credId: String?) {
        try {
            Anoncreds.proverDeleteCredential(wallet, credId).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun proverGetCredentials(filters: String?): List<String>? {
        try {
            val creds: String =
                Anoncreds.proverGetCredentials(wallet, filters).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            val jArr: JSONArray = JSONArray(creds)
            val res: MutableList<String> = ArrayList<String>()
            for (o in jArr) {
                res.add(o.toString())
            }
            return res
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun proverSearchCredential(query: String?): List<String>? {
        return null
    }

    override fun proverGetCredentialsForProofReq(proofRequest: String?): String? {
        try {
            Anoncreds.proverGetCredentialsForProofReq(wallet, proofRequest)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }





override fun proverSearchCredentialsForProofReq(
    proofRequest: JSONObject?,
    extraQuery: String?,
    limitReferents: Int
): JSONObject? {
        try {
            val credSearch: CredentialsSearchForProofReq =
                CredentialsSearchForProofReq.open(wallet, proofRequest.toString(), extraQuery)
                    .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            return try {
                var requestedAttributes: JSONObject? = proofRequest?.optJSONObject("requested_attributes")
                requestedAttributes = if (requestedAttributes != null) requestedAttributes else JSONObject()
                var requestedPredicates: JSONObject? = proofRequest?.optJSONObject("requested_predicates")
                requestedPredicates = if (requestedPredicates != null) requestedPredicates else JSONObject()
                val result: JSONObject =
                    JSONObject().put("self_attested_attributes", JSONObject())
                        .put("requested_attributes", JSONObject())
                        .put("requested_predicates", JSONObject())
                for (attrReferent in requestedAttributes.keySet()) {
                    val credForAttrStr: String = credSearch.fetchNextCredentials(attrReferent, limitReferents)
                        .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
                    val credForAttr: JSONArray = JSONArray(credForAttrStr)
                    var collection: JSONArray? =
                        result.optJSONObject("requested_attributes")?.optJSONArray(attrReferent)
                    collection = if (collection != null) collection else JSONArray()
                    for (o in credForAttr) collection.put(o)
                    result.optJSONObject("requested_attributes")?.put(attrReferent, collection)
                }
                for (predicateReferent in requestedPredicates.keySet()) {
                    val credForPredStr: String = credSearch.fetchNextCredentials(predicateReferent, limitReferents)
                        .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
                    val credForPred: JSONArray = JSONArray(credForPredStr)
                    var collection: JSONArray? =
                        result?.optJSONObject("requested_predicates")?.optJSONArray(predicateReferent)
                    collection = if (collection != null) collection else JSONArray()
                    for (o in credForPred) collection.put(o)
                    result.optJSONObject("requested_predicates")?.put(predicateReferent, collection)
                }
                result
            } finally {
                credSearch.closeSearch().get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun proverCreateProof(
        proofReq: JSONObject?,
        requestedCredentials: JSONObject?,
        masterSecretName: String?,
        schemas: JSONObject?,
        credentialDefs: JSONObject?,
        revStates: JSONObject?
    ): JSONObject? {
        try {
            val logger = Logger.getLogger("PROVER")
            logger.logLongText("--proverCreateProof START--")
            logger.logLongText("proofReq.toString()="+proofReq.toString())
            logger.logLongText(" requestedCredentials.toString()="+ requestedCredentials.toString())
            logger.logLongText("   masterSEcret.toString()"+   masterSecretName)
            logger.logLongText("   schemas.toString()"+   schemas.toString())
            logger.logLongText(" credentialDefs.toString()"+   credentialDefs.toString())
            logger.logLongText(" revStates.toString()"+   revStates.toString())
            val resStr: String = Anoncreds.proverCreateProof(
                wallet,
                proofReq.toString(),
                requestedCredentials.toString(),
                masterSecretName,
                schemas.toString(),
                credentialDefs.toString(),
                revStates.toString()
            ).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            logger.logLongText("resStr"+   resStr)
            logger.logLongText("--proverCreateProof END--")
            return JSONObject(resStr)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun verifierVerifyProof(
        proofRequest: JSONObject?,
        proof: JSONObject?,
        schemas: JSONObject?,
        credentialDefs: JSONObject?,
        revRegDefs: JSONObject?,
        revRegs: JSONObject?
    ): Boolean {
        return false
    }

    override fun createRevocation(
        blobStorageReaderHandle: Int,
        revRegDef: String?,
        revRegDelta: String?,
        timestamp: Int,
        credRevId: String?
    ): String? {
        return null
    }

    override fun updateRevocationState(
        blobStorageReaderHandle: Int,
        revState: String?,
        revRegDef: String?,
        revRegDelta: String?,
        timestamp: Int,
        credRevId: String?
    ): String? {
        return null
    }

    override fun generateNonce(): String? {
        try {
            return Anoncreds.generateNonce().get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun toUnqualified(entity: String?): String? {
        try {
            return Anoncreds.toUnqualified(entity).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }


}