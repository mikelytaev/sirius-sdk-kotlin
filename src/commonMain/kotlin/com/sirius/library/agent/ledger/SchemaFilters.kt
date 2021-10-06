package com.sirius.library.agent.ledger

import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SchemaFilters {
    var tags: Tags
    var id: String?
        get() = tags.id
        set(id) {
            tags.id = id
        }
    var name: String?
        get() = tags.name
        set(name) {
            tags.name = name
        }
    var version: String?
        get() = tags.version
        set(version) {
            tags.version = version
        }
    var submitterDid: String?
        get() = tags.submitterDid
        set(submitterDid) {
            tags.submitterDid = submitterDid
        }

    @Serializable
     class Tags(var category: String) : JsonSerializable<Tags?> {
        var id: String? = null
        var name: String? = null
        var version: String? = null
         @SerialName("submitter_did")
        var submitterDid: String? = null

        override fun serialize(): String {
            return Json.encodeToString(this)
        }

        override fun deserialize(string: String): Tags {
            return Json.decodeFromString<Tags>(string)
        }

        override fun serializeToJSONObject(): JSONObject {
            return JSONObject(serialize())
        }


    }

    init {
        tags = Tags("schema")
    }
}
