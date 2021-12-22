package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.helpers.ConfTest
import com.sirius.library.helpers.ServerTestSuite
import com.sirius.library.models.AgentParams
import com.sirius.library.utils.UUID
import kotlin.test.*

class TestLocks {
   lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }


    @Test
    @Throws(Exception::class)
    fun testSameAccount() {
        val testSuite: ServerTestSuite = confTest.suiteSingleton

        val params: AgentParams = testSuite.getAgentParams("agent1")
        val session1 = CloudAgent(
            params.serverAddress,
            params.credentials.encodeToByteArray(),
            params.getConnectioni(),
            5
        )
        val session2 = CloudAgent(
            params.serverAddress,
            params.credentials.encodeToByteArray(),
            params.getConnectioni(),
            5
        )
        session1.open()
        session2.open()
        try {
            // check locking OK
            var resources = generateRandomResources(100)
            var okBusy: Pair<Boolean?, List<String?>?> = session1.acquire(resources, 5)
            try {
                assertTrue(okBusy.first == true)
                okBusy = session2.acquire(resources, 1)
                assertFalse(okBusy.first)
                assertEquals(
                    HashSet<String>(okBusy.second),
                    HashSet<String>(resources)
                )
            } finally {
                session1.release()
            }

            // check session ok may lock after explicitly release
            okBusy = session2.acquire(resources, 1)
            assertTrue(okBusy.first)
            // Check after timeout
            resources = generateRandomResources(100)
            val timeoutSec = 5
            okBusy = session1.acquire(resources, timeoutSec)
            assertTrue(okBusy.first)
            okBusy = session2.acquire(resources, timeoutSec)
            assertFalse(okBusy.first)
           // java.lang.Thread.sleep((timeoutSec + 1).toLong() * 1000)
            okBusy = session2.acquire(resources, timeoutSec)
            assertTrue(okBusy.first)
        } finally {
            session1.close()
            session2.close()
        }
    }

    @Test
    fun testLockMultipleTime() {
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val params: AgentParams = testSuite.getAgentParams("agent1")
        val session1 = CloudAgent(
            params.serverAddress,
            params.credentials.encodeToByteArray(),
            params.getConnectioni(),
            5
        )
        val session2 = CloudAgent(
            params.serverAddress,
            params.credentials.encodeToByteArray(),
            params.getConnectioni(),
            5
        )
        session1.open()
        session2.open()
        try {
            // check locking OK
            val timeout = 5
            val resources1 = generateRandomResources(100)
            var okBusy: Pair<Boolean?, List<String?>?> = session1.acquire(resources1, timeout)
            assertTrue(okBusy.first==true)
            val resources2 = generateRandomResources(100)
            okBusy = session1.acquire(resources2, timeout)
            assertTrue(okBusy.first)

            // session1 must unlock previously locked resources on new acquire call
            okBusy = session2.acquire(resources1, timeout)
            assertTrue(okBusy.first)
        } finally {
            session1.close()
            session2.close()
        }
    }

    @Test
    fun testDifferentAccounts() {
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val params1: AgentParams = testSuite.getAgentParams("agent1")
        val params2: AgentParams = testSuite.getAgentParams("agent2")
        val session1 = CloudAgent(
            params1.serverAddress,
            params1.credentials.encodeToByteArray(),
            params1.getConnectioni(),
            5
        )
        val session2 = CloudAgent(
            params1.serverAddress,
            params1.credentials.encodeToByteArray(),
            params2.getConnectioni(),
            5
        )
        session1.open()
        session2.open()
        try {
            val resources = generateRandomResources(1)
            val (first) = session1.acquire(resources, 10)
            val (first1) = session2.acquire(resources, 10)
            assertTrue(first)
            assertTrue(first1)
        } finally {
            session1.close()
            session2.close()
        }
    }

    companion object {
        fun generateRandomResources(size: Int): List<String> {
            val res: MutableList<String> = ArrayList<String>()
            for (i in 0 until size) {
                res.add("resource-" + UUID.randomUUID.toString())
            }
            return res
        }
    }
}
