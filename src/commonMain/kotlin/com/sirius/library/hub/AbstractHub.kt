package com.sirius.library.hub

import com.sirius.library.agent.AbstractAgent
import com.sirius.library.agent.microledgers.AbstractMicroledgerList
import com.sirius.library.agent.pairwise.AbstractPairwiseList
import com.sirius.library.agent.wallet.abstract_wallet.*
import com.sirius.library.storage.abstract_storage.AbstractImmutableCollection

abstract class AbstractHub : Closeable {
    open class Config {
        var crypto: AbstractCrypto? = null
        var microledgers: AbstractMicroledgerList? = null
        var pairwiseStorage: AbstractPairwiseList? = null
        var did: AbstractDID? = null
        var anoncreds: AbstractAnonCreds? = null
        var nonSecrets: AbstractNonSecrets? = null
        var storage: AbstractImmutableCollection? = null
        var cache: AbstractCache? = null
    }

    var config: Config? = null
    var agent: AbstractAgent? = null
    val nonSecrets: AbstractNonSecrets?
        get() = if (config?.nonSecrets != null) {
            config!!.nonSecrets
        } else {
            agent?.getWallet()?.nonSecrets
        }
    val crypto: AbstractCrypto?
        get() {
            return if (config?.crypto != null) {
                config!!.crypto
            } else {
                agent?.getWallet()?.crypto
            }
        }
    val did: AbstractDID?
        get() {
            return if (config?.did != null) {
                config!!.did
            } else {
                agentConnectionLazy?.getWallet()?.did
            }
        }
    val pairwiseList: AbstractPairwiseList?
        get() {
            return if (config?.pairwiseStorage != null) {
                config!!.pairwiseStorage
            } else {
                agentConnectionLazy?.getPairwiseList()
            }
        }
    val anonCreds: AbstractAnonCreds?
        get() {
            return if (config?.anoncreds != null) {
                config!!.anoncreds
            } else {
                agentConnectionLazy?.getWallet()?.anoncreds
            }
        }
    val cache: AbstractCache?
        get() {
            return if (config?.cache != null) {
                config!!.cache
            } else {
                agentConnectionLazy?.getWallet()?.cache
            }
        }
    val microledgers: AbstractMicroledgerList?
        get() {
            return if (config?.microledgers != null) {
                config!!.microledgers
            } else {
                agentConnectionLazy?.getMicroledgers()
            }
        }
    val agentConnectionLazy: AbstractAgent?
        get() {
            if (agent?.isOpen==false) {
                agent?.open()
            }
            return agent
        }

    fun getAgent(): AbstractAgent? {
        return agent
    }

    abstract fun createAgentInstance()
    override fun close() {
        if (agent != null) agent?.close()
    }
}
