
package com.sirius.library

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.LibsodiumUtil
import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.microledgers.*
import com.sirius.library.helpers.ConfTest
import com.sirius.library.helpers.ConfTest.Companion.getState
import com.sirius.library.messaging.MessageFabric
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.Date
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID
import kotlin.test.*

class TestMicroledgers {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        MessageFabric.registerAllMessagesClass()
        confTest = ConfTest.newInstance()
        val future = CompletableFutureKotlin<Boolean>()
        LibsodiumInitializer.initializeWithCallback {
            future.complete(true)
        }
        future.get(60)
    }

    @Test
    fun testInitLedger() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                )
            )
            val (ledger, txns) = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns) ?: Pair(null,null)
            assertEquals("3u8ZCezSXJq72H5CdEryyTuwAKzeZnCZyfftJVFr7y8U", ledger?.rootHash())
        } finally {
            agent4.close()
        }
    }

    @Test
    fun testMerkleInfo() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                ),
                Transaction(
                    JSONObject().put("reqId", 4)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op4")
                ),
                Transaction(
                    JSONObject().put("reqId", 5)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op5")
                )
            )
            val (ledger, txns) = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)?: Pair(null,null)
            val merkleInfo: MerkleInfo? = ledger?.getMerkleInfo(4)
            assertEquals(merkleInfo?.rootHash, "CwX1TRYKpejHmdnx3gMgHtSioDzhDGTASAD145kjyyRh")
            assertEquals(
                merkleInfo?.auditPath, listOf(
                    "46kxvYf7RjRERXdS56vUpFCzm2A3qRYSLaRr6tVT6tSd",
                    "3sgNJmsXpmin7P5C6jpHiqYfeWwej5L6uYdYoXTMc1XQ"
                )
            )
        } finally {
            agent4.close()
        }
    }

    @Test
    fun testAppendOperations() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val (ledger) = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns) ?: Pair(null,null)
            val txns: List<Transaction> = listOf(
                Transaction(
                    Transaction(
                        JSONObject().put("reqId", 2)
                            .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                    )
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                )
            )
            val txnTime: String = Date().formatTo("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val (first, second, third) = ledger?.append(txns, txnTime) ?: Triple(0,0, listOf())
            assertEquals(3, second )
            assertEquals(2, first )
            assertEquals(txnTime, third[0].time)
            assertEquals(txnTime, third[1].time)
        } finally {
            agent4.close()
        }
    }

    @Test
    fun testCommitDiscard() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val ledger = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val txns: List<Transaction> = listOf(
                Transaction(
                    Transaction(
                        JSONObject().put("reqId", 2)
                            .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                    )
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                )
            )
            val txnTime: String = Date().formatTo("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            assertEquals(ledger?.first?.uncommittedRootHash(), ledger?.first?.rootHash())
            ledger?.first?.append(txns, txnTime)
            assertNotEquals(ledger?.first?.uncommittedRootHash(), ledger?.first?.rootHash())
            assertEquals(1, ledger?.first?.size())
            assertEquals(3, ledger?.first?.uncommittedSize())

            //commit
            ledger?.first?.commit(1)
            assertEquals(2, ledger?.first?.size())
            assertEquals(3, ledger?.first?.uncommittedSize())
            assertNotEquals(ledger?.first?.uncommittedRootHash(), ledger?.first?.rootHash())

            // discard
            ledger?.first?.discard(1)
            assertEquals(2, ledger?.first?.size())
            assertEquals(2, ledger?.first?.uncommittedSize())
            assertEquals(ledger?.first?.uncommittedRootHash(), ledger?.first?.rootHash())
        } finally {
            agent4.close()
        }

    }

    @Test
    fun testResetUncommitted() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val ledgerPair= agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger = ledgerPair?.first
            val txns: List<Transaction> = listOf(
                Transaction(
                    Transaction(
                        JSONObject().put("reqId", 2)
                            .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                    )
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                )
            )
            ledger?.append(txns)
            val uncommittedSizeBefore: Int = ledger?.uncommittedSize() ?: 0
            ledger?.resetUncommitted()
            val uncommittedSizeAfter: Int = ledger?.uncommittedSize() ?: 0
            assertNotEquals(uncommittedSizeAfter.toLong(), uncommittedSizeBefore.toLong())
            assertEquals(1, uncommittedSizeAfter.toLong())
        } finally {
            agent4.close()
        }
    }

    @Test
    fun testGetOperations() {

        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                )
            )
            val ledgerPair = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger = ledgerPair?.first
            var txns: List<Transaction?> = listOf(
                Transaction(
                    JSONObject().put("reqId", 4)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op4")
                ),
                Transaction(
                    JSONObject().put("reqId", 5)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op5")
                )
            )
            ledger?.append(txns)

            // 1 get_last_committed_txn
            var txn: Transaction? = ledger?.lastCommittedTransaction
            assertEquals(txn?.optString("op"), "op3")

            // 2 get_last_txn
            txn = ledger?.lastTransaction
            assertEquals(txn?.optString("op"), "op5")

            //3 get_uncommitted_txns
            txns = ledger?.uncommittedTransactions as List<Transaction?>
            assertEquals(2, txns.size.toLong())
            //assert all(op in str(txns) for op in ['op4', 'op5']) is True
            //assert any(op in str(txns) for op in ['op1', 'op2', 'op3']) is False

            // 4 get_by_seq_no
            txn = ledger.getTransaction(1)
            assertEquals(txn?.optString("op"), "op1")

            // 5 get_by_seq_no_uncommitted
            txn = ledger.getUncommittedTransaction(4)
            assertEquals(txn?.optString("op"), "op4")
        } finally {
            agent4.close()
        }

    }

    @Test
    fun testReset() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                ),
                Transaction(
                    JSONObject().put("reqId", 4)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op4")
                ),
                Transaction(
                    JSONObject().put("reqId", 5)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op5")
                )
            )
            val ledgerPair = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger = ledgerPair?.first
            assertEquals(5, ledger?.size())
            assertTrue(agent4.getMicroledgersi()?.isExists(ledgerName) ?: false)
            agent4.getMicroledgersi()?.reset(ledgerName)
            assertFalse(agent4.getMicroledgersi()?.isExists(ledgerName) ?: false)
        } finally {
            agent4.close()
        }

    }

    @Test
    fun testList() {

        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                )
            )
            val ledgerPair = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger = ledgerPair?.first
            var collection: List<LedgerMeta> = agent4.getMicroledgersi()?.list as? List<LedgerMeta> ?: listOf()
            var contains = false
            for (meta in collection) {
                if (meta.name.equals(ledgerName)) {
                    contains = true
                    break
                }
            }
            assertTrue(contains)
            assertTrue(agent4.getMicroledgersi()?.isExists(ledgerName) ?: false)
            agent4.getMicroledgersi()?.reset(ledgerName)
            collection = agent4.getMicroledgersi()?.list as? List<LedgerMeta> ?: listOf()
            contains = false
            for (meta in collection) {
                if (meta.name.equals(ledgerName)) {
                    contains = true
                    break
                }
            }
            assertFalse(contains)
        } finally {
            agent4.close()
        }

    }

    @Test
    fun testGetAllTxns() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                )
            )
            val ledgerPair = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger=ledgerPair?.first
            var txns: List<Transaction?> = listOf(
                Transaction(
                    JSONObject().put("reqId", 4)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op4")
                ),
                Transaction(
                    JSONObject().put("reqId", 5)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op5")
                )
            )
            ledger?.append(txns)
            txns = ledger?.allTransactions as? List<Transaction?> ?: listOf()
            assertEquals(3, txns.size.toLong())
        } finally {
            agent4.close()
        }
    }

    @Test
    fun testAuditProof() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op3")
                ),
                Transaction(
                    JSONObject().put("reqId", 4)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op4")
                ),
                Transaction(
                    JSONObject().put("reqId", 5)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op5")
                ),
                Transaction(
                    JSONObject().put("reqId", 6)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op6")
                )
            )
            val ledgerPair = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger = ledgerPair?.first
            val txns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 7)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op7")
                ),
                Transaction(
                    JSONObject().put("reqId", 8)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op8")
                ),
                Transaction(
                    JSONObject().put("reqId", 9)
                        .put("identifier", "CECeGXDi6EHuhpwz19uyjjEnsRGNXodFYqCRgdLmLRkt").put("op", "op9")
                )
            )
            ledger?.append(txns)
            val auditPaths: MutableList<List<String>> = ArrayList<List<String>>()
            for (seqNo in listOf<Int>(1, 2, 3, 4, 5, 6)) {
                val auditProof: AuditProof? = ledger?.getAuditProof(seqNo)
                assertEquals("3eDS4j8HgpAyRnuvfFG624KKvQBuNXKBenhqHmHtUgeq", auditProof?.rootHash)
                assertEquals(6, auditProof?.ledgerSize)
                assertFalse(auditPaths.contains(auditProof?.auditPath))
                auditProof?.auditPath?.let { auditPaths.add(it) }
            }
            for (seqNo in listOf<Int>(7, 8, 9)) {
                val auditProof: AuditProof? = ledger?.getAuditProof(seqNo)
                assertEquals("3eDS4j8HgpAyRnuvfFG624KKvQBuNXKBenhqHmHtUgeq", auditProof?.rootHash)
                assertEquals(6, auditProof?.ledgerSize)
                auditProof?.auditPath?.let { auditPaths.add(it) }
            }
            assertEquals("Dkoca8Af15uMLBHAqbddwqmpiqsaDEtKDoFVfNRXt44g", ledger?.uncommittedRootHash())
        } finally {
            agent4.close()
        }
    }

   /* @Test
    fun testLeafHash() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val ledgerPair = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger  = ledgerPair?.first
            val second =  ledgerPair?.second
            val txn: Transaction? = second?.get(0)
            val leafHash: ByteArray = agent4.getMicroledgersi()?.leafHash(txn) ?: ByteArray(0)
            val leafHashHex: String =  LibsodiumUtil.toHex(leafHash.toUByteArray())
         //   val leafHashHex: String = LazySodium.toHex(leafHash)
            assertEquals(
                "79D9929FD1E7F16F099C26B6F44850DA044AD0FE51E92E582D9CA372F2B8B930",
                leafHashHex
            )
        } finally {
            agent4.close()
        }
    }*/

    @Test
    fun testRename() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerName: String = confTest.ledgerName()
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val ledgerPair = agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            val ledger = ledgerPair?.first
            val newName = "new_name_" + UUID.randomUUID
            ledger?.rename(newName)
            assertFalse(agent4.getMicroledgersi()?.isExists(ledgerName) ?: true)
            assertTrue(agent4.getMicroledgersi()?.isExists(newName) ?: false)
        } finally {
            agent4.close()
        }
    }

    @Test
    fun testBatchedOps() {
        val agent4: CloudAgent = confTest.getAgent("agent4")
        val ledgerNames: List<String> = listOf(
            "Ledger-" + UUID.randomUUID,
            "Ledger-" + UUID.randomUUID
        )
        agent4.open()
        try {
            val genesisTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val resetTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op2")
                )
            )
            val commitTxns: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op3")
                )
            )

            val txnTime: String = Date().formatTo("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            for (ledgerName in ledgerNames) {
                agent4.getMicroledgersi()?.create(ledgerName, genesisTxns)
            }
            val batched: AbstractBatchedAPI? = agent4.getMicroledgersi()?.batched
            var ledgers: List<AbstractMicroledger> = batched?.openByLedgerNames(ledgerNames) ?: listOf()
            try {
                val s1: MutableSet<String> = HashSet<String>()
                for (l in ledgers) {
                    l.name()?.let { s1.add(it) }
                }
                assertEquals(s1, HashSet<String>(ledgerNames))

                // Fetch states
                ledgers = batched?.states as? List<AbstractMicroledger> ?: listOf()
                val statesBefore: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesBefore[ledger.name() ?: ""] = getState(ledger)
                }
                assertEquals(statesBefore.keys, HashSet<String>(ledgerNames))

                // Append
                ledgers = batched?.append(resetTxns) ?: listOf()
                val statesAfterAppend: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesAfterAppend[ledger.name() ?: ""] = getState(ledger)
                }
                assertEquals(statesAfterAppend.keys, HashSet<String>(ledgerNames))
                for ((_, value) in statesAfterAppend) {
                    assertEquals(2, value.optInt("uncommitted_size")?.toLong())
                }

                // Reset uncommitted
                ledgers = batched?.resetUncommitted() as? List<AbstractMicroledger> ?: listOf()
                val statesAfterResetUncommitted: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesAfterResetUncommitted[ledger.name() ?: ""] = getState(ledger)
                }
                assertEquals(statesAfterResetUncommitted.keys, HashSet<String>(ledgerNames))
                for ((_, value) in statesAfterResetUncommitted) {
                    assertEquals(1, value.optInt("uncommitted_size")?.toLong())
                }

                // Append + Commit
                batched?.append(commitTxns, txnTime)
                ledgers = batched?.commit() as? List<AbstractMicroledger> ?: listOf()
                val statesAfterCommit: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesAfterCommit[ledger.name() ?: ""] = getState(ledger)
                }
                for ((_, value)   in statesAfterCommit) {
                    assertEquals(2, value.optInt("uncommitted_size")?.toLong())
                    assertEquals(2, value.optInt("size")?.toLong())
                }

                // Check all txns
                for (ledgerName in ledgerNames) {
                    val ledger: AbstractMicroledger? = agent4.getMicroledgersi()?.getLedger(ledgerName)
                    val txns: List<Transaction> = ledger?.allTransactions as? List<Transaction> ?: listOf()
                    assertEquals(2, txns.size.toLong())
                    assertEquals("op1", txns[0].optString("op"))
                    assertEquals("op3", txns[1].optString("op"))
                    assertEquals(txnTime, txns[1].time)
                }
            } finally {
                batched?.close()
            }
        } finally {
            agent4.close()
        }
    }


}

