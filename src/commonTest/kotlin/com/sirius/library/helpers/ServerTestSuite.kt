package com.sirius.library.helpers

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.model.Entity
import com.sirius.library.base.JsonMessage
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.models.AgentParams
import com.sirius.library.utils.JSONObject

class ServerTestSuite {
    var SETUP_TIMEOUT = 60
    var serverAddress: String
    var url: String
    var metadata: String?
    var testSuitePath: String?
    var testSuiteExistsLocally = false
    fun getAgentParams(name: String?): AgentParams {
        if (metadata == null || metadata!!.isEmpty()) {
            throw RuntimeException("TestSuite is not running...")
        }
        val agentObject = JsonMessage(metadata)
        val agent: JSONObject = agentObject.getJSONOBJECTFromJSON(name)
        if (agent == null || agent.isEmpty()) {
            throw RuntimeException(String.format("TestSuite does not have agent with name %s", name))
        }
        val p2pObject: JSONObject = agent.getJSONObject("p2p")
        val credentials: String = agent.getString("credentials")
        val entitiesObject: JSONObject = agent.getJSONObject("entities")
        val entityList: MutableList<Entity> = ArrayList<Entity>()
        if (entitiesObject != null) {
            val keys: Set<String> = entitiesObject.keySet()
            for (key in keys) {
                val entityObject: JSONObject = entitiesObject.getJSONObject(key)
                val seed: String = entityObject.getString("seed")
                val verkey: String = entityObject.getString("verkey")
                val did: String = entityObject.getString("did")
                entityList.add(Entity(key, seed, verkey, did))
            }
        }
        val smartContractObject: JSONObject = p2pObject.getJSONObject("smart_contract")
        val agentP2pObject: JSONObject = p2pObject.getJSONObject("agent")
        val myVerKey: String = smartContractObject.getString("verkey")
        val mySecretKey: String = smartContractObject.getString("secret_key")
        val theirVerkey: String = agentP2pObject.getString("verkey")
        val connection = P2PConnection(myVerKey, mySecretKey, theirVerkey)
        return AgentParams(serverAddress, credentials, connection, entityList)
    }

    fun ensureIsAlive() {
        val (first, second) = httpGet(url)
        if (first) {
            metadata = second
        } else {
            if (testSuiteExistsLocally) {
                runSuiteLocally()
            }
        }
    }

    fun runSuiteLocally() {}
    fun httpGet(url: String?): Pair<Boolean, String> {
        return try {
            val httpclient: CloseableHttpClient = HttpClients.createDefault()
            val httpGet = HttpGet(url)
            val response1: CloseableHttpResponse = httpclient.execute(httpGet)
            try {
                val entity1: HttpEntity = response1.getEntity()
                val `in`: java.io.BufferedReader = java.io.BufferedReader(
                    java.io.InputStreamReader(
                        entity1.getContent()
                    )
                )
                var inputLine: String?
                val response: java.lang.StringBuffer = java.lang.StringBuffer()
                while (`in`.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                `in`.close()
                EntityUtils.consume(entity1)
                // print result
                Pair(true, response.toString())
            } finally {
                response1.close()
            }
        } catch (e: java.net.MalformedURLException) {
            e.printStackTrace()
            Pair(false, e.message)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            Pair(false, e.message)
        }
    }

    companion object {
        fun newInstance(): ServerTestSuite {
            return ServerTestSuite()
        }

        fun getFirstEndpointAddressWIthEmptyRoutingKeys(agent: CloudAgent): String {
            for (e in agent.getEndpoints()) {
                if (e.routingKeys.size === 0) {
                    return e.address
                }
            }
            return ""
        }
    }

    init {
        serverAddress = ConfTest.singletonInstance.test_suite_baseurl
        url = "$serverAddress/test_suite"
        metadata = null
        testSuitePath = java.lang.System.getenv("TEST_SUITE")
        if (testSuitePath == null) {
            testSuiteExistsLocally = false
        } else {
            //testSuiteExistsLocally = System.path.isfile(test_suite_path) and 'localhost' in self.__server_address
        }
    }
}

