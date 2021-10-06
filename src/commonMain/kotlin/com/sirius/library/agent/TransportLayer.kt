package com.sirius.library.agent

import com.sirius.library.agent.coprotocols.AbstractCoProtocolTransport
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.pairwise.TheirEndpoint

abstract class TransportLayer {
    abstract fun spawn(my_verkey: String, endpoint: TheirEndpoint): AbstractCoProtocolTransport?
    abstract fun spawn(pairwise: Pairwise): AbstractCoProtocolTransport?
    abstract fun spawn(thid: String, pairwise: Pairwise): AbstractCoProtocolTransport?
    abstract fun spawn(thid: String): AbstractCoProtocolTransport?
    abstract fun spawn(thid: String, pairwise: Pairwise, pthid: String): AbstractCoProtocolTransport?
    abstract fun spawn(thid: String, pthid: String): AbstractCoProtocolTransport?
}