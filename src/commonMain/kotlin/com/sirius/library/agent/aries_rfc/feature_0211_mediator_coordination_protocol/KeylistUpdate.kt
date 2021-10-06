package com.sirius.library.agent.aries_rfc.feature_0211_mediator_coordination_protocol

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

class KeylistUpdate(message: String) : CoordinateMediationMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return KeylistUpdateMessageBuilder()
        }

        init {
            Message.registerMessageClass(KeylistUpdate::class, PROTOCOL, "keylist-update")
        }
    }

    abstract class Builder<B : Builder<B>> : CoordinateMediationMessage.Builder<B>() {
        var updates: JSONArray = JSONArray()
        fun addKey(key: String?): B {
            updates.put(JSONObject().put("action", "add").put("recipient_key", key))
            return self()
        }

        fun addKeys(keys: List<String?>): B {
            for (k in keys) self()?.addKey(k)
            return self()
        }

        fun removeKey(key: String?): B {
            updates.put(JSONObject().put("action", "remove").put("recipient_key", key))
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            jsonObject.put("updates", updates)
            return jsonObject
        }

        fun build(): KeylistUpdate {
            return KeylistUpdate(generateJSON().toString())
        }
    }

    private class KeylistUpdateMessageBuilder : Builder<KeylistUpdateMessageBuilder>() {
        protected override fun self(): KeylistUpdateMessageBuilder {
            return this
        }
    }
}
