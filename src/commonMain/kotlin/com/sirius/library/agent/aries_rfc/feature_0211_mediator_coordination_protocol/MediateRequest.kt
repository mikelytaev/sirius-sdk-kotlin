package com.sirius.library.agent.aries_rfc.feature_0211_mediator_coordination_protocol

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

class MediateRequest(message: String) : CoordinateMediationMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return MediateRequestMessageBuilder()
        }

    }

    abstract class Builder<B : Builder<B>> : CoordinateMediationMessage.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): MediateRequest {
            return MediateRequest(generateJSON().toString())
        }
    }

    private class MediateRequestMessageBuilder : Builder<MediateRequestMessageBuilder>() {
        protected override fun self(): MediateRequestMessageBuilder {
            return this
        }

        override fun getClass(): KClass<out Message> {
            return MediateRequest::class
        }
    }
}