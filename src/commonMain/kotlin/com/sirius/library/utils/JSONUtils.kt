package com.sirius.library.utils

import kotlin.jvm.JvmOverloads

object JSONUtils {
    @JvmOverloads
    fun JSONObjectToString(obj: JSONObject?, sortKeys: Boolean = false): String {
       /* val keys: List<String> = ArrayList(obj.keySet())
        if (sortKeys) java.android.util.Collections.sort<String>(keys)
        val stringBuilder: StringBuilder = StringBuilder()
        stringBuilder.append("{")
        for (key in keys) {
            stringBuilder.append("\"").append(key).append("\"").append(":")
            val `val`: Any = obj.get(key)
            stringBuilder.append(JSONFieldToString(`val`)).append(',')
        }
        if (stringBuilder.get(stringBuilder.length - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        }
        stringBuilder.append("}")*/
        return "stringBuilder.toString()"
    }

    private fun JSONArrayToString(arr: JSONArray): String {
   /*     val stringBuilder: StringBuilder = StringBuilder()
        stringBuilder.append("[")
        for (o in arr) {
            stringBuilder.append(JSONFieldToString(o)).append(',')
        }
        if (stringBuilder.get(stringBuilder.length - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        }
        stringBuilder.append("]")*/
        return "stringBuilder.toString()"
    }

    private fun JSONFieldToString(o: Any?): String {
        if (o == null || o === JSONObject.NULL) {
            return "null"
        }
        if (!(o is JSONObject || o is JSONArray)) {
            val needQuotes = !(o is Number || o is Boolean)
            return if (needQuotes) {
              ""
            } else {
                o.toString()
            }
        }
        if (o is JSONObject) {
            return JSONObjectToString(o as JSONObject)
        }
        return if (o is JSONArray) {
            JSONArrayToString(o as JSONArray)
        } else ""
    }
}
