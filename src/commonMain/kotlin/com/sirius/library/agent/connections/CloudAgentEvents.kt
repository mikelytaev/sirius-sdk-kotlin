package com.sirius.library.agent.connections

import com.sirius.library.encryption.P2PConnection
import com.sirius.library.errors.sirius_exceptions.SiriusConnectionClosed
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.messaging.Message
import com.sirius.library.utils.*

/**
 * RPC service.
 *
 *
 * Reactive nature of Smart-Contract design
 */
expect class CloudAgentEvents(serverAddress: String, credentials: ByteArray?, p2p: P2PConnection?, timeout: Int) :
    BaseAgentConnection, AgentEvents {
    override var log: Logger
    var tunnel: String?
    var balancingGroup: String?

    override fun path(): String

    override fun setup(context: Message)

    @Throws(SiriusConnectionClosed::class, SiriusInvalidPayloadStructure::class)
    override fun pull(): CompletableFutureKotlin<Message?>?
}
