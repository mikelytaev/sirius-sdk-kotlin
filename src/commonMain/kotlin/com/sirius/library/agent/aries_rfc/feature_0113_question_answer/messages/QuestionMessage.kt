package com.sirius.library.agent.aries_rfc.feature_0113_question_answer.messages

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

class QuestionMessage(msg: String) : AriesProtocolMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return MessageBuilder()
        }

        init {
           Message.registerMessageClass(
                QuestionMessage::class,
                "questionanswer",
                "question"
            )
        }
    }

    val questionText: String?
        get() = getMessageObj().optString("question_text")
    val questionDetail: String?
        get() = getMessageObj().optString("question_detail")
    val nonce: String?
        get() = getMessageObj().optString("nonce")
    val isSignatureRequired: Boolean
        get() = getMessageObj().optBoolean("signature_required", false)
    val validResponses: List<String>
        get() {
            val responsesLis: MutableList<String> = ArrayList<String>()
            val responses: JSONArray? = getMessageObj().optJSONArray("valid_responses")
            if (responses != null) {
                for (i in 0 until responses.length()) {
                    val response: JSONObject? = responses.optJSONObject(i)
                    if (response != null) {
                        val validResponse: String? = response.optString("text")
                        validResponse?.let {
                            responsesLis.add(validResponse)
                        }

                    }
                }
            }
            return responsesLis
        }
    val content: String?
        get() = getMessageObj().optString("content")
    val expiresTime: java.time.ZonedDateTime?
        get() {
            val timing: JSONObject? = getMessageObj().optJSONObject("~timing")
            if (timing != null) {
                val expiresTimeStr: String = timing.optString("expires_time")
                if (!expiresTimeStr.isEmpty()) return java.time.ZonedDateTime.parse(expiresTimeStr)
            }
            return null
        }

    abstract class Builder<B : Builder<B>> : AriesProtocolMessage.Builder<B>() {
        var questionText: String? = null
        var questionDetail: String? = null
        var nonce: String? = null
        var signatureRequired = false
        var validResponses: List<String>? = null
        var expiresTime: java.util.Date? = null
        var ttlSec: Int? = null
        fun setQuestionText(text: String?): B {
            questionText = text
            return self()
        }

        fun setQuestionDetail(questionDetail: String?): B {
            this.questionDetail = questionDetail
            return self()
        }

        fun setExpiresTime(expiresTime: java.util.Date?): B {
            this.expiresTime = expiresTime
            return self()
        }

        fun setTtl(seconds: Int): B {
            ttlSec = seconds
            return self()
        }

        fun setNonce(nonce: String?): B {
            this.nonce = nonce
            return self()
        }

        fun setSignatureRequired(signatureRequired: Boolean): B {
            this.signatureRequired = signatureRequired
            return self()
        }

        fun setValidResponses(validResponses: List<String>?): B {
            this.validResponses = validResponses
            return self()
        }

         override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            val id: String? = jsonObject.optString("id")
            if (questionText != null) {
                jsonObject.put("question_text", questionText)
            }
            if (questionDetail != null) {
                jsonObject.put("question_detail", questionDetail)
            }
            if (nonce != null) {
                jsonObject.put("nonce", nonce)
            }
            jsonObject.put("signature_required", signatureRequired)
            if (ttlSec != null) {
                var timing: JSONObject? = jsonObject.optJSONObject("~timing")
                if (timing == null) {
                    timing = JSONObject()
                    jsonObject.put("~timing", timing)
                }
                val expiresTimeIso: String = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC).plusSeconds(
                    ttlSec!!.toLong()
                ).format(java.time.format.DateTimeFormatter.ISO_INSTANT)
                timing.put("expires_time", expiresTimeIso)
            }
            if (validResponses != null) {
                val validArray = JSONArray(validResponses!!)
                for (i in validResponses!!.indices) {
                    val responseObj = JSONObject()
                    responseObj.put("text", validResponses!![i])
                    validArray.put(responseObj)
                }
                jsonObject.put("valid_responses", validArray)
            }
            return jsonObject
        }

        fun build(): QuestionMessage {
            return QuestionMessage(generateJSON().toString())
        }
    }

    private class MessageBuilder : Builder<MessageBuilder>() {
        protected override fun self(): MessageBuilder {
            return this
        }
    }
}
