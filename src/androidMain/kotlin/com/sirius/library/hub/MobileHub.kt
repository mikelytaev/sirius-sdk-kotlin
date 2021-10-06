/*
package com.sirius.library.hub

class MobileHub(config: Config) : AbstractHub() {
    class Config : AbstractHub.Config() {
        var walletConfig: JSONObject? = null
        var walletCredentials: JSONObject? = null
        var mediatorInvitation: Invitation? = null
        var indyEndpoint: String? = null
        var serverUri: String? = null
        var sender: BaseSender? = null
    }

    var serverUri: String? = null
    override fun getAgent(): MobileAgent? {
        return super.getAgent() as MobileAgent?
    }

    override fun createAgentInstance() {
        val mobileConfig = config as Config
        agent = MobileAgent(
            mobileConfig.walletConfig,
            mobileConfig.walletCredentials
        )
        val points: MutableList<Endpoint> = ArrayList<Endpoint>()
        if ((config as Config).indyEndpoint != null && !(config as Config).indyEndpoint!!.isEmpty()) {
            points.add(Endpoint((config as Config).indyEndpoint, ArrayList<E>(), true))
        }
        getAgent().setEndpoints(points)
        getAgent().setSender((config as Config).sender)
        getAgent().create()
        getAgent().open()
    }

    init {
        this.config = config
        serverUri = config.serverUri
        createAgentInstance()
    }
}
*/
