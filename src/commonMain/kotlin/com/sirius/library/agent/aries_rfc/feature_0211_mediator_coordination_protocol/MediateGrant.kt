package com.sirius.library.agent.aries_rfc.feature_0211_mediator_coordination_protocol

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

class MediateGrant(message: String) : CoordinateMediationMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return MediateGrantMessageBuilder()
        }


    }

    val endpointAddress: String?
        get() = getMessageObjec().optString("endpoint")
    val routingKeys: List<String>
        get() {
            val res: MutableList<String> = ArrayList<String>()
            if (getMessageObjec().has("routing_keys")) {
                val keys: JSONArray = getMessageObjec().optJSONArray("routing_keys") ?: JSONArray()
                for (o in keys) res.add(o as String)
            }
            return res
        }

    abstract class Builder<B : Builder<B>> : CoordinateMediationMessage.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): MediateGrant {
            return MediateGrant(generateJSON().toString())
        }
    }

    private class MediateGrantMessageBuilder : Builder<MediateGrantMessageBuilder>() {
        protected override fun self(): MediateGrantMessageBuilder {
            return this
        }

        override fun getClass(): KClass<out Message> {
            return MediateGrant::class
        }
    }
}
