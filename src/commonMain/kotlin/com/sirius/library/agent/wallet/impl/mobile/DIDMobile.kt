
package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractDID


expect class DIDMobile(wallet: LocalWallet) : AbstractDID {

    var timeoutSec : Long
    var poolMobile: PoolMobile

}

