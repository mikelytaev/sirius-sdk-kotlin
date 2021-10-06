package com.sirius.library.agent.microledgers

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.utils.JSONObject

class BatchedAPI(api: AgentRPC, external: MutableMap<String, AbstractMicroledger>?) :
    AbstractBatchedAPI() {
    var api: AgentRPC
    var names: List<String> = ArrayList<String>()
    var external: MutableMap<String, AbstractMicroledger>? = null
    override  fun openByLedgerNames( ledgerNames: List<String>?): List<AbstractMicroledger> {
        try {
            api.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers-batched/1.0/open",
                RemoteParams.RemoteParamsBuilder.create().add("names", ledgerNames).build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        names = ledgerNames ?: listOf()
        return states
    }



    override fun close() {
        try {
            api.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers-batched/1.0/close")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val states: List<AbstractMicroledger>
        get() {
            val states: JSONObject = JSONObject(object :
                RemoteCallWrapper<String?>(api) {}.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers-batched/1.0/states"))
            return returnLadgers(states)
        }

    override fun append(transactions: List<Transaction?>?, txnTime: String?): List<AbstractMicroledger> {
        val states: JSONObject = JSONObject(
            object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers-batched/1.0/append_txns",
                RemoteParams.RemoteParamsBuilder.create().add("txns", transactions).add("txn_time", txnTime)
            )
        )
        return returnLadgers(states)
    }

    override fun commit(): List<AbstractMicroledger> {
        val states: JSONObject = JSONObject(object :
            RemoteCallWrapper<String?>(api) {}.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers-batched/1.0/commit_txns"))
        return returnLadgers(states)
    }

    override fun resetUncommitted(): List<AbstractMicroledger> {
        val states: JSONObject = JSONObject(object :
            RemoteCallWrapper<String?>(api) {}.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers-batched/1.0/reset_uncommitted"))
        return returnLadgers(states)
    }

    private fun returnLadgers(states: JSONObject): List<AbstractMicroledger> {
        val resp: MutableList<AbstractMicroledger> = ArrayList<AbstractMicroledger>()
        for (name in names) {
            val state: JSONObject? = states.optJSONObject(name)
            val ledger = Microledger(name, api, state)
            if (external != null) {
                if (external!!.containsKey(name)) {
                    ledger.assignTo(external!![name])
                } else {
                    external!![name] = ledger
                }
            }
            resp.add(ledger)
        }
        return resp
    }

    init {
        this.api = api
        this.external = external
    }
}
