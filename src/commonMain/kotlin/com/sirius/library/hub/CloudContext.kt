package com.sirius.library.hub

import com.sirius.library.agent.microledgers.AbstractMicroledgerList
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.encryption.P2PConnection

class CloudContext : Context {
    internal constructor(hub: AbstractHub) : super(hub) {}
    constructor(config: CloudHub.Config?) : super(CloudHub(config)) {}

    class CloudContextBuilder {
        var config: CloudHub.Config = CloudHub.Config()
        fun setCrypto(crypto: AbstractCrypto): CloudContextBuilder {
            config.crypto = crypto
            return this
        }

        fun setMicroledgers(microledgers: AbstractMicroledgerList): CloudContextBuilder {
            config.microledgers = microledgers
            return this
        }

        fun setServerUri(serverUri: String): CloudContextBuilder {
            config.serverUri = serverUri
            return this
        }

        fun setCredentials(credentials: ByteArray): CloudContextBuilder {
            config.credentials = credentials
            return this
        }

        fun setP2p(p2p: P2PConnection): CloudContextBuilder {
            config.p2p = p2p
            return this
        }

        fun setTimeoutSec(timeoutSec: Int): CloudContextBuilder {
            config.timeout = timeoutSec
            return this
        }

        fun build(): Context {
            return CloudContext(config)
        }
    }

    companion object {
        fun builder(): CloudContextBuilder {
            return CloudContextBuilder()
        }
    }
}
