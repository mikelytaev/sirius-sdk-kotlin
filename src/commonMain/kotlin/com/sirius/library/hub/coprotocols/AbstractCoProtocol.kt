package com.sirius.library.hub.coprotocols

import com.sirius.library.agent.coprotocols.AbstractCoProtocolTransport
import com.sirius.library.hub.Closeable
import com.sirius.library.hub.Context

abstract class AbstractCoProtocol protected constructor(context: Context) : Closeable {
    var timeToLiveSec = 60
    var isAborted = false
    var started = false
    var transport: AbstractCoProtocolTransport? = null
    var context: Context
    override fun close() {
        if (started) {
            transport?.stop()
            started = false
            transport = null
        }
    }

    init {
        this.context = context
    }
}