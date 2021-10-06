/*
package com.sirius.library.agent.wallet

import com.sirius.library.agent.wallet.impl.*


class MobileWallet(wallet: Wallet?) : AbstractWallet {
    override var anoncreds: AbstractAnonCreds
    override var did: AbstractDID
    override var crypto: AbstractCrypto
    override var cache: AbstractCache
    override var ledger: AbstractLedger
    override var pairwise: AbstractPairwise
    override var nonSecrets: AbstractNonSecrets
    fun getDid(): AbstractDID {
        return did
    }

    fun crypto: AbstractCrypto {
        return crypto
    }

    fun getCache(): AbstractCache {
        return cache
    }

    fun getLedger(): AbstractLedger {
        return ledger
    }

    fun getPairwise(): AbstractPairwise {
        return pairwise
    }

    fun getAnoncreds(): AbstractAnonCreds {
        return anoncreds
    }

    fun nonSecrets: AbstractNonSecrets {
        return nonSecrets
    }

    init {
        anoncreds = AnonCredsMobile(wallet)
        did = DIDMobile(wallet)
        crypto = CryptoMobile(wallet)
        cache = CacheMobile(wallet)
        ledger = LedgerMobile(wallet)
        nonSecrets = NonSecretsMobile(wallet)
        pairwise = PairwiseMobile(wallet, nonSecrets as NonSecretsMobile)
    }
}
*/
