package com.sirius.library.agent.microledgers

import com.sirius.library.utils.JSONObject

class Transaction(obj: JSONObject) : JSONObject(obj.toString()) {
    fun hasMetadata(): Boolean {
        return has(METADATA_ATTR) && !optJSONObject(METADATA_ATTR)!!.isEmpty()
    }

    val time: String?
        get() {
            return optJSONObject(METADATA_ATTR)?.optString(ATTR_TIME, null)
        }

    companion object {
        const val METADATA_ATTR = "txnMetadata"
        const val ATTR_TIME = "txnTime"
    }

    init {
        if (!has(METADATA_ATTR)) put(METADATA_ATTR, JSONObject())
    }
}
