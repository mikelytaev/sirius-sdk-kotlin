package com.sirius.library.utils

import kotlinx.serialization.json.*
import kotlin.reflect.KClass

open class JSONObject {
    companion object {
        fun getNames(offer: JSONObject): List<String> {
            return listOf()

        }

        val NULL = JsonNull.content

        fun serializeToObjects(element : JsonElement?) :Any?{
            if (element == null || element == JsonNull) {
                return null
            } else if (element is JsonPrimitive) {
                if (element.booleanOrNull != null) {
                    return element.boolean
                } else if (element.isString) {
                    return element.content
                } else if(element.floatOrNull!=null){
                    return element.float
                }
                else if(element.doubleOrNull!=null){
                    return element.double
                }else if(element.longOrNull!=null){
                    return element.long
                }else if(element.intOrNull!=null){
                    return element.int
                }
            }

            else if (element is JsonObject) {
                return JSONObject(element)
            } else if (element is JsonArray) {
                return JSONArray(element)
            }
            return null
        }

        fun serializeToJsonElement(value : Any?) : JsonElement{
            if (value == null || value == JSONObject.NULL) {
                return JsonNull
            }else if (value is String) {
                return JsonPrimitive(value)
            }else if (value is Number) {
                return JsonPrimitive(value)
            }else if (value is Boolean) {
                return JsonPrimitive(value)
            } else if (value is JSONObject) {
                return buildJsonObject {
                    value.jsonObject.entries.forEach {
                        put(it.key, it.value)
                    }
                }
            } else if (value is JSONArray){
                return buildJsonArray {
                    value.jsonArray.forEach {
                        this.add(it)
                    }
                }
            }else if (value is List<Any?>) {
                return buildJsonArray {
                    value.forEach {
                        val element = serializeToJsonElement(it)
                        this.add(element)
                    }
                }
            }
            return JsonNull
        }

    }

    var message: String? = null
    var jsonMap: Map<String, JsonElement> = HashMap()
    var jsonObject: JsonObject = buildJsonObject {

    }

    constructor() {

        jsonObject = buildJsonObject { }
    }

    constructor(jsonObject: JsonObject) {
        this.jsonObject = jsonObject
    }

    constructor(jsonObject: JSONObject?) {
        //  this.jsonObject = jsonObject
    }

    fun serialize() {
        // Json.encodeToJsonElement()
    }


    //fun
    constructor(message: String?) {
        message?.let {
            val unescapedString = JSONEscape.unescapeJsonObject(message)
            this.message = unescapedString
            println("message=" + message)
            val element = Json.parseToJsonElement(message)
            println("element=" + element)
            jsonObject = Json.parseToJsonElement(unescapedString).jsonObject

        }

    }

    fun optValue(key: String): Any? {
        return null
    }

    fun get(key: String): Any? {
        if (checkNull(key)) {
            return null
        }
        val elemnt = jsonObject.get(key)
        if (elemnt is JsonObject) {
            return JSONObject(elemnt)
        } else if (elemnt is JsonArray) {
            return JSONArray(elemnt)
        } else if (elemnt is JsonPrimitive) {
            if (elemnt.booleanOrNull != null) {
                return elemnt.boolean
            } else if (elemnt.isString) {
                return elemnt.content
            }

        }
        return jsonObject.get(key)
    }

    fun optString(key: String, default: String? = null): String? {
        return jsonObject.get(key)?.jsonPrimitive?.content ?: default
    }

    fun getString(key: String): String? {
        return jsonObject.get(key)?.jsonPrimitive?.content
    }

    fun has(key: String): Boolean {
        return jsonObject.containsKey(key)
    }



    fun put(key: String, value: Any?): JSONObject {
        jsonObject = buildJsonObject {
            jsonObject.entries.forEach {
                put(it.key, it.value)
            }
           val element =  serializeToJsonElement(value)
            put(key, element)
          /*  if (value == null || value == JSONObject.NULL) {
                put(key, JsonNull)
            } else if (value is String) {
                put(key, value)
            } else if (value is JSONArray) {
                putJsonArray(key) {
                    value.jsonArray.forEach {
                        this.add(it)
                    }
                }

            } else if (value is JSONObject) {
                putJsonObject(key) {
                    value.jsonObject.entries.forEach {
                        put(it.key, it.value)
                    }
                }
            } else if (value is Number) {
                put(key, value)
            } else if (value is String) {
                put(key, value)
            } else if (value is Boolean) {
                put(key, value)
            }else if (value is List<Any?>) {

            }*/
        }

        return this
    }

    fun remove(key: String) {

    }

    fun isNull(key: String): Boolean {
        return checkNull(key)
    }

    fun getBoolean(key: String): Boolean? {
        return jsonObject.get(key)?.jsonPrimitive?.booleanOrNull
    }

    fun getJSONObject(key: String): JSONObject? {
        return optJSONObject(key)
    }

    fun getJSONArray(key: String): JSONArray? {
        return optJSONArray(key)
    }

    fun optJSONObject(key: String): JSONObject? {
        if (checkNull(key)) {
            return null
        }
        if (jsonObject.get(key)?.jsonObject == null) {
            return null
        }
        return JSONObject(jsonObject.get(key)!!.jsonObject)
    }

    fun checkNull(key: String): Boolean {
        if (jsonObject.get(key) == JsonNull) {
            return true
        }
        return false
    }

    fun optJSONArray(key: String): JSONArray? {
        if (checkNull(key)) {
            return null
        }
        if (jsonObject.get(key)?.jsonArray == null || jsonObject.get(key) == JsonNull) {
            return null
        }
        return JSONArray(jsonObject.get(key)!!.jsonArray, this, key)
    }

    fun getInt(key: String): Int? {
        return jsonObject.get(key)?.jsonPrimitive?.intOrNull
    }

    fun optInt(key: String, defaultValue: Int? = null): Int? {
        return getInt(key) ?: defaultValue
    }

    override fun toString(): String {
        return jsonObject.toString()
    }

    fun optBoolean(s: String, b: Boolean): Boolean {
        val boolean = getBoolean(s)
        return boolean ?: b
    }

    fun keySet(): Set<String> {
        return jsonObject.keys
    }

    fun isEmpty(): Boolean {
        return jsonObject.isEmpty()
    }

    fun similar(cleanedExpect: JSONObject): Boolean {
        return false
    }

    fun length(): Int {
        return jsonObject.size
    }

    fun opt(key: String): Any? {
        val serialised = JSONObject.serializeToObjects(jsonObject.get(key))
        return serialised
    }
}