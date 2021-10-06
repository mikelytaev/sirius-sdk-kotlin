package com.examples.covid

import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.AttribTranslation
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.ProposedAttrib
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.state_machines.Issuer
import com.sirius.library.agent.aries_rfc.feature_0095_basic_message.Message
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter
import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.agent.consensus.simple.messages.InitRequestLedgerMessage
import com.sirius.library.agent.consensus.simple.messages.ProposeTransactionsMessage
import com.sirius.library.agent.consensus.simple.state_machines.MicroLedgerSimpleConsensus
import com.sirius.library.agent.ledger.CredentialDefinition
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.ledger.Schema
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.microledgers.Transaction
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.CloudHub
import com.sirius.library.hub.Context
import com.sirius.library.utils.UUID

class AirCompany(
    config: CloudHub.Config,
    pairwises: List<Pairwise>,
    covidMicroledgerName: String?,
    me: Pairwise.Me?,
    var boardingPassCredInfo: CredInfo
) :
    BaseParticipant(config, pairwises, covidMicroledgerName!!, me) {
    var boardingPasses: MutableMap<String, BoardingPass> =
        java.util.concurrent.ConcurrentHashMap<String, BoardingPass>()
    var aircompanyClientDids: MutableMap<String, String> = HashMap<String, String>()
    var covidPosNames: MutableSet<String> = HashSet<String>()
    fun register(boardingPass: BoardingPass): Pair<String, Invitation>? {
        CloudContext(config).also { context ->
            val connectionKey: String? = context.crypto.createKey()
            val myEndpoint: Endpoint? = context.endpointWithEmptyRoutingKeys
            if (myEndpoint == null) return null
            val invitation: Invitation =
                Invitation.builder().setLabel("Getting the boarding pass").setRecipientKeys(listOfNotNull(connectionKey))
                    .setEndpoint(myEndpoint.address).build()
            val qrContent: String = invitation.invitationUrl()
            val qrUrl: String? = context?.generateQrCode(qrContent)
            if (qrUrl == null) return null
            boardingPasses[connectionKey?:""] = boardingPass
            return Pair(qrUrl, invitation)
        }
    }

    override fun routine() {
        try {
            CloudContext(config).also { c ->
                val listener: Listener? = c.subscribe()
                while (loop) {
                    val event = listener?.one.get()
                    if (event.message() is InitRequestLedgerMessage) {
                        processInitMicroledger(c, event)
                    } else if (event.message() is ProposeTransactionsMessage) {
                        processNewCommit(c, event)
                    } else if (event.message() is ConnRequest) {
                        processBoardingPassRequest(c, event)
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun processNewCommit(c: Context, event: Event) {
        val propose: ProposeTransactionsMessage = event.message() as ProposeTransactionsMessage
        val machine = MicroLedgerSimpleConsensus(c, event.getPairwise().me)
        machine.acceptCommit(event.getPairwise(), propose)
        val trs: List<Transaction> = propose.transactions() ?: listOf()
        for (tr in trs) {
            val testRes = CovidTest(tr.getJSONObject("test_res"))
            if (testRes.hasCovid()) {
                covidPosNames.add(testRes.fullName)
                for (conn in boardingPasses.keys) {
                    val pass: BoardingPass? = boardingPasses[conn]
                    if (testRes.fullName.equals(pass?.getFullName())) {
                        val pw: Pairwise? = c.getPairwiseList().loadForDid(aircompanyClientDids[pass.getFullName()])
                        val hello: Message = Message.builder()
                            .setContent("We have to revoke your boarding pass due to positive covid test")
                            .setLocale("en").build()
                        c.sendTo(hello, pw)
                    }
                }
            } else {
                covidPosNames.remove(testRes.fullName)
            }
        }
    }

    private fun processInitMicroledger(c: Context, event: Event) {
        val machine = MicroLedgerSimpleConsensus(c, event.getPairwise().me)
        val (first) = machine.acceptMicroledger(event.getPairwise(), event.message() as InitRequestLedgerMessage)
        if (first) {
            println("Microledger for aircompany created successfully")
        } else {
            println("Microledger for aircompany creation failed")
        }
    }

    private fun processBoardingPassRequest(c: Context, event: Event) {
        val request: ConnRequest = event.message() as ConnRequest
        val (first, second) = c.getDid().createAndStoreMyDid()
        val connectionKey: String = event.recipientVerkey
        val myEndpoint: Endpoint = c.endpointWithEmptyRoutingKeys
        val sm = Inviter(c, Pairwise.Me(first, second), connectionKey, myEndpoint)
        val p2p: Pairwise = sm.createConnection(request)
        val boardingPass: BoardingPass? = boardingPasses[connectionKey]
        val hello: Message = Message.builder()
            .setContent("Dear " + boardingPass.getFullName().toString() + ", welcome to the registration!")
            .setLocale("en").build()
        c.sendTo(hello, p2p)
        if (covidPosNames.contains(boardingPass.getFullName())) {
            val reject: Message =
                Message.builder().setContent("Sorry, we can't issue the boarding pass. Get rid of covid first!")
                    .setLocale("en").build()
            c.sendTo(reject, p2p)
            return
        }
        val issuerMachine = Issuer(c, p2p, 60)
        val credId = "cred-id-" + UUID.randomUUID.toString()
        val preview: MutableList<ProposedAttrib> = ArrayList<ProposedAttrib>()
        for (key in boardingPass.keySet()) {
            preview.add(ProposedAttrib(key, boardingPass.get(key).toString()))
        }
        val ok: Boolean = issuerMachine.issue(
            Issuer.IssueParams().setValues(boardingPass).setSchema(
                boardingPassCredInfo.schema
            ).setCredDef(boardingPassCredInfo.credentialDefinition).setComment("Here is your boarding pass")
                .setPreview(preview).setTranslation(
                    translations
                ).setCredId(credId)
        )
        if (ok) {
            println("Boarding pass was successfully issued")
            c.getPairwiseList().create(p2p)
            aircompanyClientDids[boardingPass.getFullName()] = p2p.their.did
        } else {
            println("ERROR while issuing")
        }
    }

    companion object {
        var translations: List<AttribTranslation> = listOf(
            AttribTranslation("full_name", "Full Name"),
            AttribTranslation("flight", "Flight num."),
            AttribTranslation("departure", "Departure"),
            AttribTranslation("arrival", "arrival"),
            AttribTranslation("date", "date"),
            AttribTranslation("class", "class"),
            AttribTranslation("seat", "seat")
        )

        fun createBoardingPassCreds(issuer: Context, did: String?, dkmsName: String?): CredInfo? {
            val schemaName = "Boarding Pass"
            val (_, anoncredSchema) = issuer.getAnonCreds().issuerCreateSchema(
                did, schemaName, "1.0",
                "full_name", "flight", "departure", "arrival", "date", "class", "seat"
            )
            val ledger: Ledger? = issuer?.ledgers?.get(dkmsName)
            var schema: Schema? = ledger?.ensureSchemaExists(anoncredSchema, did)
            if (schema == null) {
                val (first, second) = ledger.registerSchema(anoncredSchema, did)
                schema = if (first) {
                    println("Boarding pass schema registered successfully")
                    second
                } else {
                    println("Boarding pass schema was not registered")
                    return null
                }
            } else {
                println("Boarding pass schema is exists in the ledger")
            }
            val (_, credDef) = ledger?.registerCredDef(CredentialDefinition("TAG", schema), did)
            val res = CredInfo()
            res.credentialDefinition = credDef
            res.schema = schema
            return res
        }
    }
}
