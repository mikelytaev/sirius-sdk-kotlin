package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.helpers.ConfTest
import com.sirius.library.utils.JSONObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestPairwise {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }

    @Test
    fun testPairwiseList() {
        val agent1: CloudAgent = confTest.agent1()
        val agent2: CloudAgent = confTest.agent2()
        agent1.open()
        agent2.open()
        val (first, second) = agent1.getWalleti()?.did?.createAndStoreMyDid() ?:Pair("","")
        val (first1, second1) = agent2.getWalleti()?.did?.createAndStoreMyDid() ?:Pair("","")
        val metaObj: JSONObject = JSONObject()
        metaObj.put("test", "test-value")
        val pairwise = Pairwise(
            Pairwise.Me(first, second),
            Pairwise.Their(first1, "Test-Pairwise", "http://endpoint", second1), metaObj
        )
        val list1: List<Any?> = agent1.getWalleti()?.pairwise?.listPairwise() ?: listOf<Any?>()
        agent1.getPairwiseListi()?.ensureExists(pairwise)
        val list2: List<Any?> = agent1.getWalleti()?.pairwise?.listPairwise()?: listOf<Any?>()
        assertTrue(list1.size < list2.size)
        val ok: Boolean = agent1.getPairwiseListi()?.isExists(first1) ?: false
        assertTrue(ok)
        val pairwise2: Pairwise? = agent1.getPairwiseListi()?.loadForVerkey(second1)
        assertEquals(pairwise.getMetadatai().toString(), pairwise2?.getMetadatai().toString())
        agent1.close()
        agent2.close()
    }
}
