package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.coprotocols.ThreadBasedCoProtocolTransport
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.helpers.ConfTest

class TestPerformance {
    var confTest: ConfTest? = null
   // @Before
    fun configureTest() {
        confTest = ConfTest.newInstance()
        // confTest.getSuiteSingleton().
    }

    fun routineForPinger(agent: CloudAgent, p: Pairwise, threadId: String) {
        val transport: ThreadBasedCoProtocolTransport? = agent.spawn(threadId, p)
    } /*   async def routine_for_pinger(agent: Agent, p: Pairwise, thread_id: str):
    transport = await agent.spawn(thread_id, p)
    await transport.start()
    try:
            for n in range(TEST_ITERATIONS):
    ping = Message({
        '@id': 'message-id-' + uuid.uuid4().hex,
                '@type': 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/test/1.0/ping',
                "comment": "Hi",
    })
    ok, pong = await transport.switch(ping)
            assert ok
            assert pong['@id'] == ping['@id']
            finally:
    await transport.stop()*/
}
