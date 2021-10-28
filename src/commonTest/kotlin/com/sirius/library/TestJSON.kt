package com.sirius.library

import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.test.Test

class TestJSON {



    fun testSimple(){

    }

    @Test
    fun testMany(){
        val result: JSONObject =
            JSONObject().put("self_attested_attributes", JSONObject())
                .put("requested_attributes", JSONObject())
                .put("requested_predicates", JSONObject())
        val requestedAttributes =  JSONObject().put("test1","test1").put("test2","test2").put("test3","test3")

        for (attrReferent in requestedAttributes.keySet()) {
            val credForAttrStr: List<String> = listOf("testname", "testvalue")
            val credForAttr: JSONArray = JSONArray(credForAttrStr)
            println("credForAttr="+credForAttr)
            var collection: JSONArray? =
                result.optJSONObject("requested_attributes")?.optJSONArray(attrReferent)
            collection = if (collection != null) collection else JSONArray()
            for (o in credForAttr) collection.put(o)
            println("collection="+collection)
            result.optJSONObject("requested_attributes")?.put(attrReferent, collection)
        }

        println("result="+result)
    }


    @Test
    fun testComplicatedJsonObject(){
        val result: JSONObject =
            JSONObject().put("self_attested_attributes", JSONObject())
                .put("requested_attributes", JSONObject().put("inside", JSONObject().put("inside2", JSONObject())))
                .put("requested_predicates", JSONObject())
        val requestedAttributes =  JSONObject().put("test1","test1").put("test2","test2").put("test3","test3")

        for (attrReferent in requestedAttributes.keySet()) {
            val credForAttrStr: List<String> = listOf("testname", "testvalue")
            val credForAttr: JSONArray = JSONArray(credForAttrStr)
            println("credForAttr="+credForAttr)
            var collection: JSONArray? =
                result.optJSONObject("requested_attributes")?.optJSONArray(attrReferent)
            collection = if (collection != null) collection else JSONArray()
            for (o in credForAttr) collection.put(o)
            println("collection="+collection)
            result.optJSONObject("requested_attributes")?.optJSONObject("inside")?.optJSONObject("inside2")?.put(attrReferent, collection)
        }

        println("result="+result)
    }

    @Test
    fun testSimpleJsonArray(){
        val requestedAttributes =  JSONArray().put("test1").put("test2").put("test3")
        println("requestedAttributes="+requestedAttributes)
    }

    @Test
    fun testSimpleObjectJsonArray(){
        val requestedAttributes = JSONObject().put("inside",JSONArray().put("test1").put("test2").put("test3"))
        println("requestedAttributes="+requestedAttributes)
    }


    @Test
    fun testSimpleJsonArrayJsonArray(){
        val requestedAttributes = JSONObject().put("inside",JSONArray().put(JSONArray().put("test").put("test2")).put("test2").put("test3"))
        println("requestedAttributes="+requestedAttributes)
    }


    @Test
    fun testComplicatedJsonArray(){
        val requestedAttributes =  JSONArray().put("test1").put("test2").put("test3")
        val result: JSONObject =
            JSONObject().put("self_attested_attributes", JSONObject())
                .put("requested_attributes", JSONObject().put("inside", JSONArray()))
                .put("requested_predicates", JSONObject())

        for(string in requestedAttributes){
            result.optJSONObject("requested_attributes")?.optJSONArray("inside")?.put(string)
        }
        println("result="+result)
    }
}