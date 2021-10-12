package com.sirius.library.rpc

import com.sirius.library.base.JsonSerializable
import com.sirius.library.errors.IndyException
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.errors.sirius_exceptions.SiriusPendingOperation
import com.sirius.library.errors.sirius_exceptions.SiriusPromiseContextException
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger
import com.sirius.library.utils.UUID
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * "Futures and Promises pattern.
 * (http://dist-prog-book.com/chapter/2/futures.html)
 *
 *
 *
 *
 * Server point has internal communication schemas and communication addresses for
 * Aries super-protocol/sub-protocol behaviour
 * (https://github.com/hyperledger/aries-rfcs/tree/master/concepts/0003-protocols).
 *
 *
 * Future hide communication addresses specifics of server-side service (cloud agent) and pairwise configuration
 * of communication between sdk-side and agent-side logic, allowing to take attention on
 * response awaiting routines.
 */
class Future(var tunnel: AddressedTunnel) {
    var expirationTime: Long = 0
    var id: String
    var value1: Any? = null
    var readOk = false
    var exception: JSONObject? = null

    /**
     * @param addressedTunnel communication tunnel for server-side cloud agent
     * @param expirationTime  time of response expiration
     */
    constructor(addressedTunnel: AddressedTunnel, expirationTime: Long) : this(addressedTunnel) {
        this.expirationTime = expirationTime
        tunnel = addressedTunnel
    }

    /**
     * Promise info builder
     *
     * @return: serialized promise dump
     */
    fun promise(): FuturePromise {
        return FuturePromise(id, tunnel.address, expirationTime)
    }

    /**
     * "Wait for response
     *
     * @param timeout waiting timeout in seconds
     * @return True/False
     */
    fun waitPromise(timeout: Int): Boolean {
        if (readOk) {
            return true
        }
        if (timeout == 0) {
            return false
        }
        try {
            val message: Message? = tunnel.receive(timeout)
            val payload: String? = message?.serialize()
            //(payload.get('~thread', {}).get('thid', None) == self.__id
            val threadObj: JSONObject? = message?.getJSONOBJECTFromJSON("~thread")
            var threadId: String? = null
            if (threadObj != null) {
                threadId = threadObj.getString("thid")
            }
            val isType = MSG_TYPE == message?.getType()
            val isTypeId = id == threadId
            if (MSG_TYPE == message?.getType() && id == threadId) {
                val exception: JSONObject? = message!!.getJSONOBJECTFromJSON("exception")
                if (exception == null) {
                    val value: Any? = message?.getObjectFromJSON("value")
                    val is_tuple: Boolean = message?.getBooleanFromJSON("is_tuple") ?: false
                    val is_bytes: Boolean = message?.getBooleanFromJSON("is_bytes") ?: false
                    if (is_tuple) {
                        if ((value as JSONArray).length() == 2) {
                            this.value1 = Pair<Any?, Any?>((value as JSONArray).get(0), (value as JSONArray).get(1))
                        } else if ((value as JSONArray).length() == 3) {
                            this.value1 = Triple<Any?, Any?, Any?>(
                                (value as JSONArray).get(0),
                                (value as JSONArray).get(1),
                                (value as JSONArray).get(2)
                            )
                        } else {
                            this.value1 = value
                        }
                    } else if (is_bytes) {
                        val custom = com.sirius.library.encryption.Custom
                        this.value1 = custom.b64ToBytes(value.toString(), false)
                    } else {
                        this.value1 = value
                    }
                } else {
                    this.exception = exception
                }
                readOk = true
                return true
            } else {
                Logger.getLogger("").info( "Unexpected payload" + message?.serialize().toString() + "Expected id: " + id)
            }
        } catch (siriusInvalidPayloadStructure: SiriusInvalidPayloadStructure) {
            siriusInvalidPayloadStructure.printStackTrace()
        }
        return false
    }

    /**
     * Get response value.
     *
     * @throws SiriusPendingOperation : response was not received yet. Call walt(0) to safely check value persists.
     * @return: value
     */
    @Throws(SiriusPendingOperation::class)
    fun getValue(): Any? {
        return if (readOk) {
            value1
        } else {
            throw SiriusPendingOperation()
        }
    }
    /*

     */
    /**
     * "Wait for response
     *
     * @param timeout waiting timeout in seconds
     * @return True/False
     */
    /*

    public boolean waitPromise(int timeout)  {
        if (readOk) {
            return true;
        }
        if (timeout == 0) {
            return false;
        }

        try {
            Message message = tunnel.receive(timeout);
            JsonObject messageObject = message.serializeToJsonObject();
            JSONObject threadObj = message.getJSONOBJECTFromJSON("~thread");
            String threadId = null;
            if (threadObj != null) {
                threadId = threadObj.getString("thid");
            }
            boolean isType = MSG_TYPE.equals(message.getType());
            boolean isTypeId = id.equals(threadId);
            if (MSG_TYPE.equals(message.getType()) && id.equals(threadId)) {
                JSONObject exception = message.getJSONOBJECTFromJSON("exception");
                if (exception == null) {
                    JsonElement valueElement =  messageObject.get("value");
                    Object value =   message.getObjectFromJSON("value");

                    boolean is_tuple =    messageObject.getAsJsonPrimitive("is_tuple").getAsBoolean();
                    boolean is_bytes =     messageObject.getAsJsonPrimitive("is_bytes").getAsBoolean();
                    if(is_tuple){
                        JsonArray valueArray =  messageObject.getAsJsonArray();
                        if (valueArray.size() == 2) {
                            this.value = new Pair<JsonElement,JsonElement>(valueArray.get(0),valueArray.get(1));
                        */
    /*}else if(valueArray.size() == 3){
                            this.value = new Triple<JsonElement,JsonElement,JsonElement>(valueArray.get(0),valueArray.get(1),valueArray.get(2));
                        */
    /*
}else {
                            this.value = value;
                        }
                    }else if(is_bytes){
                        Custom custom = new Custom();
                        this.value =  custom.b64ToBytes(valueElement.getAsString(),false);
                    }else{
                        this.value = value;
                    }
                } else {
                    this.exception = exception;
                }
                readOk  =true;
                return true;
            } else {
                System.out.println("Unexpected payload" + message.serialize() + "Expected id: " + id);
            }


        } catch (SiriusInvalidPayloadStructure  siriusInvalidPayloadStructure) {
            siriusInvalidPayloadStructure.printStackTrace();
        }

        return false;
    }
*/
    /**
     * Check if response was interrupted with exception
     *
     * @throws SiriusPendingOperation: response was not received yet. Call walt(0) to safely check value persists.
     * @return: True if request have done with exception
     */
    @Throws(SiriusPendingOperation::class)
    fun hasException(): Boolean {
        if (!readOk) {
            throw SiriusPendingOperation()
        }
        return exception != null
    }

    /**
     * Get exception that have interrupted response routine on server-side.
     *
     * @return Exception instance or None if it does not exists
     */
    val futureException: Exception?
        get() {
            try {
                if (hasException()) {
                    return if (exception?.optJSONObject("indy") != null) {
                        val indy_exc: JSONObject = exception?.getJSONObject("indy") ?: JSONObject()
                        IndyException.fromSdkError(indy_exc?.getInt("error_code") ?: 0, indy_exc)
                    } else {
                        SiriusPromiseContextException(exception?.optString("class_name") ?: "",
                            exception?.optString("printable") ?:""
                        )
                    }
                }
            } catch (siriusPendingOperation: SiriusPendingOperation) {
                siriusPendingOperation.printStackTrace()
            }
            return null
        }

    /**
     * Raise exception if exists
     *
     * @throws SiriusValueEmpty: raises if exception is empty
     */
    @Throws(Exception::class)
    fun raiseException() {
        try {
            if (hasException()) {
                throw futureException?: Exception()
            }
        } catch (siriusPendingOperation: SiriusPendingOperation) {
            siriusPendingOperation.printStackTrace()
        }
    }
    @Serializable
    class FuturePromise(var id: String, var channel_address: String, var expiration_stamp: Long) :
        JsonSerializable<FuturePromise> {

        override fun serialize(): String? {
            return Json.encodeToString(this)
        }

        override fun deserialize(string: String): FuturePromise {
            return Json.decodeFromString<FuturePromise>(string)
        }

        override fun serializeToJSONObject(): JSONObject {
            val json = JSONObject(serialize())
            return json
        }

    }

    companion object {
        const val MSG_TYPE = "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/future"
    }

    /**
     * @param addressedTunnel communication tunnel for server-side cloud agent
     */
    init {
        id = UUID.randomUUID.toString()
    }
}
