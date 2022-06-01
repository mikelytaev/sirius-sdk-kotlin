package com.sirius.sdk.encryption

import com.danubetech.keyformats.crypto.ByteSigner
import com.danubetech.keyformats.jose.JWSAlgorithm
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto

class IndyWalletSigner(crypto: AbstractCrypto, verkey: String) : ByteSigner(JWSAlgorithm.EdDSA) {
    var crypto: AbstractCrypto
    var verkey: String

    init {
        this.crypto = crypto
        this.verkey = verkey
    }

    @Throws(java.security.GeneralSecurityException::class)
    protected fun sign(bytes: ByteArray?): ByteArray {
        return crypto.cryptoSign(verkey, bytes)
    }
}