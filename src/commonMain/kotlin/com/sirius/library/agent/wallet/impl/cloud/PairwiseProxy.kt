package com.sirius.library.agent.wallet.impl.cloud

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.agent.wallet.abstract_wallet.AbstractPairwise
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

class PairwiseProxy(rpc: AgentRPC) : AbstractPairwise() {
    var rpc: AgentRPC
    override fun isPairwiseExist(theirDid: String?): Boolean {
        return object : RemoteCallWrapper<Boolean?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/is_pairwise_exists",
            RemoteParams.RemoteParamsBuilder.create()
                .add("their_did", theirDid)
        ) ?: false
    }

    override fun createPairwise(theirDid: String?, myDid: String?, metadata: JSONObject?, tags: JSONObject?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/create_pairwise",
            RemoteParams.RemoteParamsBuilder.create()
                .add("their_did", theirDid)
                .add("my_did", myDid)
                .add("metadata", metadata)
                .add("tags", tags)
        )
    }

    override fun listPairwise(): List<Any> {
        return object :
            RemoteCallWrapper<List<Any>?>(rpc) {}.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/list_pairwise") ?: listOf()
    }

    override fun getPairwise(thierDid: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_pairwise",
            RemoteParams.RemoteParamsBuilder.create()
                .add("their_did", thierDid)
        )
    }

    override fun setPairwiseMetadata(theirDid: String?, metadata: JSONObject?, tags: JSONObject?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/set_pairwise_metadata",
            RemoteParams.RemoteParamsBuilder.create()
                .add("their_did", theirDid)
                .add("metadata", metadata)
                .add("tags", tags)
        )
    }

    override fun search(tags: JSONObject?, limit: Int?): Pair<List<String>, Int> {
        return object : RemoteCallWrapper<Pair<List<String>, Int>?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/search_pairwise",
            RemoteParams.RemoteParamsBuilder.create()
                .add("tags", tags)
                .add("limit", limit)
        ) ?: Pair<List<String>, Int>(listOf(), 0)
    }

    init {
        this.rpc = rpc
    }
}

