/*
package com.examples.propose_credential

import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.ProposeCredentialMessage
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.ProposedAttrib
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.state_machines.Issuer
import com.sirius.library.agent.aries_rfc.feature_0095_basic_message.Message
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter
import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.agent.ledger.CredentialDefinition
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.ledger.Schema
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.Context
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID
import kotlin.jvm.JvmStatic

object Main {
    var serverUri = "https://demo.socialsirius.com"
    var credentials: ByteArray =
        "ez8ucxfrTiV1hPX99MHt/C/MUJCo8OmN4AMVmddE/sew8gBzsOg040FWBSXzHd9hDoj5B5KN4aaLiyzTqkrbD3uaeSwmvxVsqkC0xl5dtIc=".encodeToByteArray()
    var p2PConnection: P2PConnection = P2PConnection(
        "6QvQ3Y5pPMGNgzvs86N3AQo98pF5WrzM1h6WkKH3dL7f",
        "28Au6YoU7oPt6YLpbWkzFryhaQbfAcca9KxZEmz22jJaZoKqABc4UJ9vDjNTtmKSn2Axfu8sT52f5Stmt7JD4zzh",
        "6oczQNLU7bSBzVojkGsfAv3CbXagx7QLUL7Yj1Nba9iw"
    )
    var publicDid = "Th7MpTaRZVRYnPiabds81Y"
    const val DKMS_NAME = "test_network"
    fun qrCode(context: Context): Pair<String, String>? {
        val connectionKey: String? = context.crypto.createKey()
        // Теперь сформируем приглашение для других через 0160
        // шаг 1 - определимся какой endpoint мы возьмем, для простоты возьмем endpoint без доп шифрования
        val endpoints: List<Endpoint> = context.endpoints
        var myEndpoint: Endpoint? = null
        for (e in endpoints) {
            if (e.routingKeys.isEmpty()) {
                myEndpoint = e
                break
            }
        }
        if (myEndpoint == null) return null
        // шаг 2 - создаем приглашение
        val invitation: Invitation =
            Invitation.builder().setLabel("0036 propose-credential test").setRecipientKeys(listOfNotNull(connectionKey))
                .setEndpoint(myEndpoint.address).build()

        // шаг 3 - согласно Aries-0160 генерируем URL
        val qrContent: String = invitation.invitationUrl()

        // шаг 4 - создаем QR
        val qrUrl: String = context.generateQrCode(qrContent) ?: return null
        return Pair(connectionKey, qrUrl)
    }

    fun regCreds(issuer: Context, did: String?, dkmsName: String?): CredInfo? {
        val schemaName = "passport"
        val (_, anoncredSchema) = issuer.getAnonCreds().issuerCreateSchema(
            did, schemaName, "1.0",
            "name", "age", "photo"
        )
        val ledger: Ledger = issuer.getLedgers().get(dkmsName)
        var schema: Schema = ledger.ensureSchemaExists(anoncredSchema, did)
        if (schema == null) {
            val (first, second) = ledger.registerSchema(anoncredSchema, did)
            schema = if (first) {
                println("Schema was registered successfully")
                second
            } else {
                println("Schema was not registered")
                return null
            }
        } else {
            println("Schema is already exists in the ledger")
        }
        val (_, credDef) = ledger.registerCredDef(CredentialDefinition("TAG", schema), did)
        val res = CredInfo()
        res.credentialDefinition = credDef
        res.schema = schema
        return res
    }

    @Throws(java.util.concurrent.ExecutionException::class, java.lang.InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        CloudContext.builder().setServerUri(serverUri).setCredentials(credentials).setP2p(p2PConnection).build()
            .use { context ->
                val credInfo: CredInfo? = regCreds(context, publicDid, DKMS_NAME)
                val qrCodeRes = qrCode(context)
                val connectionKey = qrCodeRes!!.first
                val qrUrl = qrCodeRes.second
                println("Открой QR код и просканируй в Sirius App: $qrUrl")
                val (myDid, myVerkey) = context.getDid().createAndStoreMyDid()
                val endpoints: List<Endpoint> = context.getEndpoints()
                var myEndpoint: Endpoint? = null
                for (e in endpoints) {
                    if (e.getRoutingKeys().isEmpty()) {
                        myEndpoint = e
                        break
                    }
                }
                if (myEndpoint == null) return
                // Слушаем запросы
                println("Слушаем запросы")
                val listener: Listener = context.subscribe()
                var p2p: Pairwise? = null
                while (true) {
                    val event: Event = listener.getOne().get()
                    println("received: " + event.message().getMessageObj().toString())
                    if (event.getRecipientVerkey().equals(connectionKey) && event.message() is ConnRequest) {
                        println("ConnRequest received")
                        val request: ConnRequest = event.message() as ConnRequest
                        val sm = Inviter(context, Pairwise.Me(myDid, myVerkey), connectionKey, myEndpoint)
                        p2p = sm.createConnection(request)
                        if (p2p != null) {
                            // Ensure pairwise is stored
                            context.getPairwiseList().ensureExists(p2p)
                            val hello: Message =
                                Message.builder().setContent("Waiting for your credential propose").setLocale("en")
                                    .build()
                            context.sendTo(hello, p2p)
                        }
                    }
                    if (event.getRecipientVerkey()
                            .equals(connectionKey) && event.message() is ProposeCredentialMessage
                    ) {
                        println("ProposeCredentialMessage received")
                        if (p2p == null) {
                            println("Connection not established")
                            return
                        }
                        val propose: ProposeCredentialMessage = event.message() as ProposeCredentialMessage
                        if (!propose.getIssuerDid().equals(publicDid)) {
                            println("Wrong did")
                        }
                        if (!propose.getCredDefId().equals(credInfo.credentialDefinition.getId())) {
                            println("Wrong credDefId")
                        }
                        if (!propose.getSchemaId().equals(credInfo.schema.getId())) {
                            println("Wrong schemaId")
                        }
                        if (!propose.getSchemaIssuerDid().equals(publicDid)) {
                            println("Wrong schemaIssuerDid")
                        }
                        val proposedAttribs: List<ProposedAttrib> = propose.getCredentialProposal()
                        val vals: JSONObject = JSONObject()
                        for (attr in proposedAttribs) {
                            vals.put(attr.optString("name"), attr.get("value"))
                        }
                        val issuerMachine = Issuer(context, p2p, 60)
                        val credId = "cred-id-" + UUID.randomUUID.toString()
                        val ok: Boolean = issuerMachine.issue(
                            Issuer.IssueParams().setValues(vals).setSchema(credInfo.schema)
                                .setCredDef(credInfo.credentialDefinition).setComment("Here is your passport")
                                .setPreview(proposedAttribs).setCredId(credId)
                        )
                        if (ok) {
                            println("Pasport was successfully issued")
                        } else {
                            println("ERROR while issuing")
                        }
                    }
                }
            }
    }
}
*/
