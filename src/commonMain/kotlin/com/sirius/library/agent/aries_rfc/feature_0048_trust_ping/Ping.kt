package com.sirius.library.agent.aries_rfc.feature_0048_trust_ping

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.agent.aries_rfc.feature_0037_present_proof.messages.RequestPresentationMessage
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

/**Implementation of Ping part for trust_ping protocol
 * https://github.com/hyperledger/aries-rfcs/tree/master/features/0048-trust-ping
 */
class Ping(message: String) : AriesProtocolMessage(message) {
    companion object {
        const val PROTOCOL = "trust_ping"
        fun builder(): Builder<*> {
            return PingBuilder()
        }


    }

    val comment: String?
        get() = getStringFromJSON("comment")
    val responseRequested: Boolean?
        get() = getBooleanFromJSON("response_requested")

    abstract class Builder<B : Builder<B>> : AriesProtocolMessage.Builder<B>() {
        var comment: String? = null
        var responseRequested: Boolean? = null
        fun setComment(comment: String?): B {
            this.comment = comment
            return self()
        }

        fun setResponseRequested(responseRequested: Boolean): B {
            this.responseRequested = responseRequested
            return self()
        }

         override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            if (comment != null) {
                jsonObject.put("comment", comment)
            }
            if (responseRequested != null) {
                jsonObject.put("response_requested", responseRequested)
            }
            return jsonObject
        }

        fun build(): Ping {
            return Ping(generateJSON().toString())
        }
    }

    private class PingBuilder : Builder<PingBuilder>() {
        protected override fun self(): PingBuilder {
            return this
        }

        override fun getClass(): KClass<out Message> {
            return Ping::class
        }
    }
}
