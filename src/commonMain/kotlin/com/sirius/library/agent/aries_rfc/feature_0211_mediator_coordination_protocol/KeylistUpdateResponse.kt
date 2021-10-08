package com.sirius.library.agent.aries_rfc.feature_0211_mediator_coordination_protocol

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

class KeylistUpdateResponse(message: String) : CoordinateMediationMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return KeylistResponseMessageBuilder()
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

        override fun getClass(): KClass<out Message> {
            return KeylistUpdateResponse::class
        }
    }
}
