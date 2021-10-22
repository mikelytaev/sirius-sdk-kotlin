package com.sirius.library.rpc

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.wallet.KeyDerivationMethod
import com.sirius.library.agent.wallet.abstract_wallet.model.*
import com.sirius.library.base.JsonSerializable
import com.sirius.library.encryption.Custom
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidType
import com.sirius.library.messaging.Message
import com.sirius.library.messaging.Type
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID
import kotlin.reflect.KClass

object Parsing {
    val CLS_MAP: MutableMap<String, KClass<*>> = HashMap<String, KClass<*>>()
    val CLS_MAP_REVERT: MutableMap<KClass<*>, String> = HashMap<KClass<*>, String>()
    init {
        CLS_MAP.put("application/cache-options", CacheOptions::class)
        CLS_MAP. put("application/purge-options", PurgeOptions::class)
        CLS_MAP. put("application/retrieve-record-options", RetrieveRecordOptions::class)
        CLS_MAP.put("application/nym-role", NYMRole::class)
        CLS_MAP. put("application/pool-action", PoolAction::class)
        CLS_MAP. put("application/key-derivation-method", KeyDerivationMethod::class)


        CLS_MAP_REVERT.put(CacheOptions::class, "application/cache-options")
        CLS_MAP_REVERT. put(PurgeOptions::class, "application/purge-options")
        CLS_MAP_REVERT.put(RetrieveRecordOptions::class, "application/retrieve-record-options")
        CLS_MAP_REVERT.put(NYMRole::class, "application/nym-role")
        CLS_MAP_REVERT.put(PoolAction::class, "application/pool-action")
        CLS_MAP_REVERT. put(KeyDerivationMethod::class, "application/key-derivation-method")
    }

    /**
     * @param msgType Aries RFCs attribute
     * https://github.com/hyperledger/aries-rfcs/tree/master/concepts/0020-message-types
     * @param future  Future to check response routine is completed
     * @param params  RPC call params
     * @return RPC service packet
     */
    fun buildRequest(msgType: String, future: Future, params: RemoteParams?): Message {
        try {
            val type: Type = Type.fromStr(msgType)
            if (!listOf("sirius_rpc", "admin", "microledgers", "microledgers-batched")
                    .contains(type.protocol)
            ) {
                throw SiriusInvalidType("Expect sirius_rpc protocol")
            }
        } catch (siriusInvalidType: SiriusInvalidType) {
            siriusInvalidType.printStackTrace()
        }
        val jsonObject = JSONObject()
        jsonObject.put("@type", msgType)
        jsonObject.put("@id", UUID.randomUUID.toString())
        jsonObject.put("@promise", future.promise().serializeToJSONObject())
        val paramsObject: JSONObject = incapsulateParam(params)
        jsonObject.put("params", paramsObject)
        return Message(jsonObject.toString())
    }

    //  CLS_MAP_REVERT = {v: k for k, v in CLS_MAP.items()}
    fun incapsulateParam(params: RemoteParams?): JSONObject {
        val paramsObject = JSONObject()
        if (params == null) {
            return paramsObject
        }
        val paramsMap: Map<String, Any?> = params.params
        val keys = paramsMap.keys
        for (key in keys) {
            val (first, second) = serializeVariable(paramsMap[key])
            val oneParamObject = JSONObject()
            if (first == null) {
                oneParamObject.put("mime_type", JSONObject.NULL)
            } else {
                oneParamObject.put("mime_type", first)
            }
            if (second == null) {
                oneParamObject.put("payload", JSONObject.NULL)
            } else {
                oneParamObject.put("payload", second)
            }
            paramsObject.put(key, oneParamObject)
        }
        return paramsObject
    }

    fun serializeObject(param: Any): Any? {
        var varParam: Any? = null
        val mimeType = CLS_MAP_REVERT[param::class]
        varParam = if (mimeType != null && param is JsonSerializable<*>) {
            (param as JsonSerializable<*>).serialize()
        } else if (param is Collection<*>) {
            val jsonArray = JSONArray()
            for (oneParam in param) {
                oneParam?.let {
                    val oneParamObject = serializeObject(oneParam)
                    jsonArray.put(oneParamObject)
                }
            }
            jsonArray
        } else if (param is ByteArray) {
            val custom = Custom
            custom.bytesToB64(param, false)
        } else if (param is JsonSerializable<*>) {
            (param as JsonSerializable<*>).serializeToJSONObject()
        } else param as? JSONObject
            ?: (param as? Number ?: ((param as? String)?.toString() ?: param.toString()))
        return varParam
    }

    /**
     * Serialize input variable to JSON-compatible string
     *
     * @param param input variable
     * @return tuple (type, variable serialized dump)
     */
    fun serializeVariable(param: Any?): Pair<String?, Any?> {
        if (param == null) {
            return Pair(null, null)
        }
        var mimeType = CLS_MAP_REVERT[param::class]
        if (param is ByteArray) {
            mimeType = "application/base64"
        }
        val varParam = serializeObject(param)
        return Pair(mimeType, varParam)
        /*if isinstance(var, CacheOptions):
        return CLS_MAP_REVERT[CacheOptions], var.serialize()
        elif isinstance(var, PurgeOptions):
        return CLS_MAP_REVERT[PurgeOptions], var.serialize()
        elif isinstance(var, RetrieveRecordOptions):
        return CLS_MAP_REVERT[RetrieveRecordOptions], var.serialize()
        elif isinstance(var, NYMRole):
        return CLS_MAP_REVERT[NYMRole], var.serialize()
        elif isinstance(var, PoolAction):
        return CLS_MAP_REVERT[PoolAction], var.serialize()
        elif isinstance(var, KeyDerivationMethod):
        return CLS_MAP_REVERT[KeyDerivationMethod], var.serialize()
        elif isinstance(var, bytes):
        return 'application/base64', base64.b64encode(var).decode('ascii')
    else:
        return None, var*/
    }
}
