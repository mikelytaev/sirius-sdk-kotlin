package com.sirius.library.agent.wallet

import Indy.IndyHandle
import Indy.IndyPool


actual class LocalPool(var poolHandle: IndyHandle?) {
    var pool : IndyPool? = null
}