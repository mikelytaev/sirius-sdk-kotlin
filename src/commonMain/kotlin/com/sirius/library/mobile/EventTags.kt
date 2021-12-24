package com.sirius.library.mobile


import com.sirius.library.agent.ledger.SchemaFilters
import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * All tags should be Strings
 */
@Serializable
open class EventTags() : JsonSerializable<EventTags> {

    var id: String? = null
    var isAccepted: Boolean = false
    var pairwiseDid: String? = null

    override fun serialize(): String {
        return Json.encodeToString(this)
    }

    override fun deserialize(string: String): EventTags {
        return Json.decodeFromString<EventTags>(string)
    }

    override fun serializeToJSONObject(): JSONObject {
        return JSONObject(serialize())
    }


}