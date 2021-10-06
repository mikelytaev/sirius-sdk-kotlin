package com.sirius.library.helpers

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.microledgers.AbstractMicroledger
import com.sirius.library.agent.model.Entity
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.encryption.Custom
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.errors.sirius_exceptions.SiriusCryptoError
import com.sirius.library.models.AgentParams
import com.sirius.library.models.P2PModel
import com.sirius.library.rpc.AddressedTunnel
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.StringUtils
import com.sirius.library.utils.UUID

class ConfTest {
    var test_suite_baseurl: String? = null
    var test_suite_overlay_address: String? = null
    var old_agent_address: String? = null
    var old_agent_overlay_address: String? = null
    var old_agent_root: String? = null
    var custom: Custom = Custom
    fun configureTestEnv() {
        test_suite_baseurl = java.lang.System.getenv("TEST_SUITE_BASE_URL")
        if (test_suite_baseurl == null || test_suite_baseurl!!.isEmpty()) {
            test_suite_baseurl = "http://localhost"
        }
        test_suite_overlay_address = "http://10.0.0.90"
        old_agent_address = java.lang.System.getenv("INDY_AGENT_BASE_URL")
        if (old_agent_address == null || old_agent_address!!.isEmpty()) {
            old_agent_address = "http://127.0.0.1:88"
        }
        old_agent_overlay_address = "http://10.0.0.52:8888"
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("username", "root")
        jsonObject.put("password", "root")
        old_agent_root = jsonObject.toString()
    }

    fun createP2P(): Pair<P2PModel, P2PModel>? {
        try {
            val keysAgent: KeyPair =
                custom.createKeypair("000000000000000000000000000AGENT".toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
            val keysSdk: KeyPair =
                custom.createKeypair("00000000000000000000000000000SDK".toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
            val agent = P2PConnection(
                StringUtils.bytesToBase58String(keysAgent.getPublicKey().getAsBytes()),
                StringUtils.bytesToBase58String(keysAgent.getSecretKey().getAsBytes()),
                StringUtils.bytesToBase58String(keysSdk.getPublicKey().getAsBytes())
            )
            val smartContract = P2PConnection(
                StringUtils.bytesToBase58String(keysSdk.getPublicKey().getAsBytes()),
                StringUtils.bytesToBase58String(keysSdk.getSecretKey().getAsBytes()),
                StringUtils.bytesToBase58String(keysAgent.getPublicKey().getAsBytes())
            )
            val downstream = InMemoryChannel()
            val upstream = InMemoryChannel()
            val agentTunnel = AddressedTunnel("memory://agent->sdk", upstream, downstream, agent)
            val sdkTunnel = AddressedTunnel("memory://sdk->agent", downstream, upstream, smartContract)
            val agentModel = P2PModel(agent, agentTunnel)
            val sdkModel = P2PModel(smartContract, sdkTunnel)
            return Pair(agentModel, sdkModel)
        } catch (siriusCryptoError: SiriusCryptoError) {
            siriusCryptoError.printStackTrace()
        } catch (e: SodiumException) {
            e.printStackTrace()
        }
        return null
    }

    val suiteSingleton: ServerTestSuite
        get() {
            val serverTestSuite: ServerTestSuite = ServerTestSuite.newInstance()
            serverTestSuite.ensureIsAlive()
            return serverTestSuite
        }
    val indyAgentSingleton: IndyAgent
        get() = IndyAgent()

    fun getAgent(name: String?): CloudAgent {
        val params: AgentParams = suiteSingleton.getAgentParams(name)
        return CloudAgent(
            params.serverAddress, params.credentials.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
            params.getConnection(), 60, null, name
        )
    }

    fun testSuite(): ServerTestSuite {
        return suiteSingleton
    }

    fun indyAgent(): IndyAgent {
        return indyAgentSingleton
    }

    fun agent1(): CloudAgent {
        return getAgent("agent1")
    }

    fun agent2(): CloudAgent {
        return getAgent("agent2")
    }

    fun agent3(): CloudAgent {
        return getAgent("agent3")
    }

    fun agent4(): CloudAgent {
        return getAgent("agent4")
    }

    fun A(): CloudAgent {
        return getAgent("agent1")
    }

    fun B(): CloudAgent {
        return getAgent("agent2")
    }

    fun C(): CloudAgent {
        return getAgent("agent3")
    }

    fun D(): CloudAgent {
        return getAgent("agent4")
    }

    fun ledgerName(): String {
        return "Ledger-" + LazySodium.toHex(
            UUID.randomUUID.toString().toByteArray(java.nio.charset.StandardCharsets.US_ASCII)
        )
    }

    fun defaultNetwork(): String {
        return "default"
    }

    fun getPairwise(me: CloudAgent, their: CloudAgent): Pairwise {
        val suite: ServerTestSuite = suiteSingleton
        val myParams: AgentParams = suite.getAgentParams(me.name)
        val theirParams: AgentParams = suite.getAgentParams(their.name)
        val myEntity: Entity = myParams.getEntitiesList().get(0)
        val theirEntity: Entity = theirParams.getEntitiesList().get(0)
        val myEndpointAddress: String = ServerTestSuite.getFirstEndpointAddressWIthEmptyRoutingKeys(me)
        val theirEndpointAddress: String = ServerTestSuite.getFirstEndpointAddressWIthEmptyRoutingKeys(their)
        run {
            var pairwise: Pairwise? = me.getPairwiseList()?.loadForDid(theirEntity.did)
            val isFilled = pairwise != null && pairwise.getMetadata() != null
            if (!isFilled) {
                val me_ = Pairwise.Me(myEntity.did, myEntity.verkey)
                val their_ = Pairwise.Their(
                    theirEntity.did,
                    theirEntity.label,
                    theirEndpointAddress,
                    theirEntity.verkey,
                    ArrayList<String>()
                )
                val metadata: JSONObject = JSONObject().put(
                    "me",
                    JSONObject().put("did", myEntity.did).put("verkey", myEntity.verkey)
                ).put(
                    "their",
                    JSONObject().put("did", theirEntity.did).put("verkey", theirEntity.verkey)
                        .put("label", theirEntity.label).put(
                            "endpoint",
                            JSONObject().put("address", theirEndpointAddress)
                                .put("routing_keys",JSONArray())
                        )
                )
                pairwise = Pairwise(me_, their_, metadata)
                me.getWallet()?.did?.storeTheirDid(theirEntity.did, theirEntity.verkey)
                me.getPairwiseList()?.ensureExists(pairwise)
            }
        }
        run {
            var pairwise: Pairwise? = their.getPairwiseList()?.loadForDid(theirEntity.did)
            val isFilled = pairwise != null && pairwise?.getMetadata() != null
            if (!isFilled) {
                val me_ = Pairwise.Me(theirEntity.did, theirEntity.verkey)
                val their_ = Pairwise.Their(
                    myEntity.did,
                    myEntity.label,
                    myEndpointAddress,
                    myEntity.verkey,
                    ArrayList<String>()
                )
                val metadata: JSONObject = JSONObject().put(
                    "me",
                    JSONObject().put("did", theirEntity.did).put("verkey", theirEntity.verkey)
                ).put(
                    "their",
                   JSONObject().put("did", myEntity.did).put("verkey", myEntity.verkey)
                        .put("label", myEntity.label).put(
                            "endpoint",
                           JSONObject().put("address", myEndpointAddress)
                                .put("routing_keys", JSONArray())
                        )
                )
                pairwise = Pairwise(me_, their_, metadata)
                their.getWallet()?.did?.storeTheirDid(myEntity.did, myEntity.verkey)
                pairwise?.let {
                    their.getPairwiseList()?.ensureExists(it)
                }
            }
        }
        return me.getPairwiseList()!!.loadForDid(theirEntity.did)!!
    }

    companion object {
        private var instance: ConfTest? = null
        fun newInstance(): ConfTest {
            val confTest = ConfTest()
            confTest.configureTestEnv()
            return confTest
        }

        var proverMasterSecretName = "prover_master_secret_name"
        val singletonInstance: ConfTest?
            get() {
                if (instance == null) {
                    instance = newInstance()
                }
                return instance
            }

        fun getState(ledger: AbstractMicroledger): JSONObject {
            return JSONObject().put("name", ledger.name()).put("seq_no", ledger.seqNo())
                .put("size", ledger.size()).put("uncommitted_size", ledger.uncommittedSize())
                .put("root_hash", ledger.rootHash()).put("uncommitted_root_hash", ledger.uncommittedRootHash())
        }
    }
}
