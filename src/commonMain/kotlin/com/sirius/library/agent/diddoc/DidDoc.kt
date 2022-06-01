package com.sirius.sdk.agent.aries_rfc

import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.hub.Context
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

open class DidDoc {
    protected var payload: JSONObject

    constructor(payload: JSONObject) {
        this.payload = payload
    }

    protected constructor() {
        payload = JSONObject()
    }

    val did: String
        get() = payload.optString("id", "")!!

    fun getPayload(): JSONObject {
        return payload
    }

    fun extractService(highPriority: Boolean, type: String?): JSONObject? {
        val services: JSONArray? = payload.optJSONArray("service")
        if (services != null) {
            var ret: JSONObject? = null
            for (serviceObj in services) {
                val service: JSONObject = serviceObj as JSONObject
                if (!service.optString("type").equals(type)) continue
                if (ret == null) {
                    ret = service
                } else {
                    if (highPriority) {
                        if (service.optInt("priority", 0)!! > ret.optInt("priority", 0)!!) {
                            ret = service
                        }
                    }
                }
            }
            return ret
        }
        return null
    }

    fun extractService(): JSONObject? {
        return extractService(true, "IndyAgent")
    }

    fun addService(type: String?, endpoint: Endpoint): JSONObject {
        val service = JSONObject()
        var services: JSONArray? = payload.optJSONArray("service")
        if (services == null) {
            services = JSONArray()
            payload.put("service", services)
        }
        service.put("id", did + "#" + services.length())
        service.put("type", type)
        service.put("serviceEndpoint", endpoint.address)
        if (endpoint.routingKeys.isNotEmpty()) {
            service.put("routingKeys", endpoint.routingKeys)
        }
        services.put(service)
        return service
    }

    fun addAgentServices(context: Context<*>) {
        for (e in context.endpoints!!) {
            addService("DIDCommMessaging", e)
        }
    }

    companion object {
        const val DID = "did"
    }
}