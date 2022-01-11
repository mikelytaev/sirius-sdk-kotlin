package com.sirius.library

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.ledger.CredentialDefinition
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.ledger.Schema
import com.sirius.library.helpers.ConfTest
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.UUID
import kotlin.test.*

class TestLedgers {
    lateinit var confTest: ConfTest

    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
        val future = CompletableFutureKotlin<Boolean>()
        LibsodiumInitializer.initializeWithCallback {
            future.complete(true)
        }
        future.get(60)
    }

    @Test
    fun testSchemaRegistration() {

            val agent1: CloudAgent = confTest.agent1()
            agent1.open()
            val seed = "000000000000000000000000Steward1"
            val (first) = agent1.getWalleti()?.did?.createAndStoreMyDid(null, seed) ?: Pair("", "")
            val schemaName = "schema_" + UUID.randomUUID.toString()
            val (_, second) = agent1.getWalleti()?.anoncreds?.issuerCreateSchema(
                first, schemaName, "1.0", "attr1", "attr2", "attr3"
            ) ?: Pair(null, null)
            val ledger: Ledger? = agent1.getLedgersi().get("default")
            val (first1, second1) = ledger?.registerSchema(second, first) ?: Pair(false, null)
            assertTrue(first1)
            assertTrue((second1?.seqNo ?: 0) > 0)
            val (first2) = ledger?.registerSchema(second, first) ?: Pair(false, null)
            assertFalse(first2)
            assertNotNull(second)
            val restoredSchema: Schema? = ledger?.ensureSchemaExists(second, first)
            assertNotNull(restoredSchema)
            assertEquals(second1, restoredSchema)
            agent1.close()


    }

    @Test
    fun testSchemaLoading() {



            val agent1: CloudAgent = confTest.agent1()
            val agent2: CloudAgent = confTest.agent2()
            agent1.open()
            agent2.open()
            val seed1 = "000000000000000000000000Steward1"
            val (first) = agent1.getWalleti()?.did?.createAndStoreMyDid(null, seed1) ?: Pair("", "")
            val schemaName = "schema_" + UUID.randomUUID.toString()
            val (_, second) = agent1.getWalleti()?.anoncreds?.issuerCreateSchema(
                first, schemaName, "1.0", "attr1", "attr2", "attr3"
            ) ?: Pair(null, null)
            val ledger1: Ledger? = agent1.getLedgersi()?.get("default")
            val (first1, second1) = ledger1?.registerSchema(second, first) ?: Pair(false, null)
            assertTrue(first1)
            assertTrue((second1?.seqNo ?: 0) > 0)
            val seed2 = "000000000000000000000000Trustee0"
            val (first2) = agent2.getWalleti()?.did?.createAndStoreMyDid(null, seed2) ?: Pair("", "")
            val ledger2: Ledger? = agent2?.getLedgersi()?.get("default")
            for (i in 0..4) {
                val laodedSchema: Schema? = ledger2?.loadSchema(second1?.id, first2)
                assertNotNull(laodedSchema)
                assertEquals(second1, laodedSchema)
            }
            agent1.close()
            agent2.close()

    }

    @Test
    fun testSchemaFetching() {



            val agent1: CloudAgent = confTest.agent1()
            agent1.open()
            val seed = "000000000000000000000000Steward1"
            val (first) = agent1.getWalleti()?.did?.createAndStoreMyDid(null, seed) ?: Pair("", "")
            val schemaName = "schema_" + UUID.randomUUID.toString()
            val (_, second) = agent1.getWalleti()?.anoncreds?.issuerCreateSchema(
                first, schemaName, "1.0", "attr1", "attr2", "attr3"
            ) ?: Pair(null, null)
            val ledger: Ledger? = agent1.getLedgersi().get("default")
            val (first1) = ledger?.registerSchema(second, first) ?: Pair(false, null)
            assertTrue(first1)
            val fetches: List<Schema> = ledger?.fetchSchemas(null, schemaName) ?: listOf()
            assertEquals(1, fetches.size.toLong())
            assertEquals(first, fetches[0].issuerDid)
            agent1.close()

    }

    @Test
    fun testRegisterCredDef() {



            val agent1: CloudAgent = confTest.agent1()
            agent1.open()
            val seed = "000000000000000000000000Steward1"
            val (first) = agent1.getWalleti()?.did?.createAndStoreMyDid(null, seed) ?: Pair("", "")
            val schemaName = "schema_" + UUID.randomUUID.toString()
            val (_, second) = agent1.getWalleti()?.anoncreds?.issuerCreateSchema(
                first, schemaName, "1.0", "attr1", "attr2", "attr3"
            ) ?: Pair(null, null)
            val ledger: Ledger? = agent1.getLedgersi().get("default")
            val (first1, second1) = ledger?.registerSchema(second, first) ?: Pair(false, null)
            assertTrue(first1)
            val credDef = CredentialDefinition("Test Tag", second1)
            assertNull(credDef.getBodyi())
            agent1.close()

    } /*

            ok, ledger_cred_def = await ledger.register_cred_def(cred_def=cred_def, submitter_did=did)
            assert ok is True
        assert ledger_cred_def.body is not None
        assert ledger_cred_def.seq_no > 0
            assert ledger_cred_def.submitter_did == did
            my_value = 'my-value-' + uuid.uuid4().hex

    ok, ledger_cred_def2 = await ledger.register_cred_def(
            cred_def=cred_def, submitter_did=did, tags={'my_tag': my_value}
        )
                assert ok is True
        assert ledger_cred_def.body == ledger_cred_def2.body
        assert ledger_cred_def2.seq_no > ledger_cred_def.seq_no

            ser = ledger_cred_def.serialize()
    loaded = CredentialDefinition.deserialize(ser)
            assert loaded.body == ledger_cred_def.body
        assert loaded.seq_no == ledger_cred_def.seq_no
        assert loaded.schema.body == ledger_cred_def.schema.body
        assert loaded.config.serialize() == ledger_cred_def.config.serialize()

    results = await ledger.fetch_cred_defs(schema_id=schema_id)
        assert len(results) == 2
    results = await ledger.fetch_cred_defs(my_tag=my_value)
        assert len(results) == 1

    parts = ledger_cred_def.id.split(':')
    print(str(parts))

    opts = CacheOptions()
        for n in range(3):
    cached_body = await agent1.wallet.cache.get_cred_def('default', did, ledger_cred_def.id, opts)
            assert cached_body == ledger_cred_def.body
            cred_def = await ledger.load_cred_def(ledger_cred_def.id, did)
            assert cred_def.body == cached_body
    finally:
    await agent1.close()*/
}
