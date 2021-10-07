package com.sirius.library.agent.ledger

import com.sirius.library.base.JsonSerializable
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class CredentialDefinition : JsonSerializable<CredentialDefinition> {
    constructor() {}


    fun getBodyi(): JSONObject? {
        return body
    }




    var tag: String? = null
    var schema: Schema? = null
    var config: Config? = null
    var body: JSONObject? = null

   // @SerializedName("seq_no")
    var seqNo: Int? = null

    constructor(tag: String?, schema: Schema?, config: Config?, body: JSONObject?, seqNo: Int?) {
        this.tag = tag
        this.schema = schema
        this.config = config
        this.body = body
        this.seqNo = seqNo
    }

    constructor(tag: String?, schema: Schema?) : this(tag, schema, Config(), null) {}
    constructor(tag: String?, schema: Schema?, seqNo: Int?) : this(tag, schema, Config(), seqNo) {}
    constructor(tag: String?, schema: Schema?, config: Config?, seqNo: Int?) : this(tag, schema, config, null, seqNo) {}

    @Serializable
    class Config : JsonSerializable<Config> {
        @SerialName("support_revocation")
        var supportRevocation = false
        override fun serialize(): String {
            return "GsonUtils.getDefaultGson().toJson(this, Config::class.java)"
        }

        override fun serializeToJSONObject(): JSONObject {
            val jsonObject: JSONObject = JSONObject()
            jsonObject.put("support_revocation", supportRevocation)
            return jsonObject
        }




        override fun deserialize(string: String): Config {
            return Config()
        }
    }

    override fun serialize(): String {
        return "Gson().toJson(this, CredentialDefinition::class.java)"
    }

    override fun serializeToJSONObject(): JSONObject {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("support_revocation", true)
        return jsonObject
    }



    val id: String?
        get() {
            if (body != null) {
                if (body!!.has("id")) {
                    return body!!.getString("id")
                }
            }
            return null
        }
    val submitterDid: String?
        get() {
            val id = id
            return id?.split(":")?.toTypedArray()?.get(0)
        }

    override fun deserialize(string: String): CredentialDefinition {
        return CredentialDefinition()
    }
}

