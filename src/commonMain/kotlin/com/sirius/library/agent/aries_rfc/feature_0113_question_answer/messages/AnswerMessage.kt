package com.sirius.library.agent.aries_rfc.feature_0113_question_answer.messages

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

class AnswerMessage(msg: String) : AriesProtocolMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return MessageBuilder()
        }

        init {
            Message.registerMessageClass(AnswerMessage::class, "questionanswer", "answer")
        }
    }

    val response: String?
        get() = getMessageObjec().optString("response")

    fun setOutTime() {
        var timing: JSONObject? = getMessageObjec().optJSONObject("~timing")
        if (timing == null) {
            timing = JSONObject()
            getMessageObjec().put("~timing", timing)
        }
        val timeIso: String = ""
        //TODO java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ISO_INSTANT)
        timing.put("out_time", timeIso)
    }

    abstract class Builder<B : Builder<B>> : AriesProtocolMessage.Builder<B>() {
        var response: String? = null
        fun setResponse(response: String?): B {
            this.response = response
            return self()
        }

         override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            if (response != null) {
                jsonObject.put("response", response)
            }
            return jsonObject
        }

        fun build(): AnswerMessage {
            return AnswerMessage(generateJSON().toString())
        }
    }

    private class MessageBuilder : Builder<MessageBuilder>() {
        protected override fun self(): MessageBuilder {
            return this
        }
    }
}
