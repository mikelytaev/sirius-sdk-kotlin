package com.sirius.library.agent.connections

import com.sirius.library.agent.RemoteParams

import com.sirius.library.agent.wallet.impl.cloud.AbstractGenericType
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

abstract class RemoteCallWrapper<T> : RemoteCall<T> {
   // private val typeToken: TypeToken<T> = object : TypeToken<T>(javaClass) {}
  //  val type: java.lang.reflect.Type = typeToken.getType()
    var rpc: AgentRPC
    var myClassT: T? = null
   // var objectClass: java.lang.Class? = null

    //  TypeToken<T> type = new TypeToken<T>(getClass()) {};
    var abstractGenericType: AbstractGenericType<T>? = null
   // var abstractDAo: AbstractDAO<T>? = null

    constructor(rpc: AgentRPC) {
        this.rpc = rpc
        abstractGenericType = object : AbstractGenericType<T>() {}
    //    abstractDAo = object : AbstractDAO<T>() {}
    }

    constructor(rpc: AgentRPC, objectClass:KClass<Any>?) {
        this.rpc = rpc
     //   this.objectClass = objectClass
    }

    fun serializeResponse(objecti: Any?): T {
        if (objecti is JSONObject) {
            return objecti.toString() as T
        } else if (objecti is JSONArray) {
            val objectList: MutableList<Any?> = ArrayList<Any?>()
            for (i in 0 until (objecti).length()) {
               val serREsp =  serializeResponse(objecti.get(i)!!) as Any?
                objectList.add(serREsp )
            }
            return objectList as T
        } else if (objecti is Pair<*,*>) {
            val firstObject: Any? = serializeResponse(objecti.first) as Any?
            val secondObject: Any? = serializeResponse(objecti.second) as Any?
            return Pair(firstObject, secondObject) as T
        } else if (objecti is Triple<*,*,*>) {
            val firstObject: Any? = serializeResponse(objecti.first) as Any?
            val secondObject: Any? = serializeResponse(objecti.second) as Any?
            val thirdObject: Any? = serializeResponse(objecti.third) as Any?
            return Triple(firstObject, secondObject, thirdObject) as T
        }
        return objecti as T
    }


    override fun remoteCall(type: String, params: RemoteParams.RemoteParamsBuilder?): T? {
        try {
            return remoteCallEx(type, params)
        } catch (siriusConnectionClosed: Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }

    @Throws(Exception::class)
    fun remoteCallEx(type: String, params: RemoteParams.RemoteParamsBuilder?): T? {
        val response: Any?
        if (params == null) {
            response = rpc.remoteCall(type)
        } else {
            response = rpc.remoteCall(type, params.build())
        }
        response?.let {
            return serializeResponse(response)
        }
        return null
    }
}
