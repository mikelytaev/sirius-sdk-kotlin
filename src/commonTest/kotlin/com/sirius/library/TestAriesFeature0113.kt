package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.aries_rfc.feature_0113_question_answer.messages.AnswerMessage
import com.sirius.library.agent.aries_rfc.feature_0113_question_answer.messages.QuestionMessage
import com.sirius.library.agent.aries_rfc.feature_0113_question_answer.messages.Recipes
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.helpers.ConfTest
import com.sirius.library.hub.CloudContext
import com.sirius.library.models.AgentParams
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestAriesFeature0113 {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }

    @Test
    @Throws(Exception::class)
    fun testSane() {
        val requesterAgent: CloudAgent = confTest.getAgent("agent1")
        val responderAgent: CloudAgent = confTest.getAgent("agent2")
        requesterAgent.open()
        responderAgent.open()
        val requester2responder: Pairwise = confTest.getPairwise(requesterAgent, responderAgent)
        val responder2requester: Pairwise = confTest.getPairwise(responderAgent, requesterAgent)
        val requesterParams: AgentParams = confTest.suiteSingleton.getAgentParams("agent1")
        val requesterThread: java.lang.Thread = java.lang.Thread(java.lang.Runnable {
            CloudContext.builder().setServerUri(requesterParams.serverAddress)
                .setCredentials(requesterParams.credentials.encodeToByteArray())
                .setP2p(requesterParams.getConnection()).build().also { context ->
                    val question: QuestionMessage =
                        QuestionMessage.builder().setValidResponses(listOf("Yes", "No"))
                            .setQuestionText("Test question").setQuestionDetail("Question detail").setTtl(40).build()
                    val answer: AnswerMessage? = Recipes.askAndWaitAnswer(context, question, requester2responder)
                    assertNotNull(answer)
                    assertEquals(answer.response, "Yes")
                }
        })
        requesterThread.start()
        java.lang.Thread.sleep(100)
        val responderParams: AgentParams = confTest.suiteSingleton.getAgentParams("agent2")
        val responderThread: java.lang.Thread = java.lang.Thread(label@ java.lang.Runnable {
            try {
                CloudContext.builder().setServerUri(responderParams.serverAddress).setCredentials(
                    responderParams.credentials.encodeToByteArray()
                ).setP2p(responderParams.getConnection()).build().also { context ->
                    val listener: Listener? = context.subscribe()
                    while (true) {
                        val e: Event = listener?.one.get(60, java.util.concurrent.TimeUnit.SECONDS)
                        if (e.message() is QuestionMessage) {
                            val question: QuestionMessage = e.message() as QuestionMessage
                            Recipes.makeAnswer(context, "Yes", question, e.getPairwise())
                            return@label
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        responderThread.start()
        requesterThread.join(60000)
        responderThread.join(60000)
    }
}
