
package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.utils.JSONArray


expect class CryptoMobile(wallet: LocalWallet) : AbstractCrypto {

    var timeoutSec : Long

}

