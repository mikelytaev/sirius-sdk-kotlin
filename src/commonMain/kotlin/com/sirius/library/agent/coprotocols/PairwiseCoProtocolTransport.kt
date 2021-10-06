package com.sirius.library.agent.coprotocols

import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.messaging.Message

class PairwiseCoProtocolTransport(pairwise: Pairwise, rpc: AgentRPC) :
    AbstractCloudCoProtocolTransport(rpc) {
    var pairwise: Pairwise
    override fun start(protocols: List<String>, timeToLiveSec: Int) {
        super.start(protocols, timeToLiveSec)
        rpc.startProtocolForP2P(myVerkey, pairwise.their.verkey, protocols, timeToLiveSec)
    }

    override fun start(protocols: List<String>) {
        super.start(protocols)
        rpc.startProtocolForP2P(myVerkey, pairwise.their.verkey, protocols, timeToLiveSec)
    }

    override fun start(timeToLiveSec: Int) {
        super.start(timeToLiveSec)
        rpc.startProtocolForP2P(myVerkey, pairwise.their.verkey, protocols, timeToLiveSec)
    }

    override fun start() {
        super.start(protocols)
        rpc.startProtocolForP2P(myVerkey, pairwise.their.verkey, protocols, timeToLiveSec)
    }

    override fun stop() {
        super.stop()
        rpc.stopProtocolForP2P(myVerkey, pairwise.their.verkey, protocols, true)
    }

    init {
        this.pairwise = pairwise
        setup(
            pairwise.their.verkey,
            pairwise.their.endpointAddress,
            pairwise.me.verkey,
            pairwise.their.routingKeys
        )
    }
}
