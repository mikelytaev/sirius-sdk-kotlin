package com.sirius.library.agent.wallet.impl

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.agent.wallet.abstract_wallet.AbstractNonSecrets
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.utils.JSONObject

class NonSecretsProxy(rpc: AgentRPC) : AbstractNonSecrets() {
    var rpc: AgentRPC

    override fun addWalletRecord(type: String?, id: String?, value: String?, tags: String?) {
        var tagObject: JSONObject? = null
        if (tags != null) {
            tagObject = JSONObject(tags)
        }
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/add_wallet_record",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("id_", id).add("value", value).add("tags", tagObject)
        )
    }

    override fun updateWalletRecordValue(type: String?, id: String?, value: String?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/update_wallet_record_value",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("id_", id).add("value", value)
        )
    }

    override fun updateWalletRecordTags(type: String?, id: String?, tags: String?) {
        var tagObject: JSONObject? = null
        if (tags != null) {
            tagObject = JSONObject(tags)
        }
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/update_wallet_record_tags",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("id_", id).add("tags", tagObject)
        )
    }

    override fun addWalletRecordTags(type: String?, id: String?, tags: String?) {
        var tagObject: JSONObject? = null
        if (tags != null) {
            tagObject = JSONObject(tags)
        }
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/add_wallet_record_tags",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("id_", id).add("tags", tagObject)
        )
    }

    override fun deleteWalletRecord(type: String?, id: String?, tagNames: List<String?>?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/delete_wallet_record_tags",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("id_", id).add("tag_names", tagNames)
        )
    }

    override fun deleteWalletRecord(type: String?, id: String?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/delete_wallet_record",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("id_", id)
        )
    }

    override fun getWalletRecord(type: String?, id: String?, options: RetrieveRecordOptions?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_wallet_record",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("id_", id).add("options", options)
        )
    }

    override fun walletSearch(
        type: String?,
        query: String?,
        options: RetrieveRecordOptions?,
        limit: Int
    ): Pair<List<String>, Int> {
        var queryObject: JSONObject? = null
        if (query != null) {
            queryObject = JSONObject(query)
        }
        return object : RemoteCallWrapper<Pair<List<String>, Int>?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/wallet_search",
            RemoteParams.RemoteParamsBuilder.create()
                .add("type_", type).add("query", queryObject).add("options", options).add("limit", limit)
        ) ?: Pair<List<String>, Int>(listOf(), 0)
    }

    init {
        this.rpc = rpc
    }
}
