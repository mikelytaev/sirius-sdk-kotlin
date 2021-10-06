package com.sirius.library.utils

import kotlinx.serialization.json.*
import kotlin.reflect.KClass

open class JSONObject {
    companion object {
        val NULL = JsonNull.content
    }

    var message: String? = null
    var jsonMap: Map<String, JsonElement> = HashMap()
    var jsonObject: JsonObject = buildJsonObject { }

    constructor() {

         jsonObject = buildJsonObject { }
    }

    constructor(jsonObject: JsonObject) {
        this.jsonObject = jsonObject
    }
    fun serialize(){
       // Json.encodeToJsonElement()
    }

    fun deserilaize(string : String, kClass: KClass<Any>){
        val data = Json.decodeFromString<KClass>(string)
    }


    //fun
    constructor(message: String?) {
        message?.let {
            this.message = message
            jsonObject = Json.parseToJsonElement(message).jsonObject

        }

    }

    fun optValue(key: String): Any? {
        return null
    }

    fun get(key: String): Any? {
        return null
    }

    fun optString(key: String, default : String? = null): String? {
        return null
    }

    fun getString(key: String): String? {
        return null
    }

    fun has(key: String): Boolean {
        return jsonObject.containsKey(key)
    }

    fun put(key: String, value: Any?): JSONObject {

        return this
    }

    fun remove(key: String) {

    }

    fun isNull(key: String): Boolean {
       // return jsonObject.get(key)?.jsonNull
        return false
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
        if (jsonObject.get(key)?.jsonObject == null) {
            return null
        }
        return JSONObject(jsonObject.get(key)!!.jsonObject)
    }

    fun optJSONArray(key: String): JSONArray? {
        if (jsonObject.get(key)?.jsonArray == null) {
            return null
        }
        return JSONArray(jsonObject.get(key)!!.jsonArray)
    }

    fun getInt(key: String): Int? {
        return jsonObject.get(key)?.jsonPrimitive?.intOrNull
    }

    fun optInt(recipient: String, defaultValue: Int?): Int? {
        return null
    }

    override fun toString(): String {
        return super.toString()
    }

    fun optBoolean(s: String, b: Boolean): Boolean {
       val boolean =  getBoolean(s)
       return boolean ?: b
    }

    fun keySet(): Set<String> {
       return jsonObject.keys
    }
}