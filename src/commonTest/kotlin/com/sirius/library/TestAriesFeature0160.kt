package com.sirius.library

import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Invitee
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter
import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.helpers.ConfTest
import com.sirius.library.helpers.ServerTestSuite
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.Context
import com.sirius.library.models.AgentParams
import kotlin.test.*

class TestAriesFeature0160 {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }

    @Test
    fun testEstablishConnection() {
        /*val testSuite: ServerTestSuite = confTest.suiteSingleton
        val inviter: AgentParams = testSuite.getAgentParams("agent1")
        val invitee: AgentParams = testSuite.getAgentParams("agent2")

        // Get endpoints
        var connectionKey: String? = null
        var invitation: Invitation? = null
        CloudContext.builder().setServerUri(inviter.serverAddress)
            .setCredentials(inviter.credentials.encodeToByteArray())
            .setP2p(inviter.getConnection()).build().also { context ->
                val inviterEndpointAddress: String = context.endpointAddressWithEmptyRoutingKeys
                connectionKey = context.crypto.createKey()
                invitation = Invitation.builder().setLabel("Inviter").setEndpoint(inviterEndpointAddress)
                    .setRecipientKeys(listOfNotNull(connectionKey)).build()
            }

        // Init Me
        var inviterMe: Pairwise.Me? = null
        CloudContext.builder().setServerUri(inviter.serverAddress)
            .setCredentials(inviter.credentials.encodeToByteArray())
            .setP2p(inviter.getConnection()).build().also { context ->
                val (first, second) = context.getDidi().createAndStoreMyDid()
                inviterMe = Pairwise.Me(first, second)
            }
        var inviteeMe: Pairwise.Me? = null
        CloudContext.builder().setServerUri(invitee.serverAddress)
            .setCredentials(invitee.credentials.encodeToByteArray())
            .setP2p(invitee.getConnection()).build().also { context ->
                val (first, second) = context.getDidi().createAndStoreMyDid()
                inviteeMe = Pairwise.Me(first, second)
            }
        val finalConnectionKey = connectionKey
        val finalInviterMe: Pairwise.Me? = inviterMe
        assertNotNull(finalConnectionKey)
        assertNotNull(finalInviterMe)
        val runInviterFeature: java.util.concurrent.CompletableFuture<Boolean> =
            java.util.concurrent.CompletableFuture.supplyAsync<Boolean>(
                java.util.function.Supplier<Boolean> {
                    CloudContext.builder().setServerUri(inviter.serverAddress)
                        .setCredentials(inviter.credentials.encodeToByteArray())
                        .setP2p(inviter.getConnection()).build().also { context ->
                            runInviter(
                                context,
                                finalConnectionKey,
                                finalInviterMe
                            )
                        }
                    true
                },
                java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
        val finalInvitation: Invitation? = invitation
        val finalInviteeMe: Pairwise.Me? = inviteeMe
        val runInviteeFeature: java.util.concurrent.CompletableFuture<Boolean> =
            java.util.concurrent.CompletableFuture.supplyAsync<Boolean>(
                java.util.function.Supplier<Boolean> {
                    try {
                        java.lang.Thread.sleep(10)
                    } catch (e: java.lang.InterruptedException) {
                        e.printStackTrace()
                        fail()
                    }
                    CloudContext.builder().setServerUri(invitee.serverAddress)
                        .setCredentials(invitee.credentials.encodeToByteArray())
                        .setP2p(invitee.getConnection()).build().also { context ->
                            runInvitee(
                                context,
                                finalInvitation,
                                "Invitee",
                                finalInviteeMe
                            )
                        }
                    true
                },
                java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
        runInviterFeature.get(60, java.util.concurrent.TimeUnit.SECONDS)
        runInviteeFeature.get(60, java.util.concurrent.TimeUnit.SECONDS)*/
    }

    companion object {
        fun runInviter(
            context: Context, expectedConnectionKey: String,
            me: Pairwise.Me
        ) {
           /* try {
                val myEndpoint: Endpoint? = context.endpointWithEmptyRoutingKeys
                val listener: Listener? = context.subscribe()
                val event: Event = listener?.one.get(30, java.util.concurrent.TimeUnit.SECONDS)
                if (expectedConnectionKey == event.recipientVerkey) {
                    if (event.message() is ConnRequest) {
                        val request: ConnRequest = event.message() as ConnRequest
                        assertNotNull( myEndpoint)
                        val machine = Inviter(context, me, expectedConnectionKey, myEndpoint)
                        val pairwise: Pairwise? = machine.createConnection(request)
                        assertNotNull( pairwise)
                        context.getPairwiseListi().ensureExists(pairwise)
                    } else {
                        fail("Wrong request message type")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                fail()
            }*/
        }

        fun runInvitee(
            context: Context, invitation: Invitation,
            myLabel: String?, me: Pairwise.Me
        ) {
            val myEndpoint: Endpoint? = context.endpointWithEmptyRoutingKeys
            // Create and start machine
            if(myEndpoint==null){
                fail()
            }
            val machine = Invitee(context, me, myEndpoint)
            val pairwise: Pairwise? = machine.createConnection(invitation, myLabel)
            assertNotEquals(null, pairwise)
            pairwise?.let {
                context.getPairwiseListi().ensureExists(pairwise)
            }
        }
    }
}
