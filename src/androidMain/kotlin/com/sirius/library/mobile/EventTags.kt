package com.sirius.library.mobile

import com.google.gson.Gson
import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONObject

/**
 * All tags should be Strings
 */
open class EventTags() : JsonSerializable<EventTags> {

    var id: String? = null
    var isAccepted: Boolean = false
    var pairwiseDid: String? = null

    override fun serialize(): String {
        return Gson().toJson(this, EventTags::class.java)
    }

    override fun serializeToJSONObject(): JSONObject {
        return JSONObject(serialize())
    }


    override fun deserialize(string: String): EventTags {
        return Gson().fromJson(string, EventTags::class.java);
    }
}