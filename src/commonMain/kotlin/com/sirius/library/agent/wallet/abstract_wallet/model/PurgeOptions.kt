package com.sirius.library.agent.wallet.abstract_wallet.model

import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class PurgeOptions : JsonSerializable<PurgeOptions> {
    var maxAge = -1

    constructor(maxAge: Int) {
        this.maxAge = maxAge
    }

    constructor() {}

    override fun serialize(): String? {
        return Json.encodeToString(this)
    }

    override fun deserialize(string: String): PurgeOptions {
        return Json.decodeFromString<PurgeOptions>(string)
    }

    override fun serializeToJSONObject(): JSONObject {
        val json = JSONObject(serialize())
        return json
    }

}

