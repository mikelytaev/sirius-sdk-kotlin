package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.microledgers.*
import com.sirius.library.helpers.ConfTest
import com.sirius.library.helpers.ConfTest.Companion.getState
import com.sirius.library.utils.Date
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID
import kotlin.test.*

class TestMicroledgers {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
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
            val (ledger, txns) = agent4.getMicroledgers()?.create(ledgerName, genesisTxns) ?: Pair(null,null)
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
            val (ledger, txns) = agent4.getMicroledgers()?.create(ledgerName, genesisTxns)?: Pair(null,null)
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
            val (ledger) = agent4.getMicroledgers()?.create(ledgerName, genesisTxns) ?: Pair(null,null)
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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
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
            val df: java.text.DateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val txnTime: String = df.format(java.util.Date(java.lang.System.currentTimeMillis()))
            assertEquals(ledger.uncommittedRootHash(), ledger.rootHash())
            ledger.append(txns, txnTime)
            assertNotEquals(ledger.uncommittedRootHash(), ledger.rootHash())
            assertEquals(1, ledger.size())
            assertEquals(3, ledger.uncommittedSize())

            //commit
            ledger.commit(1)
            assertEquals(2, ledger.size())
            assertEquals(3, ledger.uncommittedSize())
            assertNotEquals(ledger.uncommittedRootHash(), ledger.rootHash())

            // discard
            ledger.discard(1)
            assertEquals(2, ledger.size())
            assertEquals(2, ledger.uncommittedSize())
            assertEquals(ledger.uncommittedRootHash(), ledger.rootHash())
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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
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
            ledger.append(txns)
            val uncommittedSizeBefore: Int = ledger.uncommittedSize()
            ledger.resetUncommitted()
            val uncommittedSizeAfter: Int = ledger.uncommittedSize()
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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
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
            ledger.append(txns)

            // 1 get_last_committed_txn
            var txn: Transaction = ledger.getLastCommittedTransaction()
            assertEquals(txn.optString("op"), "op3")

            // 2 get_last_txn
            txn = ledger.getLastTransaction()
            assertEquals(txn.optString("op"), "op5")

            //3 get_uncommitted_txns
            txns = ledger.getUncommittedTransactions()
            assertEquals(2, txns.size.toLong())
            //assert all(op in str(txns) for op in ['op4', 'op5']) is True
            //assert any(op in str(txns) for op in ['op1', 'op2', 'op3']) is False

            // 4 get_by_seq_no
            txn = ledger.getTransaction(1)
            assertEquals(txn.optString("op"), "op1")

            // 5 get_by_seq_no_uncommitted
            txn = ledger.getUncommittedTransaction(4)
            assertEquals(txn.optString("op"), "op4")
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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
            assertEquals(5, ledger.size())
            assertTrue(agent4.getMicroledgers().isExists(ledgerName))
            agent4.getMicroledgers().reset(ledgerName)
            assertFalse(agent4.getMicroledgers().isExists(ledgerName))
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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
            var collection: List<LedgerMeta> = agent4.getMicroledgers().list
            var contains = false
            for (meta in collection) {
                if (meta.name.equals(ledgerName)) {
                    contains = true
                    break
                }
            }
            assertTrue(contains)
            assertTrue(agent4.getMicroledgers().isExists(ledgerName))
            agent4.getMicroledgers().reset(ledgerName)
            collection = agent4.getMicroledgers().list
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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
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
            ledger.append(txns)
            txns = ledger.getAllTransactions()
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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
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
            ledger.append(txns)
            val auditPaths: MutableList<List<String>> = ArrayList<List<String>>()
            for (seqNo in listOf<Int>(1, 2, 3, 4, 5, 6)) {
                val auditProof: AuditProof = ledger.getAuditProof(seqNo)
                assertEquals("3eDS4j8HgpAyRnuvfFG624KKvQBuNXKBenhqHmHtUgeq", auditProof.rootHash)
                assertEquals(6, auditProof.ledgerSize)
                assertFalse(auditPaths.contains(auditProof.auditPath))
                auditPaths.add(auditProof.auditPath)
            }
            for (seqNo in listOf<Int>(7, 8, 9)) {
                val auditProof: AuditProof = ledger.getAuditProof(seqNo)
                assertEquals("3eDS4j8HgpAyRnuvfFG624KKvQBuNXKBenhqHmHtUgeq", auditProof.rootHash)
                assertEquals(6, auditProof.ledgerSize)
                auditPaths.add(auditProof.auditPath)
            }
            assertEquals("Dkoca8Af15uMLBHAqbddwqmpiqsaDEtKDoFVfNRXt44g", ledger.uncommittedRootHash())
        } finally {
            agent4.close()
        }
    }

    @Test
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
            val (ledger, second) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
            val txn: Transaction = second[0]
            val leafHash: ByteArray = agent4.getMicroledgers().leafHash(txn)
            val leafHashHex: String = LazySodium.toHex(leafHash)
            assertEquals(
                "79D9929FD1E7F16F099C26B6F44850DA044AD0FE51E92E582D9CA372F2B8B930",
                leafHashHex
            )
        } finally {
            agent4.close()
        }
    }

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
            val (ledger) = agent4.getMicroledgers().create(ledgerName, genesisTxns)
            val newName = "new_name_" + UUID.randomUUID
            ledger.rename(newName)
            assertFalse(agent4.getMicroledgers().isExists(ledgerName))
            assertTrue(agent4.getMicroledgers().isExists(newName))
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
            val df: java.text.DateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val txnTime: String = df.format(java.util.Date(java.lang.System.currentTimeMillis()))
            for (ledgerName in ledgerNames) {
                agent4.getMicroledgers().create(ledgerName, genesisTxns)
            }
            val batched: AbstractBatchedAPI = agent4.getMicroledgers().batched
            var ledgers: List<AbstractMicroledger> = batched.openByLedgerNames(ledgerNames)
            try {
                val s1: MutableSet<String> = HashSet<String>()
                for (l in ledgers) {
                    s1.add(l.name())
                }
                assertEquals(s1, HashSet<String>(ledgerNames))

                // Fetch states
                ledgers = batched.states
                val statesBefore: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesBefore[ledger.name()] = getState(ledger)
                }
                assertEquals(statesBefore.keys, HashSet<String>(ledgerNames))

                // Append
                ledgers = batched.append(resetTxns)
                val statesAfterAppend: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesAfterAppend[ledger.name()] = getState(ledger)
                }
                assertEquals(statesAfterAppend.keys, HashSet<String>(ledgerNames))
                for ((_, value): Map.Entry<String, JSONObject> in statesAfterAppend) {
                    assertEquals(2, value.optInt("uncommitted_size").toLong())
                }

                // Reset uncommitted
                ledgers = batched.resetUncommitted()
                val statesAfterResetUncommitted: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesAfterResetUncommitted[ledger.name()] = getState(ledger)
                }
                assertEquals(statesAfterResetUncommitted.keys, HashSet<String>(ledgerNames))
                for ((_, value): Map.Entry<String, JSONObject> in statesAfterResetUncommitted) {
                    assertEquals(1, value.optInt("uncommitted_size").toLong())
                }

                // Append + Commit
                batched.append(commitTxns, txnTime)
                ledgers = batched.commit()
                val statesAfterCommit: MutableMap<String, JSONObject> =
                    HashMap<String, JSONObject>()
                for (ledger in ledgers) {
                    statesAfterCommit[ledger.name()] = getState(ledger)
                }
                for ((_, value): Map.Entry<String, JSONObject> in statesAfterCommit) {
                    assertEquals(2, value.optInt("uncommitted_size").toLong())
                    assertEquals(2, value.optInt("size").toLong())
                }

                // Check all txns
                for (ledgerName in ledgerNames) {
                    val ledger: AbstractMicroledger? = agent4.getMicroledgers().getLedger(ledgerName)
                    val txns: List<Transaction> = ledger.allTransactions
                    assertEquals(2, txns.size.toLong())
                    assertEquals("op1", txns[0].optString("op"))
                    assertEquals("op3", txns[1].optString("op"))
                    assertEquals(txnTime, txns[1].time)
                }
            } finally {
                batched.close()
            }
        } finally {
            agent4.close()
        }
    }
}
