package com.sirius.library.agent.listener

import com.sirius.library.agent.AbstractAgent
import com.sirius.library.agent.connections.AgentEvents
import com.sirius.library.agent.pairwise.AbstractPairwiseList
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.utils.CompletableFutureKotlin

actual class Listener actual constructor(actual var source: AgentEvents, actual var agent: AbstractAgent) {

    actual var pairwiseResolver: AbstractPairwiseList?
        get() = agent.pairwiseList
        set(value) {}



    actual fun unsubscribe() {
        agent.unsubscribe(this)
    }

    actual val one: CompletableFutureKotlin<Event>?

        get() {
            println("source="+source)
            return source.pull()?.thenApply { msg ->
                val theirVerkey: String? = msg?.getStringFromJSON("sender_verkey")
                var pairwise: Pairwise? = null
                if (pairwiseResolver != null && theirVerkey != null) {
                    pairwise = pairwiseResolver?.loadForVerkey(theirVerkey)
                }
                Event(pairwise, msg?.serialize()?:"")
            }
        }







}