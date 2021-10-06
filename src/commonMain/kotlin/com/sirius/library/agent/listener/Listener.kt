package com.sirius.library.agent.listener

import com.sirius.library.agent.AbstractAgent
import com.sirius.library.agent.connections.AgentEvents
import com.sirius.library.agent.pairwise.AbstractPairwiseList
import com.sirius.library.agent.pairwise.Pairwise

class Listener(source: AgentEvents, agent: AbstractAgent) {
    var source: AgentEvents
    var pairwiseResolver: AbstractPairwiseList?
    var agent: AbstractAgent
    val one: java.util.concurrent.CompletableFuture<Event>?
        get() {
            try {
                return source.pull().thenApply { msg ->
                    val theirVerkey: String = msg.getStringFromJSON("sender_verkey")
                    var pairwise: Pairwise? = null
                    if (pairwiseResolver != null && theirVerkey != null) {
                        pairwise = pairwiseResolver!!.loadForVerkey(theirVerkey)
                    }
                    Event(pairwise, msg.serialize())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    fun unsubscribe() {
        agent.unsubscribe(this)
    }

    init {
        this.source = source
        pairwiseResolver = agent.getPairwiseList()
        this.agent = agent
    }
}