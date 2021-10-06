package com.sirius.library.agent.microledgers

import kotlinx.serialization.json.JsonObject

object Utils {
    fun serializeOrdering(value: JsonObject): ByteArray {
        return value.toString().toByteArray(java.nio.charset.StandardCharsets.UTF_8)
    }
}