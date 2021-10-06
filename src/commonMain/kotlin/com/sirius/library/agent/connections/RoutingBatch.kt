package com.sirius.library.agent.connections

import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

class RoutingBatch(theirVk: List<String>?, endpoint: String?, myVk: String?, routingKeys: List<String>?) :
    JSONObject() {
    init {
        put("recipient_verkeys", JSONArray(theirVk))
        put("endpoint_address", endpoint)
        put("sender_verkey", myVk)
        put("routing_keys", JSONArray(routingKeys))
    }
}
