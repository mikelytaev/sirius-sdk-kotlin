package com.sirius.library.agent.ledger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class CredentialDefinitionFilters {
    var tags: Tags
    var extras: JsonObject
    val tagsObject: JsonObject
        get() {
            val tagobj: JsonObject = GsonUtils.getDefaultGson().toJsonTree(tags, Tags::class.java).getAsJsonObject()
            val entrySet: Set<Map.Entry<String?, JsonElement?>> = extras.entrySet()
            for ((key, value): Map.Entry<String?, JsonElement?> in entrySet) {
                tagobj.add(key, value)
            }
            return tagobj
        }

    fun setExtras(extras: JsonObject) {
        this.extras = extras
    }

    fun getExtras(): JsonObject {
        return extras
    }

    fun addExtra(name: String?, value: String?) {
        extras.addProperty(name, value)
    }

    var tag: String?
        get() = tags.tag
        set(tag) {
            tags.tag = tag
        }

    fun setId(id: String?) {
        tags.id = id
    }

    var submitterDid: String?
        get() = tags.submitterDid
        set(submitterDid) {
            tags.submitterDid = submitterDid
        }
    var schemaId: String?
        get() = tags.schemaId
        set(schemaId) {
            tags.schemaId = schemaId
        }
    var seqNo: Int
        get() = tags.seqNo
        set(seqNo) {
            tags.seqNo = seqNo
        }

    @Serializable
    class Tags(var category: String) {
        var id: String? = null
        var tag: String? = null

        @SerialName("submitter_did")
        var submitterDid: String? = null

        @SerialName("schema_id")
        var schemaId: String? = null

        @SerialName("seq_no")
        var seqNo = 0

    }

    init {
        tags = Tags("cred_def")
        extras = JsonObject()
    }
}
