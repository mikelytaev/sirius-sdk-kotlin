package com.sirius.library.agent.connections

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.wallet.impl.AbstractDAO
import com.sirius.library.agent.wallet.impl.AbstractGenericType
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass

abstract class RemoteCallWrapper<T> : RemoteCall<T> {
    private val typeToken: TypeToken<T> = object : TypeToken<T>(javaClass) {}
    val type: java.lang.reflect.Type = typeToken.getType()
    var rpc: AgentRPC
    var myClassT: T? = null
    var objectClass: java.lang.Class? = null

    //  TypeToken<T> type = new TypeToken<T>(getClass()) {};
    var abstractGenericType: AbstractGenericType<T>? = null
    var abstractDAo: AbstractDAO<T>? = null

    constructor(rpc: AgentRPC) {
        this.rpc = rpc
        abstractGenericType = object : AbstractGenericType<T>() {}
        abstractDAo = object : AbstractDAO<T>() {}
    }

    constructor(rpc: AgentRPC, objectClass:KClass<Any>?) {
        this.rpc = rpc
        this.objectClass = objectClass
    }

    fun serializeResponse(`object`: Any): T {
        if (`object` is JSONObject) {
            return `object`.toString() as T
        } else if (`object` is JSONArray) {
            val objectList: MutableList<Any> = ArrayList<Any>()
            for (i in 0 until (`object` as JSONArray).length()) {
                objectList.add(serializeResponse((`object` as JSONArray).get(i)))
            }
            return objectList as T
        } else if (`object` is Pair) {
            val firstObject: Any = serializeResponse(`object`.first!!)
            val secondObject: Any = serializeResponse(`object`.second!!)
            return Pair(firstObject, secondObject) as T
        } else if (`object` is Triple) {
            val firstObject: Any = serializeResponse(`object`.first!!)
            val secondObject: Any = serializeResponse(`object`.second!!)
            val thirdObject: Any = serializeResponse(`object`.third!!)
            return Triple(firstObject, secondObject, thirdObject) as T
        }
        return `object` as T
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
