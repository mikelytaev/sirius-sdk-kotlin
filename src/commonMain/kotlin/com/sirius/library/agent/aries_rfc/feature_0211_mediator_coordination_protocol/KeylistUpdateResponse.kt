package com.sirius.library.agent.aries_rfc.feature_0211_mediator_coordination_protocol

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

class KeylistUpdateResponse(message: String) : CoordinateMediationMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return KeylistResponseMessageBuilder()
        }

        init {
            Message.registerMessageClass(KeylistUpdateResponse::class, PROTOCOL, "keylist-update-response")
        }
    }

    abstract class Builder<B : Builder<B>> :
        CoordinateMediationMessage.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): KeylistUpdateResponse {
            return KeylistUpdateResponse(generateJSON().toString())
        }
    }

    private class KeylistResponseMessageBuilder :
        Builder<KeylistResponseMessageBuilder>() {
        protected override fun self(): KeylistResponseMessageBuilder {
            return this
        }
    }
}
