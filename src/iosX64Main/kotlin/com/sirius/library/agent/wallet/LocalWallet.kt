package com.sirius.library.agent.wallet

import Indy.IndyHandle
import Indy.IndyWallet


actual class  LocalWallet(var walletHandle: IndyHandle = 0) {
    val wallet : IndyWallet = IndyWallet()
}