package com.sirius.library.agent.wallet.abstract_wallet.model

import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class CacheOptions : JsonSerializable<CacheOptions> {
    var noCache = false
    var noUpdate = false
    var noStore = false
    var minFresh = -1

    /**
     * @param noCache  (bool, optional, false by default) Skip usage of cache,
     * @param noUpdate (bool, optional, false by default) Use only cached data, do not try to update.
     * @param noStore  (bool, optional, false by default) Skip storing fresh data if updated,
     * @param minFresh int, optional, -1 by default) Return cached data if not older than this many seconds. -1 means do not check age.
     */
    constructor(noCache: Boolean, noUpdate: Boolean, noStore: Boolean, minFresh: Int) {
        this.noCache = noCache
        this.noUpdate = noUpdate
        this.noStore = noStore
        this.minFresh = minFresh
    }

    constructor() {}


    override fun serialize(): String? {
        return Json.encodeToString(this)
    }

    override fun deserialize(string: String): CacheOptions {
        return Json.decodeFromString<CacheOptions>(string)
    }

    override fun serializeToJSONObject(): JSONObject {
        val json = JSONObject(serialize())
        return json
    }
}
