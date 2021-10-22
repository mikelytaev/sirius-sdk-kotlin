
package com.sirius.library.agent.wallet

import com.sirius.library.agent.wallet.abstract_wallet.*
import com.sirius.library.agent.wallet.impl.*
import org.hyperledger.indy.sdk.wallet.Wallet


class MobileWallet(var wallet: Wallet) : AbstractWallet {
    override var anoncreds: AbstractAnonCreds = AnonCredsMobile(wallet)
    override var did: AbstractDID = DIDMobile(wallet)
    override var crypto: AbstractCrypto = CryptoMobile(wallet)
    override var cache: AbstractCache = CacheMobile(wallet)
    override var ledger: AbstractLedger = LedgerMobile(wallet)
    override var nonSecrets: AbstractNonSecrets =  NonSecretsMobile(wallet)
    override var pairwise: AbstractPairwise = PairwiseMobile(wallet, nonSecrets as NonSecretsMobile)

}

