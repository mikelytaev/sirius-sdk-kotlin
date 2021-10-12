/*
package examples.covid

import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.AttribTranslation
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.ProposedAttrib
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.state_machines.Issuer
import com.sirius.library.agent.aries_rfc.feature_0095_basic_message.Message
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter
import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.agent.consensus.simple.messages.ProposeTransactionsMessage
import com.sirius.library.agent.consensus.simple.state_machines.MicroLedgerSimpleConsensus
import com.sirius.library.agent.ledger.CredentialDefinition
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.ledger.Schema
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.microledgers.AbstractMicroledger
import com.sirius.library.agent.microledgers.Transaction
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.CloudHub
import com.sirius.library.hub.Context
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID

class Laboratory(
    config: CloudHub.Config,
    pairwises: List<Pairwise>?,
    covidMicroledgerName: String,
    me: Pairwise.Me?,
    var medCredInfo: CredInfo
) :
    BaseParticipant(config, pairwises, covidMicroledgerName!!, me) {
    var testResults: MutableMap<String, CovidTest> = HashMap<String, CovidTest>()
    fun issueTestResults(testRes: CovidTest): Pair<String, Invitation>? {
        CloudContext(config).also { context ->
            val connectionKey: String? = context.crypto.createKey()
            val myEndpoint: Endpoint? = context.endpointWithEmptyRoutingKeys
            if (myEndpoint == null) return null
            val invitation: Invitation =
                Invitation.builder().setLabel("Invitation to connect with medical organization")
                    .setRecipientKeys(listOfNotNull(connectionKey)).setEndpoint(myEndpoint.address).build()
            val qrContent: String = invitation.invitationUrl()
            val qrUrl: String? = context.generateQrCode(qrContent)
            if (qrUrl == null) return null
            testResults[connectionKey?:""] = testRes
            return Pair(qrUrl, invitation)
        }
    }

    override fun routine() {
        try {
            CloudContext(config).also { c ->
                initMicroledger(c)
                val listener: Listener? = c.subscribe()
                while (loop) {
                    val event: Event = listener?.one.get()
                    if (event.message() is ProposeTransactionsMessage) {
                        processNewCommit(c, event)
                    } else if (event.message() is ConnRequest) {
                        processCovidTestRequest(c, event)
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun initMicroledger(c: Context) {
        if (!c.getMicrolegders().isExists(covidMicroledgerName)) {
            println("Initializing microledger consensus")
            val machine = MicroLedgerSimpleConsensus(c, me)
            val (first) = machine.initMicroledger(
                covidMicroledgerName,
                covidMicroledgerParticipants,
                listOf()
            )
            if (first) {
                println("Consensus successfully initialized")
            } else {
                println("Consensus initialization failed!")
            }
        }
    }

    private fun processNewCommit(c: Context, event: Event) {
        val machine = MicroLedgerSimpleConsensus(c, event.getPairwise().me)
        machine.acceptCommit(event.getPairwise(), event.message() as ProposeTransactionsMessage)
    }

    private fun processCovidTestRequest(c: Context, event: Event) {
        val request: ConnRequest = event.message() as ConnRequest
        val (first, second) = c.getDid().createAndStoreMyDid()
        val connectionKey: String? = event.recipientVerkey
        val myEndpoint: Endpoint? = c.endpointWithEmptyRoutingKeys
        val sm = Inviter(c, Pairwise.Me(first, second), connectionKey, myEndpoint)
        val p2p: Pairwise? = sm.createConnection(request)
        val hello: Message = Message.builder().setContent("Welcome to the covid laboratory!").setLocale("en").build()
        c.sendTo(hello, p2p)
        val issuerMachine = Issuer(c, p2p, 60)
        val credId = "cred-id-" + UUID.randomUUID.toString()
        val preview: MutableList<ProposedAttrib> = ArrayList<ProposedAttrib>()
        val testRes = testResults[connectionKey]
        for (key in testRes!!.keySet()) {
            preview.add(ProposedAttrib(key, testRes.get(key).toString()))
        }
        val ok: Boolean = issuerMachine.issue(
            Issuer.IssueParams().setCredId(credId).setTranslation(translations).setPreview(preview).setValues(testRes)
                .setSchema(
                    medCredInfo.schema
                ).setCredDef(medCredInfo.credentialDefinition).setComment("Here is your covid test results")
        )
        if (ok) {
            println("Covid test confirmation was successfully issued")
            if (testRes.hasCovid()) {
                val ledger: AbstractMicroledger? = c.getMicrolegders().getLedger(covidMicroledgerName)
                val machine = MicroLedgerSimpleConsensus(c, me)
                val tr = Transaction(JSONObject().put("test_res", testRes))
                machine.commit(ledger, covidMicroledgerParticipants, listOf(tr))
            }
        } else {
            println("ERROR while issuing")
        }
    }

    companion object {
        var translations: List<AttribTranslation> = listOf(
            AttribTranslation("full_name", "Patient Full Name"),
            AttribTranslation("location", "Patient location"),
            AttribTranslation("bio_location", "Biomaterial sampling point"),
            AttribTranslation("timestamp", "Timestamp"),
            AttribTranslation("approved", "Laboratory specialist"),
            AttribTranslation("has_covid", "Covid test result")
        )

        fun createMedCreds(issuer: Context, did: String?, dkmsName: String?): CredInfo? {
            val schemaName = "Covid test result 2"
            val (_, anoncredSchema) = issuer.getAnonCreds().issuerCreateSchema(
                did, schemaName, "1.0",
                "approved", "timestamp", "bio_location", "location", "full_name", "has_covid"
            )
            val ledger: Ledger? = issuer.ledgers.get(dkmsName)
            var schema: Schema? = ledger.ensureSchemaExists(anoncredSchema, did)
            if (schema == null) {
                val (first, second) = ledger.registerSchema(anoncredSchema, did)
                schema = if (first) {
                    println("Covid test result registered successfully")
                    second
                } else {
                    println("Covid test result was not registered")
                    return null
                }
            } else {
                println("Med schema is exists in the ledger")
            }
            val (_, credDef) = ledger?.registerCredDef(CredentialDefinition("TAG", schema), did)
            val res = CredInfo()
            res.credentialDefinition = credDef
            res.schema = schema
            return res
        }
    }
}
*/
