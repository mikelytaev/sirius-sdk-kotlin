package com.sirius.library.utils

import kotlinx.serialization.json.*

open class JSONArray : Iterable<Any> {

    var jsonArray : JsonArray = buildJsonArray {  }
    constructor(list: List<String>?) : this(){
        if(list==null){
            jsonArray  = buildJsonArray {  }
        }else{
            list?.forEach {
                val element = Json.parseToJsonElement(it)
            }
        }

    }

    constructor(){
        jsonArray  = buildJsonArray {  }
    }
    constructor(jsonArray : JsonArray){
        this.jsonArray = jsonArray
    }

    constructor(optString: String?): this(){

        optString?.let {
            jsonArray =  Json.parseToJsonElement(optString).jsonArray
        }

    }


    fun length(): Int {
        return 0
    }

    fun getJSONObject(i: Int): JSONObject? {

        return null
    }

    fun put(credAttach: JSONObject): JSONArray {

        return this
    }

    override fun iterator(): Iterator<Any> {
       return jsonArray.iterator()
    }

    fun getString(i: Int): String? {
        return null
    }

    fun get(i: Int): Any? {
        return null
    }

    fun optJSONObject(i: Int): JSONObject? {
        return null
    }

    fun put(credAttach: String): JSONArray {
        return this
    }

    fun isEmpty(): Boolean {
        return false
    }

    fun put(oneParamObject: Any?) {


    }
}