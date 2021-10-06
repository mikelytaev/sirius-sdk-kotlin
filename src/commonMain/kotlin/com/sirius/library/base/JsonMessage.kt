package com.sirius.library.base

import com.sirius.library.utils.JSONObject

class JsonMessage {
    var messageObj: JSONObject = JSONObject()

    constructor(message: String) {
        messageObj = JSONObject(message)
    }

    constructor() {}

    fun prettyPrint(): String {
        /*val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson: Gson = builder.create()
        return gson.toJson(this, this.javaClass)*/
        return ""
    }

    fun getStringFromJSON(key: String): String {
        if (messageObjectHasKey(key)) {
            val value: String? = messageObj.getString(key)
            return if (value == null || value.isEmpty()) {
                ""
            } else value
        }
        return ""
    }

    fun messageObjectHasKey(key: String): Boolean {
        return messageObj.has(key)
    }

    fun getJSONOBJECTFromJSON(key: String): JSONObject? {
        return if (messageObjectHasKey(key)) {
            messageObj.getJSONObject(key)
        } else null
    }
}
