
package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCache
import com.sirius.library.agent.wallet.abstract_wallet.model.CacheOptions
import com.sirius.library.agent.wallet.abstract_wallet.model.PurgeOptions


expect class CacheMobile(wallet: LocalWallet) : AbstractCache {

    var timeoutSec : Long
    var poolMobile: PoolMobile

}

