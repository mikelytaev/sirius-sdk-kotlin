package com.sirius.library.agent.connections

import com.sirius.library.encryption.P2PConnection

abstract class WebSocketAgentConnection(
    serverAddress: String?,
    credentials: ByteArray?,
    p2p: P2PConnection?,
    timeout: Int
) :
    BaseAgentConnection(serverAddress, credentials, p2p, timeout)