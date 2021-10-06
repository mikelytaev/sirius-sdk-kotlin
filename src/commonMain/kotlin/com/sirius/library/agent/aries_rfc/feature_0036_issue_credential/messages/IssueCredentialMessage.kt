package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages

import com.sirius.library.messaging.Message
import com.sirius.library.utils.Base64
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class IssueCredentialMessage(message: String) : BaseIssueCredentialMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return IssueCredentialMessageBuilder()
        }

        init {
            Message.registerMessageClass(IssueCredentialMessage::class, PROTOCOL, "issue-credential")
        }
    }

    fun cred(): JSONObject? {
        val attach: JSONObject? = attach
        if (attach != null) {
            val b64: String? = attach.optJSONObject("data")?.optString("base64")
            return JSONObject(String(Base64.getDecoder().decode(b64)))
        }
        return null
    }

    fun credId(): String? {
        return attach?.optString("@id")
    }

    protected val attach: JSONObject?
        protected get() {
            var attach: JSONObject? = getJSONOBJECTFromJSON("credentials~attach")
            if (attach == null) {
                val arr: JSONArray = getJSONArrayFromJSON("credentials~attach", JSONArray()) ?: JSONArray()
                if (arr.length() > 0) {
                    attach = arr.getJSONObject(0)
                }
            }
            return attach
        }

    abstract class Builder<B : Builder<B>?> :
        BaseIssueCredentialMessage.Builder<B>() {
        var cred: JsonObject? = null
        var credId: String? = null
        fun setCred(cred: JsonObject?): B? {
            this.cred = cred
            return self()
        }

        fun setCredId(credId: String?): B? {
            this.credId = credId
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            val id: String? = jsonObject.optString("id")
            if (cred != null) {
                val messageId = if (credId != null) credId!! else "libindy-cred-$id"
                val credAttach = JSONObject()
                credAttach.put("@id", messageId)
                credAttach.put("mime-type", "application/json")
                val data = JSONObject()
                val base64: ByteArray = Base64.getEncoder()
                    .encode(cred.toString().encodeToByteArray())

                data.put("base64", String(base64))
                credAttach.put("data", data)
                val attaches = JSONArray()
                attaches.put(credAttach)
                jsonObject.put("credentials~attach", attaches)
            }
            return jsonObject
        }

        fun build(): IssueCredentialMessage {
            return IssueCredentialMessage(generateJSON().toString())
        }
    }

    private class IssueCredentialMessageBuilder :
        Builder<IssueCredentialMessageBuilder?>() {
        override fun self(): IssueCredentialMessageBuilder {
            return this
        }
    }
}
