package com.sirius.library.base

import com.sirius.library.hub.Context
import com.sirius.library.hub.coprotocols.AbstractCoProtocol

abstract class AbstractStateMachine(protected var context: Context) {
    var timeToLiveSec = 60
    protected var coprotocols: List<AbstractCoProtocol> = ArrayList<AbstractCoProtocol>()
    abstract fun protocols(): List<String?>?
}