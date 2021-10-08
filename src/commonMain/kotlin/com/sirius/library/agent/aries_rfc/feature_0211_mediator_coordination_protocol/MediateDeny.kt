package com.sirius.library.agent.aries_rfc.feature_0211_mediator_coordination_protocol

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

class MediateDeny(message: String) : CoordinateMediationMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return MediateDenyMessageBuilder()
        }


    }

    abstract class Builder<B : Builder<B>> : CoordinateMediationMessage.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): MediateDeny {
            return MediateDeny(generateJSON().toString())
        }
    }

    private class MediateDenyMessageBuilder : Builder<MediateDenyMessageBuilder>() {
        protected override fun self(): MediateDenyMessageBuilder {
            return this
        }

        override fun getClass(): KClass<out Message> {
            return MediateDeny::class
        }
    }
}