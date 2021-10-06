package com.sirius.library.agent.wallet.abstract_wallet.model

import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RetrieveRecordOptions : JsonSerializable<RetrieveRecordOptions?> {
    var retrieveType = false
    var retrieveValue = false
    var retrieveTags = false
    var retrieveTotalCount: Boolean? = null
    var retrieveRecords: Boolean? = null
    fun setRetrieveTotalCount(retrieveTotalCount: Boolean) {
        this.retrieveTotalCount = retrieveTotalCount
    }

    fun setRetrieveRecords(retrieveRecords: Boolean) {
        this.retrieveRecords = retrieveRecords
    }

    constructor() {}
    constructor(retrieveType: Boolean, retrieveValue: Boolean, retrieveTags: Boolean) {
        this.retrieveType = retrieveType
        this.retrieveValue = retrieveValue
        this.retrieveTags = retrieveTags
    }

    override fun serialize(): String? {
        return Json.encodeToString(this)
    }

    override fun deserialize(string: String): RetrieveRecordOptions {
        return Json.decodeFromString<RetrieveRecordOptions>(string)
    }

    override fun serializeToJSONObject(): JSONObject {
        val json = JSONObject(serialize())
        return json
    }


    fun checkAll() {
        retrieveType = true
        retrieveValue = true
        retrieveTags = true
    }
}

