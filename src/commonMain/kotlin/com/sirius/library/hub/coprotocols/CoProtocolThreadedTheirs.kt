package com.sirius.library.hub.coprotocols

import com.sirius.library.agent.coprotocols.AbstractCoProtocolTransport
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.errors.sirius_exceptions.SiriusContextError
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.errors.sirius_exceptions.SiriusPendingOperation
import com.sirius.library.hub.Context
import com.sirius.library.messaging.Message

class CoProtocolThreadedTheirs(
    context: Context<*>,
    thid: String,
    theirs: List<Pairwise>,
    pthid: String?,
    timeToLiveSec: Int
) :
    AbstractCoProtocol(context) {
    var thid: String
    var theirs: List<Pairwise>
    var pthid: String? = null
    var dids: MutableList<String> = ArrayList<String>()


    class SendResult(pairwise: Pairwise, success: Boolean, body: String?) {
        var pairwise: Pairwise
        var success: Boolean
        var body: String?

        init {
            this.pairwise = pairwise
            this.success = success
            this.body = body
        }
    }

    /**
     * Send message to given participants
     * @param message
     * @return
     */
    fun send(message: Message): List<SendResult> {
        val res: MutableList<SendResult> = ArrayList<SendResult>()
        try {
            val responces: List<Pair<Boolean, String?>> = transportLazy?.sendMany(
                message,
                theirs
            ).orEmpty()
            for (i in responces.indices) {
                val (first, second) = responces[i]
                res.add(SendResult(theirs[i], first, second))
            }
        } catch (siriusPendingOperation: SiriusPendingOperation) {
            siriusPendingOperation.printStackTrace()
        }
        return res
    }

    class GetOneResult(pairwise: Pairwise?, message: Message?) {
        var pairwise: Pairwise? = null
        var message: Message? = null

        init {
            this.pairwise = pairwise
            this.message = message
        }
    }

    /**
     * Read event from any of participants at given timeout
     * @return
     */
    val one: GetOneResult
        get() {
            try {
                val getOneResult: AbstractCoProtocolTransport.GetOneResult? = transportLazy?.one
                val p2p: Pairwise? = loadP2PFromVerkey(getOneResult?.senderVerkey)
                return GetOneResult(p2p, getOneResult?.message)
            } catch (siriusInvalidPayloadStructure: SiriusInvalidPayloadStructure) {
                siriusInvalidPayloadStructure.printStackTrace()
            }
            return GetOneResult(null, null)
        }

    class SendAndWaitResult(pairwise: Pairwise?, success: Boolean, message: Message?) {
        var pairwise: Pairwise?
        var success: Boolean
        var message: Message?

        init {
            this.pairwise = pairwise
            this.success = success
            this.message = message
        }
    }

    /**
     * Switch state while participants at given timeout give responses
     * @return
     */
    fun sendAndWait(message: Message): List<SendAndWaitResult> {
        val statuses = send(message)
        var resSize = 0
        for (sr in statuses) {
            if (sr.success) resSize++
        }
        var accum = 0
        val results: MutableList<SendAndWaitResult> = ArrayList<SendAndWaitResult>()
        while (accum < resSize) {
            val getOneResult = one
            if (getOneResult.pairwise == null) break
            if (dids.contains(getOneResult!!.pairwise!!.their.did)) {
                results.add(SendAndWaitResult(getOneResult.pairwise, true, getOneResult.message))
                accum++
            }
        }
        return results
    }

    private fun loadP2PFromVerkey(verkey: String?): Pairwise? {
        for (p2p in theirs) {
            if (p2p.their.verkey.equals(verkey)) return p2p
        }
        return null
    }

    private val transportLazy: AbstractCoProtocolTransport?
        private get() {
            if (transport == null) {
                transport = if (pthid == null) {
                    context.currentHub?.agentConnectionLazy?.spawn(thid)
                } else {
                    context.currentHub?.agentConnectionLazy?.spawn(thid, pthid!!)
                }
                transport?.timeToLiveSec=timeToLiveSec
                transport?.start()
                started = true
            }
            return transport
        }

    init {
        if (theirs.isEmpty()) {
            throw SiriusContextError("theirs is empty")
        }
        this.timeToLiveSec = timeToLiveSec
        this.thid = thid
        this.pthid = pthid
        this.theirs = theirs
        for (p in theirs) {
            p.their.did?.let{
                dids.add(it)
            }
        }
    }
}
