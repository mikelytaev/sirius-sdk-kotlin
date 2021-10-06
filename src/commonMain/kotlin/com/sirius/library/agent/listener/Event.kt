package com.sirius.library.agent.listener

import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

class Event(pairwise: Pairwise?, message: String) : Message(message) {
    var pairwise: Pairwise?
    fun message(): Message? {
        if (getMessageObj()?.has("message") ==true) {
            val msgJson: JSONObject? = getMessageObj()?.getJSONObject("message")
            var restored: Message? =restoreMessageInstance(msgJson.toString())?.second
            return restored
        }
        return null
    }

    val recipientVerkey: String?
        get() = getMessageObj()?.optString("recipient_verkey")
    val senderVerkey: String?
        get() = getMessageObj()?.optString("sender_verkey")

    fun getPairwise(): Pairwise? {
        return pairwise
    }

    init {
        this.pairwise = pairwise
    }
}