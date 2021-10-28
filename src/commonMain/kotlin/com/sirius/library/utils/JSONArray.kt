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

    fun putToAll(parentJson: JSONObject?, parentKey: String?) {
        if (parentJson != null && parentKey != null) {
            parentJson!!.jsonObject = buildJsonObject {
                parentJson!!.jsonObject.entries.forEach {
                    this.put(it.key, it.value)
                }
                this.put(parentKey!!, jsonArray)
                println("put=" + parentKey + " jsonObject=" + jsonArray)
            }
            if (parentJson.parentJson != null && parentJson.parentKey != null) {
                parentJson.putToAll(parentJson.parentJson, parentJson.parentKey)
            }
        }
    }

    fun put(credAttach: JSONObject): JSONArray {
        jsonArray = buildJsonArray {
            jsonArray.forEach {
                this.add(it)
            }
            this.add(credAttach.jsonObject)
        }
        putToAll(parentObject,parentKey )
        //putToAll()
      /*  parentObject?.let {
          //  parentObject.putToAll()
            it.jsonObject = buildJsonObject {
                it.jsonObject.entries.forEach {
                    put(it.key,it.value)
                }
                parentKey?.let {
                    put(it, jsonArray)
                }
            }
        }*/
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
        return jsonArray.get(i).jsonPrimitive.content
    }

    fun get(i: Int): Any? {
        return JSONObject.serializeToObjects(jsonArray.get(i))
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
        putToAll(parentObject,parentKey )
        return this
    }

    fun isEmpty(): Boolean {
        return jsonArray.isEmpty()
    }

    fun put(oneParamObject: Any?) : JSONArray {
        jsonArray = buildJsonArray {
            jsonArray.forEach {
                this.add(it)
            }
            val jsonElement = JSONObject.serializeToJsonElement(oneParamObject)
            this.add(jsonElement)
        }
        putToAll(parentObject,parentKey )
        return this
    }

    fun getBoolean(i: Int): Boolean {
        return jsonArray.get(i).jsonPrimitive.booleanOrNull ?: false
    }

    fun remove(i: Int) {


    }

    override fun toString(): String {
        return jsonArray.toString()
    }
}