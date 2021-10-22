package com.sirius.library.hub.coprotocols

import com.sirius.library.errors.sirius_exceptions.SiriusInvalidMessage
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.errors.sirius_exceptions.SiriusPendingOperation
import com.sirius.library.hub.Context
import com.sirius.library.messaging.Message

abstract class AbstractP2PCoProtocol protected constructor(context: Context<*>) : AbstractCoProtocol(context) {
    @Throws(SiriusPendingOperation::class)
    abstract fun send(message: Message)
    @Throws(SiriusInvalidPayloadStructure::class, SiriusInvalidMessage::class, SiriusPendingOperation::class)
    abstract fun sendAndWait(message: Message): Pair<Boolean, Message?>
}