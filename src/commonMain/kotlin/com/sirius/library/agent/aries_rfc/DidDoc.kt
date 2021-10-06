package com.sirius.library.agent.aries_rfc

import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmOverloads

class DidDoc(payload: JSONObject) {
    var payload: JSONObject
    fun getPayload(): JSONObject {
        return payload
    }

    @JvmOverloads
    fun extractService(highPriority: Boolean = true, type: String = "IndyAgent"): JSONObject? {
        val services: JSONArray? = payload.optJSONArray("service")
        if (services != null) {
            var ret: JSONObject? = null
            for (serviceObj in services) {
                val service: JSONObject = serviceObj as JSONObject
                if (service.optString("type") != type) continue
                if (ret == null) {
                    ret = service
                } else {
                    if (highPriority) {
                        if (service.optInt("priority", 0) ?: 0 > ret.optInt("priority", 0) ?: 0) {
                            ret = service
                        }
                    }
                }
            }
            return ret
        }
        return null
    }

    companion object {
        const val DID = "did"
        const val DID_DOC = "did_doc"
        const val VCX_DID = "DID"
        const val VCX_DID_DOC = "DIDDoc"
    }

    init {
        this.payload = payload
    }
}
