package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.consensus.simple.messages.*
import com.sirius.library.agent.microledgers.AbstractMicroledger
import com.sirius.library.agent.microledgers.Transaction
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.errors.sirius_exceptions.SiriusContextError
import com.sirius.library.errors.sirius_exceptions.SiriusValidationError
import com.sirius.library.helpers.ConfTest
import com.sirius.library.utils.JSONObject
import kotlin.test.*

class TestSimpleConsensus {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }

    @Test
    @Throws(SiriusContextError::class, SiriusValidationError::class)
    fun testInitLedgerMessaging() {
        val agentA: CloudAgent = confTest.getAgent("agent1")
        val agentB: CloudAgent = confTest.getAgent("agent2")
        val ledgerName: String = confTest.ledgerName()
        agentA.open()
        agentB.open()
        try {
            val a2b: Pairwise = confTest.getPairwise(agentA, agentB)
            val b2a: Pairwise = confTest.getPairwise(agentB, agentA)
            a2b.me.did = "did:peer:" + a2b.me.did
            b2a.me.did = "did:peer:" + b2a.me.did
            val genesisTxns: MutableList<Transaction> = ArrayList<Transaction>()
            genesisTxns.add(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val request: InitRequestLedgerMessage = InitRequestLedgerMessage.builder()
                .setParticipants(listOf(a2b.me.did, b2a.me.did))
                .setLedgerName(ledgerName).setGenesis(genesisTxns).setRootHash("xxx").build()
            request.addSignature(agentA.getWallet().crypto, a2b.me)
            request.addSignature(agentB.getWallet().crypto, b2a.me)
            assertEquals(2, request.signatures().length())
            request.checkSignatures(agentA.getWallet().crypto, a2b.me.did)
            request.checkSignatures(agentA.getWallet().crypto, b2a.me.did)
            request.checkSignatures(agentA.getWallet().crypto)
            request.checkSignatures(agentB.getWallet().crypto, a2b.me.did)
            request.checkSignatures(agentB.getWallet().crypto, b2a.me.did)
            request.checkSignatures(agentB.getWallet().crypto)
            val response: InitResponseLedgerMessage = InitResponseLedgerMessage.builder().build()
            response.assignFrom(request)
            val payload1: JSONObject = request.getMessageObj()
            val payload2: JSONObject = response.getMessageObj()
            assertFalse(payload1.similar(payload2))
            payload1.remove("@id")
            payload1.remove("@type")
            payload2.remove("@id")
            payload2.remove("@type")
            assertTrue(payload1.similar(payload2))
        } finally {
            agentA.close()
            agentB.close()
        }
    }

    @Test
    @Throws(SiriusValidationError::class)
    fun testTransactionMessaging() {
        val agentA: CloudAgent = confTest.getAgent("agent1")
        val agentB: CloudAgent = confTest.getAgent("agent2")
        val ledgerName: String = confTest.ledgerName()
        agentA.open()
        agentB.open()
        try {
            val a2b: Pairwise = confTest.getPairwise(agentA, agentB)
            val b2a: Pairwise = confTest.getPairwise(agentB, agentA)
            a2b.me.did = "did:peer:" + a2b.me.did
            b2a.me.did = "did:peer:" + b2a.me.did
            val genesisTxns: MutableList<Transaction> = ArrayList<Transaction>()
            genesisTxns.add(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                )
            )
            val (ledgerForA) = agentA.getMicroledgers().create(ledgerName, genesisTxns)
            val (ledgerForB) = agentB.getMicroledgers().create(ledgerName, genesisTxns)
            val newTransactions: List<Transaction> = listOf(
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op2")
                ),
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op3")
                )
            )
            val (_, _, newTxns) = ledgerForA.append(newTransactions)

            // A->B
            val stateA = MicroLedgerState(ConfTest.getState(ledgerForA))
            val x: MicroLedgerState = MicroLedgerState.fromLedger(ledgerForA)
            assertTrue(stateA.similar(x))
            assertEquals(stateA.getHash(), x.getHash())
            val propose: ProposeTransactionsMessage =
                ProposeTransactionsMessage.builder().setTransactions(newTxns).setState(stateA).build()
            propose.validate()

            // B -> A
            ledgerForB.append(propose.transactions())
            val preCommit: PreCommitTransactionsMessage =
                PreCommitTransactionsMessage.builder().setState(MicroLedgerState(ConfTest.getState(ledgerForA))).build()
            preCommit.signState(agentB.getWallet().crypto, b2a.me)
            preCommit.validate()
            val (first, second) = preCommit.verifyState(agentA.getWallet().crypto, a2b.their.verkey)
            assertTrue(first)
            assertEquals(second, stateA.hash)

            // A -> B
            val commit: CommitTransactionsMessage = CommitTransactionsMessage.builder().build()
            commit.addPreCommit(a2b.their.did, preCommit)
            commit.validate()
            val states: JSONObject = commit.verifyPreCommits(agentA.getWallet().crypto, stateA)
            assertTrue(states.toString().contains(a2b.their.did))
            assertTrue(states.toString().contains(a2b.their.verkey))

            // B -> A (post commit)
            val postCommit: PostCommitTransactionsMessage = PostCommitTransactionsMessage.builder().build()
            postCommit.addCommitSign(agentB.getWallet().crypto, commit, b2a.me)
            postCommit.validate()
            assertTrue(
                postCommit.verifyCommits(
                    agentA.getWallet().crypto,
                    commit,
                    listOf(a2b.their.verkey)
                )
            )
        } finally {
            agentA.close()
            agentB.close()
        }
    }

    private fun routineOfLedgerCreator(
        uri: String, credentials: ByteArray, p2p: P2PConnection, me: Pairwise.Me,
        participants: List<String>, ledgerName: String, genesis: List<Transaction>
    ): java.util.function.Function<java.lang.Void, Pair<Boolean, AbstractMicroledger>> {
        return label@ java.util.function.Function<java.lang.Void, Pair<Boolean, AbstractMicroledger>> { unused: java.lang.Void? ->
            CloudContext.builder().setServerUri(uri).setCredentials(credentials).setP2p(p2p).build().use { c ->
                val machine = MicroLedgerSimpleConsensus(c, me)
                return@label machine.initMicroledger(ledgerName, participants, genesis)
            }
        }
    }

    private fun routineOfLedgerCreationAcceptor(
        uri: String,
        credentials: ByteArray,
        p2p: P2PConnection
    ): java.util.function.Function<java.lang.Void, Pair<Boolean, AbstractMicroledger>> {
        return label@ java.util.function.Function<java.lang.Void, Pair<Boolean, AbstractMicroledger>> { unused: java.lang.Void? ->
            try {
                CloudContext.builder().setServerUri(uri).setCredentials(credentials).setP2p(p2p).build().use { c ->
                    val listener: Listener = c.subscribe()
                    val event: Event = listener.getOne().get(30, java.util.concurrent.TimeUnit.SECONDS)
                    val propose: Message = event.message()
                    assertTrue(propose is InitRequestLedgerMessage)
                    val machine = MicroLedgerSimpleConsensus(c, event.getPairwise().getMe())
                    return@label machine.acceptMicroledger(event.getPairwise(), propose as InitRequestLedgerMessage)
                }
            } catch (e: java.lang.InterruptedException) {
                e.printStackTrace()
            } catch (e: java.util.concurrent.ExecutionException) {
                e.printStackTrace()
            } catch (e: java.util.concurrent.TimeoutException) {
                e.printStackTrace()
            }
            null
        }
    }

    @Test
    @Throws(
        java.lang.InterruptedException::class,
        java.util.concurrent.ExecutionException::class,
        java.util.concurrent.TimeoutException::class
    )
    fun testSimpleConsensusInitLedger() {
        val agentA: CloudAgent = confTest.getAgent("agent1")
        val agentB: CloudAgent = confTest.getAgent("agent2")
        val agentC: CloudAgent = confTest.getAgent("agent3")
        val ledgerName: String = confTest.ledgerName()
        val testSuite: ServerTestSuite = confTest.getSuiteSingleton()
        val aParams: AgentParams = testSuite.getAgentParams("agent1")
        val bParams: AgentParams = testSuite.getAgentParams("agent2")
        val cParams: AgentParams = testSuite.getAgentParams("agent3")
        agentA.open()
        agentB.open()
        agentC.open()
        try {
            val a2b: Pairwise = confTest.getPairwise(agentA, agentB)
            val a2c: Pairwise = confTest.getPairwise(agentA, agentC)
            assertEquals(a2b.getMe(), a2c.getMe())
            val b2a: Pairwise = confTest.getPairwise(agentB, agentA)
            val b2c: Pairwise = confTest.getPairwise(agentB, agentC)
            assertEquals(b2a.getMe(), b2c.getMe())
            val c2a: Pairwise = confTest.getPairwise(agentC, agentA)
            val c2b: Pairwise = confTest.getPairwise(agentC, agentB)
            assertEquals(c2a.getMe(), c2b.getMe())
            val participants: List<String> =
                java.util.Arrays.asList(a2b.me.did, a2b.getTheir().getDid(), a2c.getTheir().getDid())
            val genesis: List<Transaction> = java.util.Arrays.asList<Transaction>(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                )
            )
            val creatorRoutine: java.util.function.Function<java.lang.Void, Pair<Boolean, AbstractMicroledger>> =
                routineOfLedgerCreator(
                    aParams.getServerAddress(),
                    aParams.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    aParams.getConnection(),
                    a2b.getMe(),
                    participants,
                    ledgerName,
                    genesis
                )
            val acceptorRoutine1: java.util.function.Function<java.lang.Void, Pair<Boolean, AbstractMicroledger>> =
                routineOfLedgerCreationAcceptor(
                    bParams.getServerAddress(),
                    bParams.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8), bParams.getConnection()
                )
            val acceptorRoutine2: java.util.function.Function<java.lang.Void, Pair<Boolean, AbstractMicroledger>> =
                routineOfLedgerCreationAcceptor(
                    cParams.getServerAddress(),
                    cParams.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8), cParams.getConnection()
                )
            val stamp1: Long = java.lang.System.currentTimeMillis()
            println("> begin")
            val cf1: java.util.concurrent.CompletableFuture<Pair<Boolean, AbstractMicroledger>> =
                java.util.concurrent.CompletableFuture.supplyAsync<Pair<Boolean, AbstractMicroledger>>(
                    java.util.function.Supplier<Pair<Boolean, AbstractMicroledger>> { creatorRoutine.apply(null) },
                    java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
            val cf2: java.util.concurrent.CompletableFuture<Pair<Boolean, AbstractMicroledger>> =
                java.util.concurrent.CompletableFuture.supplyAsync<Pair<Boolean, AbstractMicroledger>>(
                    java.util.function.Supplier<Pair<Boolean, AbstractMicroledger>> { acceptorRoutine1.apply(null) },
                    java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
            val cf3: java.util.concurrent.CompletableFuture<Pair<Boolean, AbstractMicroledger>> =
                java.util.concurrent.CompletableFuture.supplyAsync<Pair<Boolean, AbstractMicroledger>>(
                    java.util.function.Supplier<Pair<Boolean, AbstractMicroledger>> { acceptorRoutine2.apply(null) },
                    java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
            cf1.get(30, java.util.concurrent.TimeUnit.SECONDS)
            cf2.get(30, java.util.concurrent.TimeUnit.SECONDS)
            cf3.get(30, java.util.concurrent.TimeUnit.SECONDS)
            println("> end")
            val stamp2: Long = java.lang.System.currentTimeMillis()
            println("***** Consensus timeout: " + (stamp2 - stamp1) / 1000 + " sec")
            assertTrue(agentA.getMicroledgers().isExists(ledgerName))
            assertTrue(agentB.getMicroledgers().isExists(ledgerName))
            assertTrue(agentC.getMicroledgers().isExists(ledgerName))
            for (agent in java.util.Arrays.asList<Any>(agentA, agentB, agentC)) {
                val ledger: AbstractMicroledger = agent.getMicroledgers().getLedger(ledgerName)
                val txns: List<Transaction> = ledger.getAllTransactions()
                assertEquals(2, txns.size.toLong())
            }
        } finally {
            agentA.close()
            agentB.close()
            agentC.close()
        }
    }

    private fun routineOfTxnCommitter(
        uri: String, credentials: ByteArray, p2p: P2PConnection,
        me: Me, participants: List<String>,
        ledger: AbstractMicroledger, txns: List<Transaction>
    ): java.util.function.Function<java.lang.Void, Pair<Boolean, List<Transaction>>> {
        return label@ java.util.function.Function<java.lang.Void, Pair<Boolean, List<Transaction>>> { unused: java.lang.Void? ->
            CloudContext.builder().setServerUri(uri).setCredentials(credentials).setP2p(p2p).build().use { c ->
                val machine = MicroLedgerSimpleConsensus(c, me)
                return@label machine.commit(ledger, participants, txns)
            }
        }
    }

    private fun routineOfTxnAcceptor(
        uri: String,
        credentials: ByteArray,
        p2p: P2PConnection
    ): java.util.function.Function<java.lang.Void, Boolean> {
        return label@ java.util.function.Function<java.lang.Void, Boolean> { unused: java.lang.Void? ->
            try {
                CloudContext.builder().setServerUri(uri).setCredentials(credentials).setP2p(p2p).build().use { c ->
                    val listener: Listener = c.subscribe()
                    val event: Event = listener.getOne().get(30, java.util.concurrent.TimeUnit.SECONDS)
                    assertNotNull(event.getPairwise())
                    if (event.message() is ProposeTransactionsMessage) {
                        val machine =
                            MicroLedgerSimpleConsensus(c, event.getPairwise().getMe())
                        return@label machine.acceptCommit(
                            event.getPairwise(),
                            event.message() as ProposeTransactionsMessage
                        )
                    }
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                fail()
            }
            false
        }
    }

    @Test
    @Throws(
        java.lang.InterruptedException::class,
        java.util.concurrent.ExecutionException::class,
        java.util.concurrent.TimeoutException::class
    )
    fun testSimpleConsensusCommit() {
        val agentA: CloudAgent = confTest.getAgent("agent1")
        val agentB: CloudAgent = confTest.getAgent("agent2")
        val agentC: CloudAgent = confTest.getAgent("agent3")
        val ledgerName: String = confTest.ledgerName()
        val testSuite: ServerTestSuite = confTest.getSuiteSingleton()
        val aParams: AgentParams = testSuite.getAgentParams("agent1")
        val bParams: AgentParams = testSuite.getAgentParams("agent2")
        val cParams: AgentParams = testSuite.getAgentParams("agent3")
        agentA.open()
        agentB.open()
        agentC.open()
        try {
            val a2b: Pairwise = confTest.getPairwise(agentA, agentB)
            val a2c: Pairwise = confTest.getPairwise(agentA, agentC)
            assertEquals(a2b.getMe(), a2c.getMe())
            val b2a: Pairwise = confTest.getPairwise(agentB, agentA)
            val b2c: Pairwise = confTest.getPairwise(agentB, agentC)
            assertEquals(b2a.getMe(), b2c.getMe())
            val c2a: Pairwise = confTest.getPairwise(agentC, agentA)
            val c2b: Pairwise = confTest.getPairwise(agentC, agentB)
            assertEquals(c2a.getMe(), c2b.getMe())
            val participants: List<String> =
                java.util.Arrays.asList(a2b.me.did, a2b.getTheir().getDid(), a2c.getTheir().getDid())
            val genesis: List<Transaction> = java.util.Arrays.asList<Transaction>(
                Transaction(
                    JSONObject().put("reqId", 1)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op1")
                ),
                Transaction(
                    JSONObject().put("reqId", 2)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op2")
                )
            )
            val t1: Pair<AbstractMicroledger, List<Transaction>> = agentA.getMicroledgers().create(ledgerName, genesis)
            var ledgerForA: AbstractMicroledger = t1.first
            agentB.getMicroledgers().create(ledgerName, genesis)
            agentC.getMicroledgers().create(ledgerName, genesis)
            val txns: List<Transaction> = java.util.Arrays.asList<Transaction>(
                Transaction(
                    JSONObject().put("reqId", 3)
                        .put("identifier", "5rArie7XKukPCaEwq5XGQJnM9Fc5aZE3M9HAPVfMU2xC").put("op", "op3")
                ),
                Transaction(
                    JSONObject().put("reqId", 4)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op4")
                ),
                Transaction(
                    JSONObject().put("reqId", 5)
                        .put("identifier", "2btLJAAb1S3x6hZYdVyAePjqtQYi2ZBSRGy4569RZu8h").put("op", "op5")
                )
            )
            val committer: java.util.function.Function<java.lang.Void, Pair<Boolean, List<Transaction>>> =
                routineOfTxnCommitter(
                    aParams.getServerAddress(),
                    aParams.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    aParams.getConnection(),
                    a2b.getMe(),
                    participants,
                    ledgerForA,
                    txns
                )
            val acceptor1: java.util.function.Function<java.lang.Void, Boolean> = routineOfTxnAcceptor(
                bParams.getServerAddress(),
                bParams.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8), bParams.getConnection()
            )
            val acceptor2: java.util.function.Function<java.lang.Void, Boolean> = routineOfTxnAcceptor(
                cParams.getServerAddress(),
                cParams.getCredentials().getBytes(java.nio.charset.StandardCharsets.UTF_8), cParams.getConnection()
            )
            val stamp1: Long = java.lang.System.currentTimeMillis()
            println("> begin")
            val cf1: java.util.concurrent.CompletableFuture<Pair<Boolean, List<Transaction>>> =
                java.util.concurrent.CompletableFuture.supplyAsync<Pair<Boolean, List<Transaction>>>(
                    java.util.function.Supplier<Pair<Boolean, List<Transaction>>> {
                        committer.apply(
                            null
                        )
                    },
                    java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
            val cf2: java.util.concurrent.CompletableFuture<Boolean> =
                java.util.concurrent.CompletableFuture.supplyAsync<Boolean>(
                    java.util.function.Supplier<Boolean> { acceptor1.apply(null) },
                    java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
            val cf3: java.util.concurrent.CompletableFuture<Boolean> =
                java.util.concurrent.CompletableFuture.supplyAsync<Boolean>(
                    java.util.function.Supplier<Boolean> { acceptor2.apply(null) },
                    java.util.concurrent.Executor { r: java.lang.Runnable? -> java.lang.Thread(r).start() })
            cf1.get(30, java.util.concurrent.TimeUnit.SECONDS)
            cf2.get(30, java.util.concurrent.TimeUnit.SECONDS)
            cf3.get(30, java.util.concurrent.TimeUnit.SECONDS)
            println("> end")
            val stamp2: Long = java.lang.System.currentTimeMillis()
            println("***** Consensus timeout: " + (stamp2 - stamp1) / 1000 + " sec")
            ledgerForA = agentA.getMicroledgers().getLedger(ledgerName)
            val ledgerForB: AbstractMicroledger = agentB.getMicroledgers().getLedger(ledgerName)
            val ledgerForC: AbstractMicroledger = agentC.getMicroledgers().getLedger(ledgerName)
            val ledgers: List<AbstractMicroledger> =
                java.util.Arrays.asList<AbstractMicroledger>(ledgerForA, ledgerForB, ledgerForC)
            for (ledger in ledgers) {
                val allTxns: List<Transaction> = ledger.getAllTransactions()
                assertEquals(5, allTxns.size.toLong())
                assertTrue(allTxns.toString().contains("op3"))
                assertTrue(allTxns.toString().contains("op4"))
                assertTrue(allTxns.toString().contains("op5"))
            }
        } finally {
            agentA.close()
            agentB.close()
            agentC.close()
        }
    }
}
