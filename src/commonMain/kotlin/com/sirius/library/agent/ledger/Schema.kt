package com.sirius.library.agent.ledger

import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class Schema : AnonCredSchema {
    var seqNo = 0
    val issuerDid: String?
        get() = if (id != null) {
            id!!.split(":").get(0)
        } else null

    constructor() : super() {}
    constructor(json: String) : super(json) {
        val schema = deserialize(json)
        seqNo = schema.seqNo
    }

    override fun equals(o: Any?): Boolean {
        val isEqueals = super.equals(o)
        val schema = o as Schema?
        return seqNo == schema!!.seqNo && isEqueals
    }

    override fun serialize(): String {
        return Json.encodeToString(this)
    }

    override fun deserialize(string: String): Schema {
        return Json.decodeFromString<Schema>(string)
    }

    override fun serializeToJSONObject(): JSONObject {
        val json = JSONObject(serialize())
        json.put("seqNo", seqNo)
        return json
    }


}


