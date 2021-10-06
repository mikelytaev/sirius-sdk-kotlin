package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.storages.InWalletImmutableCollection
import com.sirius.library.helpers.ConfTest
import com.sirius.library.storage.impl.InMemoryImmutableCollection
import com.sirius.library.storage.impl.InMemoryKeyValueStorage
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class TestStorages {
    @Test
    fun testInMemoryKvStorage() {
        val kv = InMemoryKeyValueStorage()
        kv.selectDb("db1")
        kv.set("key1", "value1")
        var value: Any = kv.get("key1")
        assertEquals("value1", value)
        kv.selectDb("db2")
        kv.set("key1", 1000)
        value = kv.get("key1")
        assertEquals(1000, value)
        kv.selectDb("db1")
        value = kv.get("key1")
        assertEquals("value1", value)
        kv.delete("key1")
        value = kv.get("key1")
        assertNull(value)
        kv.delete("unknown-key")
    }

    @Test
    fun testInMemoryImmutableCollection() {
        val collection = InMemoryImmutableCollection()
        collection.selectDb("db1")
        collection.add("Value1", "{\"tag1\": \"tag-val-1\", \"tag2\": \"tag-val-2\"}")
        collection.add("Value2", "{\"tag1\": \"tag-val-1\", \"tag2\": \"tag-val-3\"}")
        val (_, second) = collection.fetch("{\"tag1\": \"tag-val-1\"}", 0)
        assertEquals(second.intValue(), 2)
        val (_, second1) = collection.fetch("{\"tag2\": \"tag-val-2\"}", 0)
        assertEquals(second1.intValue(), 1)
        collection.selectDb("db2")
        collection.fetch("{}", 0)
    }

    @Test
    fun testInWalletImmutableCollection() {
        //TODO test
        val confTest: ConfTest = ConfTest.newInstance()
        val agent1: CloudAgent = confTest.agent1()
        agent1.open()

        //  agent1: Agent
        val collection = InWalletImmutableCollection(agent1.getWallet().nonSecrets)
        val value1: JSONObject = JSONObject()
        value1.put("key1", "value1")
        value1.put("key2", 10000)
        val value2: JSONObject = JSONObject()
        value2.put("key1", "value2")
        value2.put("key2", 50000)
        collection.selectDb(UUID.randomUUID.toString())
        println("valu1=" + value1.toString())
        println("valu2=" + value2.toString())
        println("valu3=" + "{\"tag\": \"value1\"}")
        println("valu4=" + "{\"tag\": \"value2\"}")
        collection.add(value1.toString(), "{\"tag\": \"value1\"}")
        collection.add(value2.toString(), "{\"tag\": \"value2\"}")
        val query1: JSONObject = JSONObject()
        query1.put("tag", "value1")
        val (first, second) = collection.fetch(query1.toString())
        assertEquals(1, second as Int.toLong())
        assertEquals(1, first.size())
        assertEquals(value1.toString(), first[0])
        val query2: JSONObject = JSONObject()
        query2.put("tag", "value2")
        val (first1, second1) = collection.fetch(query2.toString())
        assertEquals(1, second1 as Int.toLong())
        assertEquals(1, first1.size())
        assertEquals(value2.toString(), first1[0])
        val query3: JSONObject = JSONObject()
        val (_, second2) = collection.fetch(query3.toString())
        assertEquals(2, second2 as Int.toLong())
        collection.selectDb(UUID.randomUUID.toString())
        val query4: JSONObject = JSONObject()
        val (_, second3) = collection.fetch(query4.toString())
        assertEquals(0, second3 as Int.toLong())
        agent1.close()
    }
}
/*



@pytest.mark.asyncio
async def test_inwallet_immutable_collection(agent1: Agent):
        await agent1.open()
        try:
        collection = InWalletImmutableCollection(agent1.wallet.non_secrets)

        value1 = {
        'key1': 'value1',
        'key2': 10000
        }
        value2 = {
        'key1': 'value2',
        'key2': 50000
        }

        await collection.select_db(db_name=uuid.uuid4().hex)
        await collection.add(value1, {'tag': 'value1'})
        await collection.add(value2, {'tag': 'value2'})

        fetched, count = await collection.fetch({'tag': 'value1'})
        assert count == 1
        assert len(fetched) == 1
        assert fetched[0] == value1

        fetched, count = await collection.fetch({'tag': 'value2'})
        assert count == 1
        assert len(fetched) == 1
        assert fetched[0] == value2

        fetched, count = await collection.fetch({})
        assert count == 2

        await collection.select_db(db_name=uuid.uuid4().hex)
        fetched, count = await collection.fetch({})
        assert count == 0
        finally:
        await agent1.close()
*/
