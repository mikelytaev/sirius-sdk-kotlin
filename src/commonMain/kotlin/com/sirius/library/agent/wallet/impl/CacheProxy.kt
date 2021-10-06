package com.sirius.library.agent.wallet.impl

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCache
import com.sirius.library.agent.wallet.abstract_wallet.model.CacheOptions
import com.sirius.library.agent.wallet.abstract_wallet.model.PurgeOptions

class CacheProxy(rpc: AgentRPC) : AbstractCache() {
    var rpc: AgentRPC
    override fun getSchema(poolName: String?, submitter_did: String?, id: String?, options: CacheOptions?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_schema",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitter_did).add("id_", id).add("options", options)
        )
    }

    override fun getCredDef(poolName: String?, submitter_did: String?, id: String?, options: CacheOptions?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_cred_def",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitter_did).add("id_", id).add("options", options)
        )
    }

    override fun purgeSchemaCache(options: PurgeOptions?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/purge_schema_cache",
            RemoteParams.RemoteParamsBuilder.create().add("options", options)
        )
    }

    override fun purgeCredDefCache(options: PurgeOptions?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/purge_cred_def_cache",
            RemoteParams.RemoteParamsBuilder.create().add("options", options)
        )
    }

    init {
        this.rpc = rpc
    }
}
