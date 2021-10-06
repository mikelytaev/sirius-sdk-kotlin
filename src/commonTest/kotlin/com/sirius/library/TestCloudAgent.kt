package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.model.Entity
import com.sirius.library.helpers.ConfTest
import com.sirius.library.helpers.ServerTestSuite
import com.sirius.library.models.AgentParams
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestCloudAgent {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }

    //TODO do all tests
    @Test
    fun testAllAgentsPing() {
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val allAgentsList: MutableList<String> = ArrayList<String>()
        allAgentsList.add("agent1")
        allAgentsList.add("agent2")
        allAgentsList.add("agent3")
        allAgentsList.add("agent4")
        for (i in allAgentsList.indices) {
            val agentName = allAgentsList[i]
            val params: AgentParams = testSuite.getAgentParams(agentName)
            val agent = CloudAgent(
                params.getServerAddress(), params.getCredentials().getBytes(java.nio.charset.StandardCharsets.US_ASCII),
                params.getConnection(), 10
            )
            agent.open()
            val isPinged: Boolean = agent.ping()
            assertTrue(isPinged)
            agent.close()
        }
    }

    @Test
    fun testAgentsWallet() {
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val params: AgentParams = testSuite.getAgentParams("agent1")
        val agent = CloudAgent(
            params.serverAddress, params.credentials.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
            params.getConnection(), 10
        )
        agent.open()
        //Check wallet calls is ok
        val didVerkey: Pair<String, String> = agent.getWallet().did.createAndStoreMyDid()
        assertNotNull(didVerkey)
        assertNotNull(didVerkey.first)
        assertNotNull(didVerkey.second)
        agent.close()
    }

    @Test
    @Throws(
        java.lang.InterruptedException::class,
        java.util.concurrent.ExecutionException::class,
        java.util.concurrent.TimeoutException::class,
        SiriusRPCError::class
    )
    fun testAgentsCommunications() {
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val agent1params: AgentParams = testSuite.getAgentParams("agent1")
        val agent2params: AgentParams = testSuite.getAgentParams("agent2")
        val entityList1: List<Entity> = agent1params.getEntitiesList()
        val entityList2: List<Entity> = agent2params.getEntitiesList()
        val entity1: Entity = entityList1[0]
        val entity2: Entity = entityList2[0]
        val agent1 = CloudAgent(
            agent1params.serverAddress,
            agent1params.credentials.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
            agent1params.getConnection(),
            10
        )
        val agent2 = CloudAgent(
            agent2params.serverAddress,
            agent2params.credentials.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
            agent2params.getConnection(),
            10
        )
        agent1.open()
        agent2.open()
        //Get endpoints
        var agent2Endpoint = ""
        for (e in agent2.getEndpoints()) {
            if (e.routingKeys.size === 0) {
                agent2Endpoint = e.address
                break
            }
        }
        agent1.getWallet().getDid().storeTheirDid(entity2.getDid(), entity2.getVerkey())
        if (!agent1.getWallet().getPairwise().isPairwiseExist(entity2.getDid())) {
            println("#1")
            agent1.getWallet().getPairwise().createPairwise(entity2.getDid(), entity1.getDid())
        }
        agent2.getWallet().getDid().storeTheirDid(entity1.getDid(), entity1.getVerkey())
        if (!agent2.getWallet().getPairwise().isPairwiseExist(entity1.getDid())) {
            println("#2")
            agent2.getWallet().getPairwise().createPairwise(entity1.getDid(), entity2.getDid())
        }
        //Prepare Message
        val trustPing = Message(
            JSONObject().put("@type", "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/ping")
                .put("@id", "trust-ping-message" + UUID.randomUUID.hashCode())
                .put("comment", "Hi. Are you listening?").put("response_requested", true)
        )
        val thierVerkeys: MutableList<String> = ArrayList<String>()
        thierVerkeys.add(entity2.getVerkey())
        val finalAgent2Endpoint = agent2Endpoint
        val agent2Listener: Listener = agent2.subscribe()
        val eventFeat: java.util.concurrent.CompletableFuture<Event> = agent2Listener.getOne()
        println("sendMess1=")
        agent1.sendMessage(trustPing, thierVerkeys, finalAgent2Endpoint, entity1.getVerkey(), ArrayList<E>())
        val event: Event = eventFeat.get(10, java.util.concurrent.TimeUnit.SECONDS)
        println("event=" + event.getMessageObj())
        val message: JSONObject = event.getJSONOBJECTFromJSON("message")
        assertNotNull(message)
        val type: String = message.getString("@type")
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/ping", type)
        val id: String = message.getString("@id")
        assertEquals(trustPing.getId(), id)
        agent1.close()
        agent2.close()
    }

    @Test
    @Throws(
        java.lang.InterruptedException::class,
        java.util.concurrent.ExecutionException::class,
        java.util.concurrent.TimeoutException::class,
        SiriusRPCError::class
    )
    fun testListenerRestoreMessage() {
        val agent1Params: AgentParams = confTest.suiteSingleton.getAgentParams("agent1")
        val agent2Params: AgentParams = confTest.getSuiteSingleton().getAgentParams("agent2")
        val agent1ParamsEntitiesList: List<Entity> = agent1Params.getEntitiesList()
        val agent2ParamsEntitiesList: List<Entity> = agent2Params.getEntitiesList()
        val entity1: Entity = agent1ParamsEntitiesList[0]
        val entity2: Entity = agent2ParamsEntitiesList[0]
        val agent1 = CloudAgent(
            agent1Params.getServerAddress(),
            agent1Params.getCredentials().getBytes(java.nio.charset.StandardCharsets.US_ASCII),
            agent1Params.getConnection(),
            10
        )
        val agent2 = CloudAgent(
            agent2Params.getServerAddress(),
            agent2Params.getCredentials().getBytes(java.nio.charset.StandardCharsets.US_ASCII),
            agent2Params.getConnection(),
            10
        )
        agent1.open()
        agent2.open()

        //GET endpoints
        var agent2Endpoint: String? = null
        for (i in 0 until agent2.getEndpoints().size()) {
            if (agent2.getEndpoints().get(i).getRoutingKeys().isEmpty()) {
                agent2Endpoint = agent2.getEndpoints().get(i).getAddress()
            }
        }
        val agent2Listener: Listener = agent2.subscribe()

        //# Exchange Pairwise
        agent1.getWallet().getDid().storeTheirDid(entity2.getDid(), entity2.getVerkey())
        val isExist1: Boolean = agent1.getWallet().getPairwise().isPairwiseExist(entity2.getDid())
        if (!isExist1) {
            println("#1")
            agent1.getWallet().getPairwise().createPairwise(entity2.getDid(), entity1.getDid())
        }
        agent2.getWallet().getDid().storeTheirDid(entity1.getDid(), entity1.getVerkey())
        val isExist2: Boolean = agent2.getWallet().getPairwise().isPairwiseExist(entity1.getDid())
        if (!isExist2) {
            println("#2")
            agent2.getWallet().getPairwise().createPairwise(entity1.getDid(), entity2.getDid())
        }

        //Bind Message class to protocol
        Message.registerMessageClass(TrustPingMessageUnderTest::class.java, "trust_ping_test")
        //Prepare message
        val trust_ping = Message(
            JSONObject().put("@type", "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping_test/1.0/ping")
                .put("@id", "trust-ping-message" + UUID.randomUUID.hashCode())
                .put("comment", "Hi. Are you listening?").put("response_requested", true)
        )
        val verkeyList: MutableList<String> = ArrayList<String>()
        verkeyList.add(entity2.getVerkey())
        val eventFeat: java.util.concurrent.CompletableFuture<Event> = agent2Listener.getOne()
        agent1.sendMessage(trust_ping, verkeyList, agent2Endpoint, entity1.getVerkey(), ArrayList<E>())
        val event: Event = eventFeat.get(10, java.util.concurrent.TimeUnit.SECONDS)
        val message: JSONObject = event.getJSONOBJECTFromJSON("message")
        println("message=$message")
        // assert isinstance(msg, TrustPingMessageUnderTest), 'Unexpected msg type: ' + str(type(msg))
        agent1.close()
        agent2.close()
    }
}
