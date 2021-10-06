package com.sirius.library.agent.wallet

import com.sirius.library.agent.wallet.abstract_wallet.*

interface AbstractWallet {
    val did: AbstractDID
    val crypto: AbstractCrypto
    val cache: AbstractCache
    val ledger: AbstractLedger
    val pairwise: AbstractPairwise
    val anoncreds: AbstractAnonCreds
    val nonSecrets: AbstractNonSecrets
}