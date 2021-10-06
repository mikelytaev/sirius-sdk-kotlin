package com.sirius.library.agent.aries_rfc.feature_0048_trust_ping

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.agent.coprotocols.AbstractCloudCoProtocolTransport.Companion.THREAD_DECORATOR
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

/**
 * Implementation of Pong part for trust_ping protocol
 * https://github.com/hyperledger/aries-rfcs/tree/master/features/0048-trust-ping
 */
class Pong(message: String) : AriesProtocolMessage(message) {
    companion object {
        const val PROTOCOL = "trust_ping"
        fun builder(): Builder<*> {
            return PongBuilder()
        }

        init {
            Message.registerMessageClass(Pong::class, PROTOCOL, "ping_response")
        }
    }

    val comment: String?
        get() = getStringFromJSON("comment")

    abstract class Builder<B : Builder<B>> : AriesProtocolMessage.Builder<B>() {
        var comment: String? = null
        var pingId: String? = null
        fun setComment(comment: String?): B {
            this.comment = comment
            return self()
        }

        fun setPingId(pingId: String?): B {
            this.pingId = pingId
            return self()
        }

         override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            if (comment != null) {
                jsonObject.put("comment", comment)
            }
            if (pingId != null) {
                var thread: JSONObject? = jsonObject.optJSONObject(THREAD_DECORATOR)
                if (thread == null) thread = JSONObject()
                thread.put("thid", pingId)
                jsonObject.put(THREAD_DECORATOR, thread)
            }
            return jsonObject
        }

        fun build(): Pong {
            return Pong(generateJSON().toString())
        }
    }

    private class PongBuilder : Builder<PongBuilder>() {
        protected override fun self(): PongBuilder {
            return this
        }
    }
}
