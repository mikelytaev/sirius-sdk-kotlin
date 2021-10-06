package com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

class ConnRequest(msg: String) : ConnProtocolMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return ConnRequestBuilder()
        }

        init {
            Message.registerMessageClass(ConnRequest::class, ConnProtocolMessage.PROTOCOL, "request")
        }
    }

    val label: String?
        get() = getMessageObj()?.optString("label")

    abstract class Builder<B : Builder<B>?> : ConnProtocolMessage.Builder<B>() {
        var label: String? = null
        var did: String? = null
        var verkey: String? = null
        var endpoint: String? = null
        var didDocExtra: JSONObject? = null
        var connectionServices: MutableList<JSONObject> = ArrayList<JSONObject>()
        fun addConnectionService(service: JSONObject?): B {
            service?.let {
                connectionServices.add(service)
            }
            return self()
        }

        fun setLabel(label: String?): B {
            this.label = label
            return self()
        }

        fun setDid(did: String?): B {
            this.did = did
            return self()
        }

        fun setVerkey(verkey: String?): B {
            this.verkey = verkey
            return self()
        }

        fun setEndpoint(endpoint: String?): B {
            this.endpoint = endpoint
            return self()
        }

        fun setDidDocExtra(didDocExtra: JSONObject?): B {
            this.didDocExtra = didDocExtra
            return self()
        }

         override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            val id: String? = jsonObject.optString("id")
            if (label != null) {
                jsonObject.put("label", label)
            }
            if (did != null && verkey != null && endpoint != null) {
                val extra: JSONObject? = if (didDocExtra != null) didDocExtra else JSONObject()
                jsonObject.put(
                    "connection",
                    JSONObject().put("DID", did).put("DIDDoc", buildDidDoc(did!!, verkey, endpoint, extra))
                )
                for (s in connectionServices) {
                    jsonObject.getJSONObject("connection")?.getJSONObject("DIDDoc")?.getJSONArray("service")?.put(s)
                }
            }
            return jsonObject
        }

        fun build(): ConnRequest {
            return ConnRequest(generateJSON().toString())
        }
    }

    private class ConnRequestBuilder : Builder<ConnRequestBuilder?>() {
        protected override fun self(): ConnRequestBuilder {
            return this
        }
    }
}
