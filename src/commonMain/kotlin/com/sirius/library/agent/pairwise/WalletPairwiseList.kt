package com.sirius.library.agent.pairwise

import com.sirius.library.agent.wallet.abstract_wallet.AbstractDID
import com.sirius.library.agent.wallet.abstract_wallet.AbstractPairwise
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

class WalletPairwiseList(apiPairwise: AbstractPairwise, apiDid: AbstractDID) : AbstractPairwiseList() {
    var apiPairwise: AbstractPairwise
    var apiDid: AbstractDID
    override fun create(pairwise: Pairwise) {
        apiDid.storeTheirDid(pairwise.their.did, pairwise.their.verkey)
        apiPairwise.createPairwise(
            pairwise.their.did,
            pairwise.me.did,
            pairwise.getMetadatai(),
            buildTags(pairwise)
        )
    }

    override fun update(pairwise: Pairwise) {
        apiPairwise.setPairwiseMetadata(pairwise.their.did, pairwise.getMetadatai(), buildTags(pairwise))
    }

    override fun isExists(theirDid: String): Boolean {
        return apiPairwise.isPairwiseExist(theirDid)
    }

    override fun ensureExists(pairwise: Pairwise) {
        if (isExists(pairwise.their.did?: "")) {
            update(pairwise)
        } else {
            create(pairwise)
        }
    }

    override fun loadForDid(theirDid: String): Pairwise? {
        return if (isExists(theirDid)) {
            val raw: String? = apiPairwise.getPairwise(theirDid)
            val metadataObj: JSONObject = JSONObject(raw)
            val metadata: JSONObject = JSONObject(metadataObj.get("metadata").toString())
            restorePairwise(metadata)
        } else {
            null
        }
    }

    override fun loadForVerkey(theirVerkey: String): Pairwise? {
        val tagsObj: JSONObject = JSONObject()
        tagsObj.put("their_verkey", theirVerkey)
        val (first) = apiPairwise.search(tagsObj, 1)
        if (first != null) {
            if (first!!.size > 0) {
                val raw = first!![0]
                val metadataObj: JSONObject = JSONObject(raw)
                val metadata: JSONObject = JSONObject(metadataObj.get("metadata").toString())
                return restorePairwise(metadata)
            }
        }
        return null
    }

    companion object {
        fun buildTags(pairwise: Pairwise): JSONObject {
            val jsonObject: JSONObject = JSONObject()
            jsonObject.put("my_did", pairwise.me.did)
            jsonObject.put("my_verkey", pairwise.me.verkey)
            jsonObject.put("their_verkey", pairwise.their.verkey)
            return jsonObject
        }

        fun restorePairwise(metadata: JSONObject): Pairwise {
            // JSONObject metaObject = new JSONObject(metadata);
            val meObj: JSONObject? = metadata.optJSONObject("me")
            var meDid: String? = null
            var meVerKey: String? = null
            if (meObj != null) {
                meDid = meObj.getString("did")
                meVerKey = meObj.getString("verkey")
            }
            val me = Pairwise.Me(meDid, meVerKey)
            if (meObj != null) {
                me.setDidDoci(meObj.optJSONObject("did_doc"))
            }
            var theirObj: JSONObject? = metadata.optJSONObject("their")
            if (theirObj == null && meObj != null) {
                theirObj = meObj.optJSONObject("their")
            }
            var theirDid: String? = null
            var theirVerKey: String? = null
            var theirLabel: String? = null
            var theirEndpoint: String? = null
            val theirRoutingKeys: MutableList<String>? = null
            if (theirObj != null) {
                theirDid = theirObj.getString("did")
                theirVerKey = theirObj.getString("verkey")
                theirLabel = theirObj.getString("label")
                val endpointObj: JSONObject? = theirObj.optJSONObject("endpoint")
                if (endpointObj != null) {
                    theirEndpoint = endpointObj.getString("address")
                    val routingArray: JSONArray? = endpointObj.getJSONArray("routing_keys")
                    if (routingArray != null) {
                        for (i in 0 until routingArray.length()) {
                            val key: String? = routingArray.getString(i)
                            key?.let {
                                theirRoutingKeys!!.add(key)
                            }
                        }
                    }
                }
            }
            val their = Pairwise.Their(theirDid, theirLabel, theirEndpoint, theirVerKey, theirRoutingKeys)
            if (theirObj != null) {
                their.setDidDoci(theirObj.optJSONObject("did_doc"))
            }
            return Pairwise(me, their, metadata)
        }
    }

    init {
        this.apiPairwise = apiPairwise
        this.apiDid = apiDid
    }
}
