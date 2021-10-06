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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.fail

class TestAriesFeature0160 {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }

    @Test
    @Throws(
        java.lang.InterruptedException::class,
        java.util.concurrent.ExecutionException::class,
        java.util.concurrent.TimeoutException::class
    )
    fun testEstablishConnection() {
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val inviter: AgentParams = testSuite.getAgentParams("agent1")
        val invitee: AgentParams = testSuite.getAgentParams("agent2")

        // Get endpoints
        var connectionKey: String? = null
        var invitation: Invitation? = null
        CloudContext.builder().setServerUri(inviter.getServerAddress())
            .setCredentials(inviter.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8))
            .setP2p(inviter.getConnection()).build().use { context ->
                val inviterEndpointAddress: String = context.getEndpointAddressWithEmptyRoutingKeys()
                connectionKey = context.crypto.createKey()
                invitation = Invitation.builder().setLabel("Inviter").setEndpoint(inviterEndpointAddress)
                    .setRecipientKeys(listOf(connectionKey)).build()
            }

        // Init Me
        var inviterMe: Pairwise.Me? = null
        CloudContext.builder().setServerUri(inviter.getServerAddress())
            .setCredentials(inviter.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8))
            .setP2p(inviter.getConnection()).build().use { context ->
                val (first, second) = context.getDid().createAndStoreMyDid()
                inviterMe = Pairwise.Me(first, second)
            }
        var inviteeMe: Pairwise.Me? = null
        CloudContext.builder().setServerUri(invitee.getServerAddress())
            .setCredentials(invitee.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8))
            .setP2p(invitee.getConnection()).build().use { context ->
                val (first, second) = context.getDid().createAndStoreMyDid()
                inviteeMe = Pairwise.Me(first, second)
            }
        val finalConnectionKey = connectionKey
        val finalInviterMe: Pairwise.Me? = inviterMe
        val runInviterFeature: java.util.concurrent.CompletableFuture<Boolean> =
            java.util.concurrent.CompletableFuture.supplyAsync<Boolean>(
                java.util.function.Supplier<Boolean> {
                    CloudContext.builder().setServerUri(inviter.getServerAddress())
                        .setCredentials(inviter.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8))
                        .setP2p(inviter.getConnection()).build().use { context ->
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
                    CloudContext.builder().setServerUri(invitee.getServerAddress())
                        .setCredentials(invitee.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8))
                        .setP2p(invitee.getConnection()).build().use { context ->
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
        runInviteeFeature.get(60, java.util.concurrent.TimeUnit.SECONDS)
    }

    companion object {
        fun runInviter(
            context: Context, expectedConnectionKey: String?,
            me: Pairwise.Me?
        ) {
            try {
                val myEndpoint: Endpoint = context.getEndpointWithEmptyRoutingKeys()
                val listener: Listener = context.subscribe()
                val event: Event = listener.getOne().get(30, java.util.concurrent.TimeUnit.SECONDS)
                if (expectedConnectionKey == event.getRecipientVerkey()) {
                    if (event.message() is ConnRequest) {
                        val request: ConnRequest = event.message() as ConnRequest
                        val machine = Inviter(context, me, expectedConnectionKey, myEndpoint)
                        val pairwise: Pairwise = machine.createConnection(request)
                        assertNotEquals(null, pairwise)
                        context.getPairwiseList().ensureExists(pairwise)
                    } else {
                        fail("Wrong request message type")
                    }
                }
            } catch (e: java.lang.InterruptedException) {
                e.printStackTrace()
                fail()
            } catch (e: java.util.concurrent.ExecutionException) {
                e.printStackTrace()
                fail()
            } catch (e: java.util.concurrent.TimeoutException) {
                e.printStackTrace()
                fail()
            }
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
                context.getPairwiseList().ensureExists(pairwise)
            }
        }
    }
}
