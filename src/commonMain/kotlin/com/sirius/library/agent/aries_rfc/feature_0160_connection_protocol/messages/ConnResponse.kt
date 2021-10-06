package com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages

import com.sirius.library.agent.aries_rfc.Utils
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

class ConnResponse(msg: String) : ConnProtocolMessage(msg) {
    companion object {
        fun signField(crypto: AbstractCrypto, fieldValue: JSONObject, myVerkey: String?): JSONObject {
            return Utils.sign(crypto, fieldValue.toString(), myVerkey)
        }

        fun verifySignedField(crypto: AbstractCrypto, signedField: JSONObject): JSONObject? {
            val (first, second) = Utils.verifySigned(crypto, signedField)
            return if (second) {
                JSONObject(first)
            } else {
                null
            }
        }

        fun builder(): Builder<*> {
            return ConnResponseBuilder()
        }

        init {
            Message.registerMessageClass(ConnResponse::class, ConnProtocolMessage.PROTOCOL, "response")
        }
    }

    fun signConnection(crypto: AbstractCrypto, key: String) {
        val obj: JSONObject? = getMessageObj()
        obj?.put("connection~sig", signField(crypto, obj?.getJSONObject("connection") ?: JSONObject(), key))
        obj?.remove("connection")
    }

    fun verifyConnection(crypto: AbstractCrypto): Boolean {
        val connection: JSONObject? = verifySignedField(crypto, getMessageObj()?.optJSONObject("connection~sig") ?: JSONObject())
        if (connection != null) {
            getMessageObj()?.put("connection", connection)
            return true
        }
        return false
    }

    abstract class Builder<B : Builder<B>?> : ConnProtocolMessage.Builder<B>() {
        var did: String? = null
        var verkey: String? = null
        var endpoint: String? = null
        var didDocExtra: JSONObject? = null
        fun setDid(did: String?): B? {
            this.did = did
            return self()
        }

        fun setVerkey(verkey: String?): B? {
            this.verkey = verkey
            return self()
        }

        fun setEndpoint(endpoint: String?): B? {
            this.endpoint = endpoint
            return self()
        }

        fun setDidDocExtra(didDocExtra: JSONObject?): B? {
            this.didDocExtra = didDocExtra
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            val id: String? = jsonObject.optString("id")
            if (did != null && verkey != null && endpoint != null) {
                val extra: JSONObject? = if (didDocExtra != null) didDocExtra else JSONObject()
                jsonObject.put(
                    "connection",
                    JSONObject().put("DID", did).put("DIDDoc", buildDidDoc(did!!, verkey, endpoint, extra))
                )
            }
            return jsonObject
        }

        fun build(): ConnResponse {
            return ConnResponse(generateJSON().toString())
        }
    }

    private class ConnResponseBuilder : Builder<ConnResponseBuilder?>() {
        override fun self(): ConnResponseBuilder {
            return this
        }
    }
}
