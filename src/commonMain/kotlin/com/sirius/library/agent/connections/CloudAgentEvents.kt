package com.sirius.library.agent.connections

/**
 * RPC service.
 *
 *
 * Reactive nature of Smart-Contract design
 */
class CloudAgentEvents(serverAddress: String?, credentials: ByteArray?, p2p: P2PConnection?, timeout: Int) :
    BaseAgentConnection(serverAddress, credentials, p2p, timeout), AgentEvents {
    override var log: java.util.logging.Logger =
        java.util.logging.Logger.getLogger(CloudAgentEvents::class.java.getName())
    var tunnel: String? = null
    var balancingGroup: String? = null

    override fun path(): String {
        return "events"
    }

    override fun setup(context: Message) {
        super.setup(context)
        // Extract load balancing info
        val balancing: JSONArray = context.getJSONArrayFromJSON("~balancing", JSONArray())
        for (i in 0 until balancing.length()) {
            val balance: JSONObject = balancing.getJSONObject(i)
            if ("kafka" == balance.getString("id")) {
                val jsonObject: JSONObject = balance.getJSONObject("data").getJSONObject("json")
                if (!jsonObject.isNull("group_id")) {
                    balancingGroup = jsonObject.getString("group_id")
                }
            }
        }
    }

    @Throws(SiriusConnectionClosed::class, SiriusInvalidPayloadStructure::class)
    override fun pull(): java.util.concurrent.CompletableFuture<Message> {
        if (!connector.isOpen()) {
            throw SiriusConnectionClosed("Open agent connection at first")
        }
        return connector.read().thenApply { data ->
            try {
                val payload: JSONObject =
                    JSONObject(String(data, java.nio.charset.StandardCharsets.US_ASCII))
                if (payload.has("protected")) {
                    val message: String = p2p.unpack(payload.toString())
                    //log.log(Level.INFO, "Received protected message. Unpacked: " + message);
                    return@thenApply Message(message)
                } else {
                    //log.log(Level.INFO, "Received message: " + payload);
                    return@thenApply Message(payload.toString())
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return@thenApply null
                //throw new SiriusInvalidPayloadStructure(e.getMessage());
            }
        }
    }
}
