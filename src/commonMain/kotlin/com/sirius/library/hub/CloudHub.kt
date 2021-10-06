package com.sirius.library.hub

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.connections.BaseAgentConnection
import com.sirius.library.encryption.P2PConnection

class CloudHub(config: Config?) : AbstractHub() {
    class Config : AbstractHub.Config() {
        var serverUri: String = ""
        var credentials: ByteArray? = null
        var p2p: P2PConnection? = null
        var timeout: Int = BaseAgentConnection.IO_TIMEOUT
    }

    val serverUri: String?
        get() = (config as Config).serverUri

    fun setServerUri(serverUri: String): CloudHub {
        (config as Config?)!!.serverUri = serverUri
        return this
    }

    val credentials: ByteArray?
        get() = (config as Config).credentials

    fun setCredentials(credentials: ByteArray): CloudHub {
        (config as Config?)!!.credentials = credentials
        return this
    }

    val connection: P2PConnection?
        get() = (config as Config).p2p

    fun setConnection(connection: P2PConnection?): CloudHub {
        (config as Config?)!!.p2p = connection
        return this
    }

    fun setTimeout(timeout: Int): CloudHub {
        (config as Config?)!!.timeout = timeout
        return this
    }

    override fun createAgentInstance() {
        agent = CloudAgent(
            (config as Config).serverUri,
            (config as Config).credentials,
            (config as Config).p2p,
            (config as Config).timeout,
            config!!.storage
        )
        agent!!.open()
    }

    init {
        this.config = config
        createAgentInstance()
    }
}
