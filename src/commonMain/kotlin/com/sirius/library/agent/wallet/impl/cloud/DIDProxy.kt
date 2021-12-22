package com.sirius.library.agent.wallet.impl.cloud

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.agent.wallet.abstract_wallet.AbstractDID
import com.sirius.library.utils.JSONArray

class DIDProxy(rpc: AgentRPC) : AbstractDID() {
    var rpc: AgentRPC
    override  fun createAndStoreMyDid(did: String?, seed: String?, cid: Boolean?): Pair<String, String> {
        return object : RemoteCallWrapper<Pair<String, String>?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/create_and_store_my_did",
            RemoteParams.RemoteParamsBuilder.create()
                .add("seed", seed)
                .add("did", did)
                .add("cid", cid)
        ) ?:  Pair<String, String>("","")
    }

    override fun storeTheirDid(did: String?, verkey: String?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/store_their_did",
            RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .add("verkey", verkey)
        )
    }

    override fun setDidMetadata(did: String?, metadata: String?) {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .add("metadata", metadata)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/set_did_metadata", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
    }

    override  fun listMyDidsWithMeta(): List<Any>? {
        try {
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/list_my_dids_with_meta")
            if (response is JSONArray) {
                val objectList: MutableList<Any> = ArrayList<Any>()
                for (i in 0 until (response as JSONArray).length()) {
                    val `object`: Any? = (response as JSONArray).get(i)
                    `object`?.let {
                        objectList.add(`object`)
                    }
                }
                return objectList
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun getDidMetadata(did: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_did_metadata", params)
            if (response != null) {
                return response.toString()
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun keyForLocalDid(did: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/key_for_local_did", params)
            if (response != null) {
                return response.toString()
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun keyForDid(poolName: String?, did: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .add("pool_name", poolName)
                .build()
            val response: Any? = rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/key_for_did", params)
            if (response != null) {
                return response.toString()
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun createKey(seed: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("seed", seed)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/create_key__did", params)
            if (response != null) {
                return response.toString()
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun replaceKeysStart(did: String?, seed: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("seed", seed)
                .add("did", did)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/replace_keys_start", params)
            if (response != null) {
                return response.toString()
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun replaceKeysApply(did: String?) {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/replace_keys_apply", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
    }

    override fun setKeyMetadata(verkey: String?, metadata: String?) {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("verkey", verkey)
                .add("metadata", metadata)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/set_key_metadata__did", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
    }

    override fun getKeyMetadata(verkey: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("verkey", verkey)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_key_metadata__did", params)
            if (response != null) {
                return response.toString()
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun setEndpointForDid(did: String?, address: String?, transportKey: String?) {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .add("address", address)
                .add("transport_key", transportKey)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/set_endpoint_for_did", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
    }

    override fun getEndpointForDid(poolName: String?, did: String?): Pair<String, String>? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .add("pool_name", poolName)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_endpoint_for_did", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun getMyDidMeta(did: String?): Any? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_my_did_with_meta", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun abbreviateVerKey(did: String?, fullVerkey: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .build()
            val response: Any? =
                rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/abbreviate_verkey", params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    override fun qualifyDid(did: String?, method: String?): String? {
        try {
            val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create()
                .add("did", did)
                .add("method", method)
                .build()
            val response: Any? = rpc.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/qualify_did", params)
            if (response != null) {
                return response.toString()
            }
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    init {
        this.rpc = rpc
    }
}
