package com.sirius.library.agent.connections

import com.sirius.library.encryption.P2PConnection
import com.sirius.library.errors.sirius_exceptions.SiriusConnectionClosed
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger
import com.sirius.library.utils.StringCodec

/**
 * RPC service.
 *
 *
 * Reactive nature of Smart-Contract design
 */
class CloudAgentEvents(serverAddress: String, credentials: ByteArray?, p2p: P2PConnection?, timeout: Int) :
    BaseAgentConnection(serverAddress, credentials, p2p, timeout), AgentEvents {
     override var log: Logger =
       Logger.getLogger(CloudAgentEvents::class.simpleName)
    var tunnel: String? = null
    var balancingGroup: String? = null

    override fun path(): String {
        return "events"
    }

    override fun setup(context: Message) {
        super.setup(context)
        // Extract load balancing info
        val balancing: JSONArray = context.getJSONArrayFromJSON("~balancing", JSONArray()) ?: JSONArray()
        for (i in 0 until balancing.length()) {
            val balance: JSONObject = balancing.getJSONObject(i) ?: JSONObject()
            if ("kafka" == balance.getString("id")) {
                val jsonObject: JSONObject = balance.getJSONObject("data")?.getJSONObject("json") ?: JSONObject()
                if (!jsonObject.isNull("group_id")) {
                    balancingGroup = jsonObject.getString("group_id")
                }
            }
        }
    }

    @Throws(SiriusConnectionClosed::class, SiriusInvalidPayloadStructure::class)
    override fun pull(): java.util.concurrent.CompletableFuture<Message> {
        if (!connector!!.isOpen) {
            throw SiriusConnectionClosed("Open agent connection at first")
        }
        return connector?.read().thenApply { data ->
            try {
                val codec = StringCodec()
                val payload: JSONObject =
                    JSONObject(codec.fromByteArrayToASCIIString(data))
                if (payload.has("protected")) {
                    val message: String = p2p?.unpack(payload.toString()) ?:""
                    //log.log(Level.INFO, "Received protected message. Unpacked: " + message);
                    return@thenApply Message(message)
                } else {
                    //log.log(Level.INFO, "Received message: " + payload);
                    return@thenApply Message(payload.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@thenApply null
                //throw new SiriusInvalidPayloadStructure(e.getMessage());
            }
        }
    }
}
