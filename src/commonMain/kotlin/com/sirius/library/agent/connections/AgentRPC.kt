package com.sirius.library.agent.connections

import com.sirius.library.agent.RemoteParams
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.errors.sirius_exceptions.SiriusConnectionClosed
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.errors.sirius_exceptions.SiriusRPCError
import com.sirius.library.errors.sirius_exceptions.SiriusTimeoutRPC
import com.sirius.library.messaging.Message
import com.sirius.library.messaging.Type
import com.sirius.library.rpc.AddressedTunnel
import com.sirius.library.rpc.Future
import com.sirius.library.rpc.Parsing
import com.sirius.library.utils.Date
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.jvm.JvmOverloads

/**
 * RPC service.
 *
 *
 * Proactive form of Smart-Contract design
 */
class AgentRPC(serverAddress: String, credentials: ByteArray?, p2p: P2PConnection?, timeout: Int) :
    BaseAgentConnection(serverAddress, credentials, p2p, timeout) {
    var endpoints: List<Endpoint>
    var networks: List<String>
    var websockets: Map<String, WebSocket>
    var preferAgentSide: Boolean
    lateinit var tunnelRpc: AddressedTunnel
    lateinit var tunnelCoprotocols: AddressedTunnel
    override fun path(): String {
        return "rpc"
    }

    /**
     * Call Agent services
     *
     * @param msgType
     * @param params
     * @param waitResponse wait for response
     * @return
     */
    @Throws(Exception::class)
    fun remoteCall(msgType: String, params: RemoteParams?, waitResponse: Boolean): Any? {
        if (!connector!!.isOpen) {
            throw SiriusConnectionClosed("Open agent connection at first")
        }
        var expirationTime: Long = 0
        if (timeout !== 0) {
            expirationTime = (Date().time+ timeout * 1000) / 1000
        }
        val future = Future(tunnelRpc, expirationTime)
        val request: Message = Parsing.buildRequest(msgType, future, params)
        val payload: String = request.serialize()
        val msgTyp: Type = Type.fromStr(msgType)
        val isEncryptes: Boolean = !listOf("admin", "microledgers", "microledgers-batched")
            .contains(msgTyp.protocol)
        val isPosted: Boolean = tunnelRpc.post(request, isEncryptes)
        if (!isPosted) {
            throw SiriusRPCError()
        }
        if (waitResponse) {
            val success: Boolean = future.waitPromise(timeout)
            if (success) {
                if (future.hasException()) {
                    future.raiseException()
                } else {
                    return future.getValue()
                }
            } else {
                throw SiriusTimeoutRPC()
            }
        }
        return null
    }

    @JvmOverloads
    @Throws(Exception::class)
    fun remoteCall(msgType: String, params: RemoteParams? = null): Any? {
        return remoteCall(msgType, params, true)
    }

    override fun setup(context: Message) {
        super.setup(context)
        //Extract proxy info
        val proxies: MutableList<JSONObject> = ArrayList<JSONObject>()
        val proxiesArray: JSONArray? = context.getJSONArrayFromJSON("~proxy", null)
        if (proxiesArray != null) {
            for (i in 0 until proxiesArray.length()) {
                proxiesArray.getJSONObject(i)?.let {
                    proxies.add(it)
                }

            }
        }
        var channel_rpc: String? = null
        var channel_sub_protocol: String? = null
        for (proxy in proxies) {
            if ("reverse" == proxy.getString("id")) {
                channel_rpc = proxy.getJSONObject("data")?.getJSONObject("json")?.getString("address")
            } else if ("sub-protocol" == proxy.getString("id")) {
                channel_sub_protocol = proxy.getJSONObject("data")?.getJSONObject("json")?.getString("address")
            }
        }
        if (channel_rpc == null) {
            throw RuntimeException("rpc channel is empty")
        }
        if (channel_sub_protocol == null) {
            throw RuntimeException("sub-protocol channel is empty")
        }
        tunnelRpc = AddressedTunnel(channel_rpc, connector, connector, p2p)
        tunnelCoprotocols = AddressedTunnel(channel_sub_protocol, connector, connector, p2p)
        //Extract active endpoints
        val endpointsArray: JSONArray? = context.getJSONArrayFromJSON("~endpoints", null)
        val endpointsCollection: MutableList<Endpoint> = ArrayList<Endpoint>()
        if (endpointsArray != null) {
            for (i in 0 until endpointsArray.length()) {
                val endpointObj: JSONObject? = endpointsArray.getJSONObject(i)
                val bodyObj: JSONObject? = endpointObj?.getJSONObject("data")?.getJSONObject("json")
                val address: String = bodyObj?.getString("address")?:""
                val frontendKey: String = bodyObj?.optString("frontend_routing_key") ?:""
                if (!frontendKey.isEmpty()) {
                    val routingKeys: JSONArray? = bodyObj?.getJSONArray("routing_keys")
                    if (routingKeys != null) {
                        for (z in 0 until routingKeys.length()) {
                            val routingKey: JSONObject? = routingKeys?.getJSONObject(z)
                            val isDefault: Boolean = routingKey?.getBoolean("is_default") ?: false
                            val key: String? = routingKey?.getString("routing_key")
                            val routingKeysList: MutableList<String> = ArrayList<String>()
                            key?.let {
                                routingKeysList.add(it)
                            }
                            routingKeysList.add(frontendKey)
                            endpointsCollection.add(Endpoint(address, routingKeysList, isDefault))
                        }
                    }
                } else {
                    endpointsCollection.add(Endpoint(address, listOf(), false))
                }
            }
        }
        if (endpointsCollection.isEmpty()) {
            throw RuntimeException("Endpoints are empty")
        }
        endpoints = endpointsCollection
        //Extract Networks
        val networkList: MutableList<String> = ArrayList<String>()
        val networksArray: JSONArray = context.getJSONArrayFromJSON("~networks", JSONArray()) ?:JSONArray()
        for (i in 0 until networksArray.length()) {
            val network: String? = networksArray?.getString(i)
            network?.let {
                networkList.add(network)
            }

        }
        networks = networkList
    }

    /**
     * Send Message to other Indy compatible agent
     *
     * @param message     message
     * @param their_vk    Verkey of recipients
     * @param endpoint    Endpoint Address of recipient
     * @param myVk        Verkey of sender (None for anocrypt mode)
     * @param routingKeys Routing keys if it is exists
     * @param coprotocol  True if message is part of co-protocol stream
     * See:
     * - https://github.com/hyperledger/aries-rfcs/tree/master/concepts/0003-protocols
     * - https://github.com/hyperledger/aries-rfcs/tree/master/concepts/0008-message-id-and-threading
     * @return Response message if coprotocol is True
     */
    @Throws(SiriusConnectionClosed::class, SiriusRPCError::class, SiriusInvalidPayloadStructure::class)
    fun sendMessage(
        message: Message?,
        their_vk: List<String?>?,
        endpoint: String,
        myVk: String?,
        routingKeys: List<String?>?,
        coprotocol: Boolean
    ): Message? {
        var routingKeys = routingKeys
        if (!connector.isOpen) {
            throw SiriusConnectionClosed("Open agent connection at first")
        }
        val paramsBuilder: RemoteParams.RemoteParamsBuilder = RemoteParams.RemoteParamsBuilder.create()
            .add("message", message)
        if (routingKeys == null) {
            routingKeys = ArrayList<String>()
        }
        paramsBuilder.add("routing_keys", routingKeys)
            .add("recipient_verkeys", their_vk)
            .add("sender_verkey", myVk)
        var response: Any? = null
        if (preferAgentSide) {
            paramsBuilder.add("timeout", timeout)
            paramsBuilder.add("endpoint_address", endpoint)
            try {
                response =
                    remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/send_message", paramsBuilder.build())
            } catch (siriusRPCError: Exception) {
                siriusRPCError.printStackTrace()
            }
        } else {
            try {
                val wired = remoteCall(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prepare_message_for_send",
                    paramsBuilder.build()
                )
                if (endpoint.startsWith("ws://") || endpoint.startsWith("wss://")) {
                }
            } catch (siriusRPCError: Exception) {
                siriusRPCError.printStackTrace()
            }
        }
        val ok = (response as Pair?)!!.first as Boolean
        val body = response!!.second as String
        if (!ok) {
            throw SiriusRPCError(body)
        } else {
            if (coprotocol) {
                return readProtocolMessage()
            }
        }
        return null
    }

    @Throws(SiriusConnectionClosed::class)
    fun sendMessageBatched(message: Message?, batches: List<RoutingBatch>?): List<Pair<Boolean, String?>> {
        if (!connector.isOpen) {
            throw SiriusConnectionClosed("Open agent connection at first")
        }
        val params: RemoteParams = RemoteParams.RemoteParamsBuilder.create().add("message", message).add(
            "timeout",
            timeout
        ).add("batches", batches).build()
        try {
            val response = remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/send_message_batched", params)
            val jsonArr: JSONArray? = response as JSONArray?
            val res: MutableList<Pair<Boolean, String>> = .ArrayList<Pair<Boolean, String>>()
            for (o in jsonArr) {
                val internalArr: JSONArray = o as JSONArray
                res.add(Pair(internalArr.getBoolean(0), internalArr.get(1).toString()))
            }
            return res
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }

    fun startProtocolWithThreads(threads: List<String?>?, timeToLiveSec: Int) {
        try {
            this.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/start_protocol",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("threads", threads)
                    .add("channel_address", tunnelCoprotocols.address)
                    .add("ttl", timeToLiveSec).build()
            )
        } catch (siriusRPCError: Exception) {
            siriusRPCError.printStackTrace()
        }
    }

    @JvmOverloads
    fun stopProtocolWithThreads(threads: List<String?>?, offResponse: Boolean = false) {
        try {
            this.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/stop_protocol",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("threads", threads)
                    .add("off_response", offResponse).build(),
                !offResponse
            )
        } catch (siriusRPCError: Exception) {
            siriusRPCError.printStackTrace()
        }
    }

    fun startProtocolWithThreading(thid: String?, timeToLiveSec: Int) {
        try {
            this.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/start_protocol",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("thid", thid)
                    .add("channel_address", tunnelCoprotocols.address)
                    .add("ttl", timeToLiveSec).build()
            )
        } catch (siriusRPCError: Exception) {
            siriusRPCError.printStackTrace()
        }
    }

    fun stopProtocolWithThreading(thid: String?, offResponse: Boolean) {
        try {
            this.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/stop_protocol",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("thid", thid)
                    .add("off_response", offResponse).build(),
                !offResponse
            )
        } catch (siriusRPCError: Exception) {
            siriusRPCError.printStackTrace()
        }
    }

    fun startProtocolForP2P(
        senderVerkey: String?,
        recipientVerkey: String?,
        protocols: List<String?>?,
        timeToLiveSec: Int
    ) {
        try {
            this.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/start_protocol",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("sender_verkey", senderVerkey)
                    .add("recipient_verkey", recipientVerkey)
                    .add("protocols", protocols)
                    .add("channel_address", tunnelCoprotocols.address)
                    .add("ttl", timeToLiveSec).build()
            )
        } catch (siriusRPCError: Exception) {
            siriusRPCError.printStackTrace()
        }
    }

    fun stopProtocolForP2P(
        senderVerkey: String?,
        recipientVerkey: String?,
        protocols: List<String?>?,
        offResponse: Boolean
    ) {
        try {
            this.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/stop_protocol",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("sender_verkey", senderVerkey)
                    .add("recipient_verkey", recipientVerkey)
                    .add("protocols", protocols)
                    .add("off_response", offResponse).build(),
                !offResponse
            )
        } catch (siriusRPCError: Exception) {
            siriusRPCError.printStackTrace()
        }
    }

    @Throws(SiriusInvalidPayloadStructure::class)
    fun readProtocolMessage(): Message? {
        return tunnelCoprotocols.receive(timeout)
    }

    init {
        endpoints = ArrayList<Endpoint>()
        networks = ArrayList<String>()
        websockets = HashMap<String, WebSocket>()
        preferAgentSide = true

        //self.__connector = aiohttp.TCPConnector(verify_ssl = False, keepalive_timeout = 60)
    }
}
