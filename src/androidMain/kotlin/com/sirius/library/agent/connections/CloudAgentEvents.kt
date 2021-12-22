package com.sirius.library.agent.connections

import com.sirius.library.base.CompleteFuture
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.errors.sirius_exceptions.SiriusConnectionClosed
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger
import com.sirius.library.utils.StringUtils

actual class CloudAgentEvents actual constructor(serverAddress: String, credentials: ByteArray?, p2p: P2PConnection?, timeout: Int) :
    BaseAgentConnection(serverAddress, credentials, p2p, timeout), AgentEvents {
    actual override var log: Logger =
        Logger.getLogger(CloudAgentEvents::class.simpleName)
    actual var tunnel: String? = null
    actual var balancingGroup: String? = null

    actual override fun path(): String {
        return "events"
    }

    actual override fun setup(context: Message) {
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


    actual override fun pull(): CompleteFuture<Message?>? {
        if (!connector!!.isOpen) {
            throw SiriusConnectionClosed("Open agent connection at first")
        }

        val future = connector!!.read().thenApply { data ->
            try {
                val payload: JSONObject =
                    JSONObject(StringUtils.bytesToString(data?: ByteArray(0), StringUtils.CODEC.US_ASCII))
                if (payload.has("protected")) {
                    val message = p2p!!.unpack(payload.toString())
                    //log.log(Level.INFO, "Received protected message. Unpacked: " + message);
                    return@thenApply Message(message!!)
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

        return future as CompleteFuture<Message?>
    }




}
