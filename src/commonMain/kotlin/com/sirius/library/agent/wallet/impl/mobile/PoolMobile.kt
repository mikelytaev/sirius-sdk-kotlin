
package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalPool
import com.sirius.library.hub.MobileContext

const val POOL_PROTOCOL_VERSION = 2

expect class PoolMobile {

    fun getPoolHandle(name: String): LocalPool?

    companion object {
        var openedPoolRegistry: MutableMap<String, LocalPool>
        fun registerPool(name: String?, genesisFilePath: String?)

        fun setProtocolVersion(version : Int = POOL_PROTOCOL_VERSION)

    }
}

