package com.sirius.library.utils

import org.json.JSONObject

actual object JSONEscape {

    actual fun unescapeJsonObject(unescaped: String): String {
        val json = JSONObject(unescaped)
        return json.toString()
    }
}