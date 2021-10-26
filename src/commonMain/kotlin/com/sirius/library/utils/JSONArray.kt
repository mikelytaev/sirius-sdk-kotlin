package com.sirius.library.utils

import kotlinx.serialization.json.*

open class JSONArray : Iterable<Any?> {

    var jsonArray : JsonArray = buildJsonArray {  }
    var parentObject : JSONObject? = null
    var parentKey : String? = null
    constructor(list: List<String?>?) : this(){
        if(list==null){
            jsonArray  = buildJsonArray {  }
        }else{
            jsonArray =  buildJsonArray {
                list?.forEach {
                    it?.let {
                        this.add(it)
                       // val element = Json.parseToJsonElement(it)
                       // this.add(element)
                    }
                }
            }
        }

    }

    constructor(){
        jsonArray  = buildJsonArray {  }
    }
    constructor(jsonArray : JsonArray, parentObject : JSONObject? =null,parentKey : String? =  null ){
        this.jsonArray = jsonArray
        this.parentObject = parentObject
        this.parentKey = parentKey
    }

    constructor(optString: String?): this(){

        optString?.let {
            jsonArray =  Json.parseToJsonElement(optString).jsonArray
        }

    }


    fun length(): Int {
        return jsonArray.size
    }

    fun getJSONObject(i: Int): JSONObject? {
        return JSONObject(jsonArray.get(i).jsonObject)
    }

    fun put(credAttach: JSONObject): JSONArray {
        jsonArray = buildJsonArray {
            jsonArray.forEach {
                this.add(it)
            }
            this.add(credAttach.jsonObject)
        }
        parentObject?.let {
            it.jsonObject = buildJsonObject {
                it.jsonObject.entries.forEach {
                    put(it.key,it.value)
                }
                parentKey?.let {
                    put(it, jsonArray)
                }
            }
        }
        return this
    }

    override fun iterator(): Iterator<Any?> {
        val list : MutableList<Any?> = mutableListOf()
        jsonArray.iterator().forEach {
            val element = JSONObject.serializeToObjects(it)
            list.add(element)
        }
      val iterator =   list.iterator()
       return iterator
    }

    fun getString(i: Int): String? {
        return null
    }

    fun get(i: Int): Any? {
        return null
    }

    fun optJSONObject(i: Int): JSONObject? {
        return getJSONObject(i)
    }

    fun put(value: String): JSONArray {
        jsonArray = buildJsonArray {
            jsonArray.forEach {
                this.add(it)
            }
            this.add(value)
        }
        return this
    }

    fun isEmpty(): Boolean {
        return false
    }

    fun put(oneParamObject: Any?) {


    }

    fun getBoolean(i: Int): Boolean {
        return false
    }

    fun remove(i: Int) {


    }

    override fun toString(): String {
        return jsonArray.toString()
    }
}