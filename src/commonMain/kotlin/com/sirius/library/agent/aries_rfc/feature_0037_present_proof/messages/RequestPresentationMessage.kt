package com.sirius.library.agent.aries_rfc.feature_0037_present_proof.messages

import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.AttribTranslation
import com.sirius.library.messaging.Message
import com.sirius.library.utils.Base64
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID

class RequestPresentationMessage(msg: String) : BasePresentProofMessage(msg) {
    companion object {
        private const val TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        fun builder(): Builder<*> {
            return RequestPresentationMessageBuilder()
        }

        init {
            Message.registerMessageClass(RequestPresentationMessage::class, PROTOCOL, "request-presentation")
        }
    }

    fun proofRequest(): JSONObject? {
        val obj: Any? = getMessageObj().get("request_presentations~attach")
        var attach: JSONObject? = null
        if (obj is JSONArray && !(obj as JSONArray).isEmpty()) {
            attach = (obj as JSONArray).getJSONObject(0)
        }
        if (obj is JSONObject) {
            attach = obj as JSONObject
        }
        if (attach != null && attach.has("data") && attach.getJSONObject("data")?.has("base64") ==true) {
            val rawBase64: String = attach.getJSONObject("data")?.getString("base64") ?:""
            return JSONObject(String(Base64.getDecoder().decode(rawBase64)))
        }
        return null
    }

    fun expiresTime(): java.util.Date? {
        val timing: JSONObject = getMessageObj().optJSONObject("~timing")
        if (timing != null) {
            val dateTimeStr: String = timing.optString("expires_time", "")
            if (!dateTimeStr.isEmpty()) {
                val df: java.text.DateFormat = java.text.SimpleDateFormat(TIME_FORMAT)
                try {
                    return df.parse(dateTimeStr)
                } catch (ignored: java.text.ParseException) {
                }
            }
        }
        return null
    }

    abstract class Builder<B : Builder<B>> :
        BasePresentProofMessage.Builder<B>() {
        var proofRequest: JSONObject? = null
        var translation: List<AttribTranslation>? = null
        var expiresTime: java.util.Date? = null
        fun setProofRequest(proofRequest: JSONObject?): B {
            this.proofRequest = proofRequest
            return self()
        }

        fun setTranslation(translation: List<AttribTranslation>?): B {
            this.translation = translation
            return self()
        }

        fun setExpiresTime(expiresTime: java.util.Date?): B {
            this.expiresTime = expiresTime
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            val id: String? = jsonObject.optString("id")
            if (proofRequest != null) {
                val base64: ByteArray = java.util.Base64.getEncoder()
                    .encode(proofRequest.toString().toByteArray(java.nio.charset.StandardCharsets.UTF_8))
                jsonObject.put(
                    "request_presentations~attach", JSONArray().put(
                        JSONObject().put("@id", "libindy-request-presentation-" + UUID.randomUUID)
                            .put("mime-type", "application/json").put(
                                "data",
                                JSONObject().put("base64", String(base64))
                            )
                    )
                )
            }
            if (translation != null && !translation!!.isEmpty()) {
                if (jsonObject.has("~attach")) jsonObject.remove("~attach")
                val attach: JSONObject =
                    JSONObject().put("@type", CREDENTIAL_TRANSLATION_TYPE).put("id", CREDENTIAL_TRANSLATION_ID).put(
                        "~l10n", JSONObject().put(
                            "locale",
                            locale
                        )
                    ).put("mime-type", "application/json")
                val data = JSONObject()
                val transArr = JSONArray()
                for (trans in translation!!) {
                    transArr.put(trans.getDict())
                }
                data.put("json", transArr)
                attach.put("data", data)
                jsonObject.put("~attach", JSONArray().put(attach))
            }
            if (expiresTime != null) {
                val timing = JSONObject()
                val df: java.text.DateFormat = java.text.SimpleDateFormat(TIME_FORMAT)
                timing.put("expires_time", df.format(expiresTime))
                jsonObject.put("~timing", timing)
            }
            return jsonObject
        }

        fun build(): RequestPresentationMessage {
            return RequestPresentationMessage(generateJSON().toString())
        }
    }

    private class RequestPresentationMessageBuilder :
        Builder<RequestPresentationMessageBuilder>() {
        protected override fun self(): RequestPresentationMessageBuilder {
            return this
        }
    }
}
