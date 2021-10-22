/*

package examples.covid

import com.sirius.library.agent.aries_rfc.feature_0037_present_proof.state_machines.Verifier
import com.sirius.library.agent.aries_rfc.feature_0095_basic_message.Message
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter
import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.agent.consensus.simple.messages.InitRequestLedgerMessage
import com.sirius.library.agent.consensus.simple.messages.ProposeTransactionsMessage
import com.sirius.library.agent.consensus.simple.state_machines.MicroLedgerSimpleConsensus
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.CloudHub
import com.sirius.library.hub.Context
import com.sirius.library.utils.JSONObject

class Airport(
    config: CloudHub.Config,
    var medCredInfo: CredInfo,
    var labDid: String,
    var boardingPassCredInfo: CredInfo,
    var aircompanyDid: String,
    var dkmsName: String
) :
    BaseParticipant(config, null, null, null) {
    fun enterToTerminal(): Pair<String, Invitation>? {
        CloudContext(config).also { context ->
            val connectionKey: String? = context.crypto.createKey()
            val myEndpoint: Endpoint? = context.endpointWithEmptyRoutingKeys
            if (myEndpoint == null) return null
            val invitation: Invitation =
                Invitation.builder().setLabel("Entering to the terminal").setRecipientKeys(listOfNotNull(connectionKey))
                    .setEndpoint(myEndpoint.address).build()
            val qrContent: String? = context.generateQrCode(invitation.invitationUrl())
            if(qrContent==null){
                return null
            }
            return Pair(qrContent, invitation)
        }
    }

    override fun routine() {
        try {
            CloudContext(config).also { c ->
                val listener: Listener? = c.subscribe()
                while (loop) {
                    val event: Event = listener?.one.get()
                    if (event.message() is InitRequestLedgerMessage) {
                        processInitMicroledger(c, event)
                    } else if (event.message() is ProposeTransactionsMessage) {
                        processNewCommit(c, event)
                    } else if (event.message() is ConnRequest) {
                        processCredVerification(c, event)
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun processNewCommit(c: Context, event: Event) {
        val machine = MicroLedgerSimpleConsensus(c, event.getPairwise()?.me)
        machine.acceptCommit(event.getPairwise(), event.message() as ProposeTransactionsMessage)
    }

    private fun processInitMicroledger(c: Context, event: Event) {
        val machine = MicroLedgerSimpleConsensus(c, event.getPairwise()?.me)
        val (first) = machine.acceptMicroledger(event.getPairwise(), event.message() as InitRequestLedgerMessage)
        if (first) {
            println("Microledger for airport created successfully")
        } else {
            println("Microledger for airport creation failed")
        }
    }

    private fun processCredVerification(c: Context, event: Event) {
        val request: ConnRequest = event.message() as ConnRequest
        val (first, second) = c.getDid().createAndStoreMyDid()
        val connectionKey: String? = event.recipientVerkey
        val myEndpoint: Endpoint? = c.endpointWithEmptyRoutingKeys

        if(connectionKey==null || myEndpoint == null) return
        val sm = Inviter(c, Pairwise.Me(first, second), connectionKey, myEndpoint)
        val pw: Pairwise = sm.createConnection(request) ?: return
        val proofRequest: JSONObject = JSONObject()
            .put("nonce", c.getAnonCreds().generateNonce()).put("name", "Verify false covid test").put("version", "1.0")
            .put(
                "requested_attributes", JSONObject()
                    .put(
                        "attr1_referent", JSONObject().put("name", "has_covid").put(
                            "restrictions", JSONObject()
                                .put(
                                    "issuer_did",
                                    labDid
                                )
                        )
                    ).put(
                        "attr2_referent", JSONObject().put("name", "flight").put(
                            "restrictions", JSONObject()
                                .put(
                                    "issuer_did",
                                    aircompanyDid
                                )
                        )
                    )
            )
        val verLedger: Ledger = c.ledgers?.get(dkmsName) ?: return
        val machine = Verifier(c, pw, verLedger)
        val ok: Boolean = machine.verify(
            Verifier.VerifyParams().setProofRequest(proofRequest).setComment("Verify covid test and boarding pass")
                .setProtocolVersion("1.0")
        )
        if (ok) {
            println(machine.getRequestedProof().toString())
            val hasCovid: Boolean =
                machine.getRequestedProof()?.getJSONObject("revealed_attrs")?.getJSONObject("attr1_referent")
                    ?.optString("raw").equals("true")
            if (hasCovid) {
                val hello: Message = Message.builder()
                    .setContent("Sorry, but we can't let your go to the terminal. Please, get rid of covid first!")
                    .setLocale("en").build()
                c.sendTo(hello, pw)
            } else {
                val hello: Message = Message.builder().setContent("Welcome on board!").setLocale("en").build()
                c.sendTo(hello, pw)
            }
        } else {
            println("verification failed")
        }
    }
}

*/
