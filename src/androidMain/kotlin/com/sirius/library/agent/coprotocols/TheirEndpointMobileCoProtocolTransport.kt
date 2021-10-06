/*
package com.sirius.library.agent.coprotocols

import com.sirius.library.agent.MobileAgent
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.pairwise.TheirEndpoint
import com.sirius.library.errors.sirius_exceptions.SiriusPendingOperation
import com.sirius.library.messaging.Message

class TheirEndpointMobileCoProtocolTransport(agent: MobileAgent, myVerkey: String, endpoint: TheirEndpoint) :
    AbstractCoProtocolTransport() {
    var agent: MobileAgent
    var myVerkey: String
    var endpoint: TheirEndpoint
    var listener: Listener
    override fun start() {}
    override fun stop() {
        listener.unsubscribe()
    }

    override fun sendAndWait(message: Message?): Pair<Boolean, Message> {
        send(message)
        val r = one
        return if (r != null) {
            //if (r.senderVerkey.equals(endpoint.getVerkey())) {
            Pair(true, r.message)
            //}
        } else Pair(false, null)
    }

    override val one: GetOneResult?
        get() {
            try {
                val event: Event = listener.one.get(timeToLiveSec, java.util.concurrent.TimeUnit.SECONDS)
                return GetOneResult(event.message(), event.senderVerkey, event.recipientVerkey)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    @Throws(SiriusPendingOperation::class)
    override fun send(message: Message?) {
        agent.sendMessage(
            message,
            listOf(endpoint.verkey),
            endpoint.endpointAddress,
            myVerkey,
            listOf()
        )
    }

    override fun sendMany(message: Message?, to: List<Pairwise?>?): List<Pair<Boolean, String>>? {
        return null
    }

    init {
        this.agent = agent
        this.myVerkey = myVerkey
        this.endpoint = endpoint
        listener = agent.subscribe()
    }
}
*/
