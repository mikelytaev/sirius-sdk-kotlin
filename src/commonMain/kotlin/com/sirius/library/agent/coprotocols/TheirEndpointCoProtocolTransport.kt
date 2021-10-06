package com.sirius.library.agent.coprotocols

import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.pairwise.TheirEndpoint

class TheirEndpointCoProtocolTransport(myVerkey: String, endpoint: TheirEndpoint, rpc: AgentRPC) :
    AbstractCloudCoProtocolTransport(rpc) {
     var theirEndpoint: TheirEndpoint
    override fun start(protocols: List<String>, timeToLiveSec: Int) {
        super.start(protocols, timeToLiveSec)
        rpc.startProtocolForP2P(myVerkey, theirEndpoint.verkey, protocols, timeToLiveSec)
    }

    override fun start(protocols: List<String>) {
        super.start(protocols)
        rpc.startProtocolForP2P(myVerkey, theirEndpoint.verkey, protocols, timeToLiveSec)
    }

    override fun start(timeToLiveSec: Int) {
        super.start(timeToLiveSec)
        rpc.startProtocolForP2P(myVerkey, theirEndpoint.verkey, protocols, timeToLiveSec)
    }

    override fun start() {
        super.start(protocols)
        rpc.startProtocolForP2P(myVerkey, theirEndpoint.verkey, protocols, timeToLiveSec)
    }

    override fun stop() {
        super.stop()
        rpc.stopProtocolForP2P(myVerkey, theirEndpoint.verkey, protocols, true)
    }

    init {
        this.theirEndpoint = endpoint
        setup(endpoint.verkey, endpoint.endpointAddress, myVerkey, endpoint.routingKeys)
    }
}
