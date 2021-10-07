package com.examples.covid

import com.sirius.library.agent.model.Entity
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.CloudHub
import com.sirius.library.hub.Context
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

object Helpers {
    fun establishConnection(
        myConf: CloudHub.Config,
        myEntity: Entity,
        theirConf: CloudHub.Config,
        theirEntity: Entity
    ): Pairwise {
        val me: Context = CloudContext(myConf)
        val their: Context = CloudContext(theirConf)
        run {
            var pairwise: Pairwise? = me.getPairwiseListi().loadForDid(theirEntity.did)
            val isFilled = pairwise != null && pairwise.getMetadatai() != null
            if (!isFilled) {
                val me_ = Pairwise.Me(myEntity.did, myEntity.verkey)
                val their_ = Pairwise.Their(
                    theirEntity.did, theirEntity.label,
                    their.endpointAddressWithEmptyRoutingKeys, theirEntity.verkey, listOf()
                )
                val metadata: JSONObject = JSONObject().put(
                    "me",
                    JSONObject().put("did", myEntity.did).put("verkey", myEntity.verkey)
                ).put(
                    "their",
                    JSONObject().put("did", theirEntity.did).put("verkey", theirEntity.verkey)
                        .put("label", theirEntity.label).put(
                            "endpoint",
                            JSONObject().put("address", their.endpointAddressWithEmptyRoutingKeys)
                                .put("routing_keys", JSONArray())
                        )
                )
                pairwise = Pairwise(me_, their_, metadata)
                me.getDidi().storeTheirDid(theirEntity.did, theirEntity.verkey)
                me.getPairwiseListi().ensureExists(pairwise)
            }
        }
        run {
            var pairwise: Pairwise? = their.getPairwiseListi().loadForDid(theirEntity.did)
            val isFilled = pairwise != null && pairwise?.getMetadatai() != null
            if (!isFilled) {
                val me_ = Pairwise.Me(theirEntity.did, theirEntity.verkey)
                val their_ = Pairwise.Their(
                    myEntity.did, myEntity.label,
                    me.endpointAddressWithEmptyRoutingKeys, myEntity.verkey, listOf()
                )
                val metadata: JSONObject = JSONObject().put(
                    "me",
                    JSONObject().put("did", theirEntity.did).put("verkey", theirEntity.verkey)
                ).put(
                    "their",
                    JSONObject().put("did", myEntity.did).put("verkey", myEntity.verkey)
                        .put("label", myEntity.label).put(
                            "endpoint",
                            JSONObject().put("address", me.endpointAddressWithEmptyRoutingKeys)
                                .put("routing_keys", JSONArray())
                        )
                )
                pairwise = Pairwise(me_, their_, metadata)
                their.getDidi().storeTheirDid(myEntity.did, myEntity.verkey)
                their.getPairwiseListi().ensureExists(pairwise!!)
            }
        }
        val res: Pairwise? = me.getPairwiseListi().loadForDid(theirEntity.did)
        me.close()
        their.close()
        return res!!
    }
}
