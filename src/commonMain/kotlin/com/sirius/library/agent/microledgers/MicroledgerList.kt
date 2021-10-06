package com.sirius.library.agent.microledgers

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.errors.sirius_exceptions.SiriusContextError
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.JSONUtils

class MicroledgerList(api: AgentRPC) : AbstractMicroledgerList() {
    var api: AgentRPC
    var instances: MutableMap<String, AbstractMicroledger> = HashMap<String, AbstractMicroledger>()
    var batchedAPI: BatchedAPI

    override fun create(name: String?, genesis: List<Transaction?>?): Pair<AbstractMicroledger?, List<Transaction?>?>? {
        val instance = Microledger(name?:"", api)
        val txns = instance.init(genesis)
        instances[name?:""] = instance
        return Pair(instance, txns)
    }

    override fun getLedger(name: String?): AbstractMicroledger? {
        if (!instances.containsKey(name)) {
            checkIsExists(name?:"")
            val instance = Microledger(name?:"", api)
            instances[name?:""] = instance
        }
        return instances[name]
    }

    override fun reset(name: String?) {
        checkIsExists(name?:"")
        object : RemoteCallWrapper<Unit>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/reset",
            RemoteParams.RemoteParamsBuilder.create().add("name", name)
        )
        if (instances.containsKey(name)) instances.remove(name)
    }




    override fun isExists(name: String?): Boolean {
        return object : RemoteCallWrapper<Boolean?>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/is_exists",
            RemoteParams.RemoteParamsBuilder.create().add("name", name)
        ) ?: false
    }

    override fun leafHash(txn: Transaction?): ByteArray? {
        val data: ByteArray = JSONUtils.JSONObjectToString(txn, true).encodeToByteArray()
        return object : RemoteCallWrapper<ByteArray?>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/leaf_hash",
            RemoteParams.RemoteParamsBuilder.create().add("data", data)
        )
    }

    override val list: List<LedgerMeta>
        get() {
            val collection: List<String> = object : RemoteCallWrapper<List<String>?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/list",
                RemoteParams.RemoteParamsBuilder.create().add("name", "*")
            ).orEmpty()
            val res: MutableList<LedgerMeta> = ArrayList<LedgerMeta>()
            for (s in collection) {
                res.add(LedgerMeta(JSONObject(s)))
            }
            return res
        }
    override val batched: AbstractBatchedAPI
        get() = batchedAPI

    private fun checkIsExists(name: String) {
        if (!instances.containsKey(name)) {
            val isExists = isExists(name)
            if (!isExists) throw SiriusContextError("MicroLedger with name $name does not exists")
        }
    }

    init {
        this.api = api
        batchedAPI = BatchedAPI(api, instances)
    }
}
