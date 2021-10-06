package com.sirius.library.messaging

import com.sirius.library.base.JsonSerializable
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidMessage
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidType
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID
import kotlin.reflect.KClass

open class Message : JsonSerializable<Message> {
    val FIELD_TYPE = "@type"
    val FIELD_ID = "@id"

    constructor() {
    }

    constructor(message: String) {
        init(message)
    }

    constructor(message: JSONObject) {
        init(message.toString())
    }


    fun getType(): String? {
        return messageObj.optString(FIELD_TYPE)
    }

    fun getId(): String? {
        return messageObj.optString(FIELD_ID)
    }

    fun setId(id: String?) {
        this.messageObj.put(FIELD_ID, id)
    }

    fun getMessageObj(): JSONObject {
        return messageObj
    }

    lateinit var messageObj: JSONObject
    var typeOfType: Type? = null

    fun getVersion(): String? {
        return this.typeOfType?.version
    }

    fun getDocUri(): String? {
        return typeOfType!!.docUri
    }

/*
    fun Message(message: String?) {
        init(message)
    }

    fun Message(message: JSONObject) {
        init(message.toString())
    }
*/

    open fun init(message: String) {
        messageObj = JSONObject(message)
        if (!messageObjectHasKey(FIELD_TYPE)) {
            SiriusInvalidMessage("No @type in message").printStackTrace()
        }
        try {
            typeOfType = Type.fromStr(messageObj!!.optString(FIELD_TYPE)?:"")
        } catch (siriusInvalidType: SiriusInvalidType) {
            siriusInvalidType.printStackTrace()
        }
        if (!messageObj!!.has(FIELD_ID)) {
            messageObj!!.put(FIELD_ID, generateId())
        }
    }

    fun getObjectFromJSON(key: String): Any? {
        return if (messageObjectHasKey(key)) {
            if (messageObj!!.isNull(key)) {
                null
            } else messageObj!!.get(key)
        } else null
    }

    fun getStringFromJSON(key: String): String? {
        if (messageObjectHasKey(key)) {
            val value: String? = messageObj?.optString(key)
            return if (value == null || value.isEmpty()) {
                ""
            } else value
        }
        return ""
    }

    fun getBooleanFromJSON(key: String): Boolean? {
        return if (messageObjectHasKey(key)) {
            messageObj.getBoolean(key)
        } else null
    }

    fun messageObjectHasKey(key: String): Boolean {
        return messageObj.has(key) ?: false
    }

    fun getJSONOBJECTFromJSON(key: String): JSONObject? {
        return if (messageObjectHasKey(key)) {
            messageObj.optJSONObject(key)
        } else null
    }

    fun getJSONOBJECTFromJSON(key: String, defaultValue: JSONObject?): JSONObject? {
        return if (messageObjectHasKey(key)) {
            messageObj.optJSONObject(key)
        } else defaultValue
    }

    fun getJSONOBJECTFromJSON(key: String, defaultValue: String?): JSONObject? {
        return if (messageObjectHasKey(key)) {
            messageObj.optJSONObject(key)
        } else JSONObject(defaultValue)
    }

    fun getJSONArrayFromJSON(key: String, defaultValue: JSONArray?): JSONArray? {
        return if (messageObjectHasKey(key)) {
            messageObj.getJSONArray(key) ?: return defaultValue
        } else defaultValue
    }

    fun generateId(): String {
        return UUID.randomUUID.toString()
    }

    override fun serialize(): String {
        return messageObj.toString()
    }

    override fun serializeToJSONObject(): JSONObject {
        return JSONObject(messageObj.toString())
    }

    override fun deserialize(string: String): Message {
        return Message(string ?: "")
    }


    companion object {

        fun generateId(): String {
            return UUID.randomUUID.toString()
        }

        val MSG_REGISTRY: MutableList<Triple<KClass<out Message>, String, String>> =
            ArrayList<Triple<KClass<out Message>, String, String>>()

        fun registerMessageClass(clas: KClass<out Message>, protocol: String) {
            registerMessageClass(clas, protocol, "*")
        }

        fun registerMessageClass(
            clas: KClass<out Message>,
            protocol: String,
            name: String?
        ) {
            var name = name
            if (name == null) name = "*"
            for (i in MSG_REGISTRY.indices) {
                if (MSG_REGISTRY[i].first.equals(clas)) {
                    MSG_REGISTRY.set(i, Triple(clas, protocol, name))
                    return
                }
            }
            MSG_REGISTRY.add(Triple(clas, protocol, name))
        }

        fun getProtocolAndName(clas: KClass<out Message>): Pair<String?, String?> {
            for (triple in MSG_REGISTRY) {
                if (triple.first.equals(clas)) {
                    return Pair(triple.second, triple.third)
                }
            }
            return Pair(null, null)
        }


        fun restoreMessageInstance(payload: String?): Pair<Boolean, Message?> {
            if(payload==null){
                return Pair(false, null)
            }
            val message: Message = Message(payload)
            val protocol: String? = message.typeOfType?.protocol
            val name: String? = message.typeOfType?.name
            var clssTo: KClass<out Message>? = null
            for (triple  in MSG_REGISTRY) {
                if (triple.second.equals(protocol) && (triple.third.equals(name) || triple.third.equals("*"))) {
                    clssTo = triple.first
                }
            }
            if (clssTo != null) {
                //TODO FABRIC or PLATFORM
            /*    constructor =  MessageFabric.construct()

                val constructor: java.lang.reflect.Constructor<out Message> =
                    clssTo.getConstructor(
                        String::class.java
                    )*/
                return Pair(true, MessageFabric.newInstance(payload))
            }
            return Pair(false, null)
        }

    }




}





