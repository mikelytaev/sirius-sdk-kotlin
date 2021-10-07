package com.sirius.library.agent.coprotocols

import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidMessage
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.errors.sirius_exceptions.SiriusPendingOperation
import com.sirius.library.messaging.Message

abstract class AbstractCoProtocolTransport {
    class GetOneResult(message: Message, senderVerkey: String, recipientVerkey: String) {
        var message: Message
        var senderVerkey: String
        var recipientVerkey: String

        init {
            this.message = message
            this.senderVerkey = senderVerkey
            this.recipientVerkey = recipientVerkey
        }
    }

    var timeToLiveSec = 60
    var protocols: List<String> = ArrayList<String>()

    abstract fun start()
    abstract fun stop()
    @Throws(SiriusPendingOperation::class, SiriusInvalidPayloadStructure::class, SiriusInvalidMessage::class)
    abstract fun sendAndWait(message: Message): Pair<Boolean, Message?>

    @get:Throws(SiriusInvalidPayloadStructure::class)
    abstract val one: GetOneResult?

    @Throws(SiriusPendingOperation::class)
    abstract fun send(message: Message)
    @Throws(SiriusPendingOperation::class)
    abstract fun sendMany(message: Message, to: List<Pairwise>): List<Pair<Boolean, String?>>
    fun setTimeToLiveSeci(timeToLiveSec: Int) {
        this.timeToLiveSec = timeToLiveSec
    }
}