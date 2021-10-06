package com.sirius.library.agent.connections

import com.sirius.library.errors.sirius_exceptions.SiriusConnectionClosed
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure

interface AgentEvents {
    @Throws(SiriusConnectionClosed::class, SiriusInvalidPayloadStructure::class)
    fun pull(): CompletableFuture<Message?>?
}