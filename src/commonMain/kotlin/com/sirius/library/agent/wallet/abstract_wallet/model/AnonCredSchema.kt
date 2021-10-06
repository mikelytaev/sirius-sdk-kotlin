package com.sirius.library.agent.wallet.abstract_wallet.model

import com.sirius.library.agent.ledger.Schema
import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

open class AnonCredSchema : JsonSerializable<AnonCredSchema> {
    var ver: String? = null
    var id: String? = null
    var name: String? = null
    var version: String? = null
    var attrNames: List<String>? = null

    constructor() {}
    constructor(json: String) {
        val anonCreds = deserialize(json)
        ver = anonCreds.ver
        id = anonCreds.id
        name = anonCreds.name
        version = anonCreds.version
        attrNames = anonCreds.attrNames
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null) return false
        if(o is AnonCredSchema){
            val that = o as AnonCredSchema
            val thisAttrNamesSet: Set<String> = HashSet<String>(attrNames?: listOf())
            val thatAttrNamesSet: Set<String> = HashSet<String>(that.attrNames?: listOf())
            return id == that.id && name == that.name && version == that.version && thisAttrNamesSet == thatAttrNamesSet
        }
        return false
    }



    val body: JSONObject
        get() = serializeToJSONObject()

    override fun serialize(): String? {
        return Json.encodeToString(this)
    }

    override fun deserialize(string: String): AnonCredSchema {
        return Json.decodeFromString<AnonCredSchema>(string)
    }

    override fun serializeToJSONObject(): JSONObject {
        val json = JSONObject(serialize())
        return json
    }

}

