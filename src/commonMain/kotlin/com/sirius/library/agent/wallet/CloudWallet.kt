package com.sirius.library.agent.wallet

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.wallet.abstract_wallet.*
import com.sirius.library.agent.wallet.impl.*

class CloudWallet(agentRPC: AgentRPC) : AbstractWallet {
    override val did: AbstractDID
    override val crypto: AbstractCrypto
    override val cache: AbstractCache
    override val ledger: AbstractLedger
    override val pairwise: AbstractPairwise
    override val anoncreds: AbstractAnonCreds
    override val nonSecrets: AbstractNonSecrets
    var rpc: AgentRPC

    fun getDid(): AbstractDID {
        return did
    }

    fun getCrypto(): AbstractCrypto {
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

    fun getNonSecrets(): AbstractNonSecrets {
        return nonSecrets
    }

    fun generateWalletKey(seed: String): Any? {
        val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
            .add("seed", seed)
            .build()
        try {
            return rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/generate_wallet_key", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    init {
        rpc = agentRPC
        did = DIDProxy(rpc)
        crypto = CryptoProxy(rpc)
        cache = CacheProxy(rpc)
        pairwise = PairwiseProxy(rpc)
        nonSecrets = NonSecretsProxy(rpc)
        ledger = LedgerProxy(rpc)
        anoncreds = AnonCredsProxy(rpc)
    }


}

