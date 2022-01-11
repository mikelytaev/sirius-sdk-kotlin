package com.sirius.library.agent.microledgers

import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.StringUtils
import kotlinx.serialization.json.JsonObject

object Utils {
    fun serializeOrdering(value: JSONObject): ByteArray {
        return StringUtils.stringToBytes(value.toString(), StringUtils.CODEC.UTF_8)
    }
}