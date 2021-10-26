package com.sirius.library.agent

import com.sirius.library.agent.connections.AgentEvents
import com.sirius.library.agent.coprotocols.AbstractCoProtocolTransport
import com.sirius.library.agent.coprotocols.PairwiseMobileCoProtocolTransport
import com.sirius.library.agent.coprotocols.TheirEndpointMobileCoProtocolTransport
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.pairwise.TheirEndpoint
import com.sirius.library.agent.pairwise.WalletPairwiseList
import com.sirius.library.agent.storages.InWalletImmutableCollection
import com.sirius.library.agent.wallet.MobileWallet
import com.sirius.library.base.CompleteFuture
import com.sirius.library.base.WebSocketConnector
import com.sirius.library.errors.IndyException
import com.sirius.library.messaging.Message
import com.sirius.library.utils.*
import com.sirius.library.utils.StringUtils.UTF_8
import io.ktor.client.*
import org.hyperledger.indy.sdk.LibIndy


import org.hyperledger.indy.sdk.crypto.Crypto
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet
import java.lang.System
import java.util.concurrent.CompletableFuture

class MobileAgent(walletConfig: JSONObject?, walletCredentials: JSONObject?) :
    AbstractAgent() {
    var walletConfig: JSONObject? = null
    var walletCredentials: JSONObject? = null
    var timeoutSec = 60
    var mediatorAddress: String? = null

    var sender: BaseSender? = null
   /* var sender: BaseSender? = object : BaseSender() {
        override fun sendTo(endpoint: String?, data: ByteArray?): Boolean {
            if (endpoint.startsWith("http")) {
                try {
                    val httpClient: HttpClient = HttpClients.createDefault()
                    val httpPost = HttpPost(endpoint)
                    httpPost.setHeader("content-type", "application/ssi-agent-wire")
                    httpPost.setEntity(ByteArrayEntity(cryptoMsg))
                    httpClient.execute(httpPost)
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            } else if (endpoint.startsWith("ws")) {
                val webSocket: WebSocketConnector? = getWebSocket(endpoint)
                if (!webSocket.isOpen()) webSocket.open()
                webSocket.write(cryptoMsg)
            } else {
                throw java.lang.RuntimeException("Not yet supported!")
            }
            return true
        }

        override fun open(endpoint: String?) {
            getWebSocket(endpoint)
        }

        override fun close() {
            webSockets.forEach {
                it.value.close()
            }
        }
    }*/
    var indyWallet: Wallet? = null
    var webSockets: MutableMap<String, WebSocketConnector> = HashMap<String, WebSocketConnector>()

    inner class MobileAgentEvents : AgentEvents {
        var future: CompletableFutureKotlin<Message?>? = null
        override fun pull(): CompletableFutureKotlin<Message?>? {
            future = CompletableFutureKotlin<Message?>()
            return future
        }
    }

    var events: MutableList<Pair<MobileAgentEvents, Listener>> =
        ArrayList<Pair<MobileAgentEvents, Listener>>()

    fun create() {
        try {
            Wallet.createWallet(walletConfig.toString(), walletCredentials.toString())
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            if (!e.message!!.contains("WalletExistsException")) e.printStackTrace()
        }
    }

    override fun open() {
        try {
            indyWallet = Wallet.openWallet(walletConfig.toString(), walletCredentials.toString())
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            if (!e.message!!.contains("WalletAlreadyOpenedException")) e.printStackTrace()
        }
        if (indyWallet != null) {
            wallet = MobileWallet(indyWallet!!)
        }
        pairwiseList = WalletPairwiseList(wallet!!.pairwise, wallet!!.did)
        if (storage == null) {
            storage = InWalletImmutableCollection(wallet!!.nonSecrets)
        }
        for (network in networks.orEmpty()) {
            ledgers.put(network, Ledger(network, wallet!!.ledger, wallet!!.anoncreds, wallet!!.cache, storage!!))
        }
    }

    private val networks: List<String>?
        private get() {
            try {
                val str: String = Pool.listPools().get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
                val arr: JSONArray = JSONArray(str)
                val networks: MutableList<String> = ArrayList<String>()
                for (o in arr) {
                   val poolString =  (o as JSONObject).optString("pool")
                    poolString?.let {
                        networks.add(poolString)
                    }
                }
                return networks
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }
    override val isOpen: Boolean
        get() = indyWallet != null
    override val name: String
        get() = "Mobile agent"


    override fun sendMessage(
        message: Message?,
        their_vk: List<String?>?,
        endpoint: String,
        my_vk: String?,
        routing_keys: List<String?>?
    ) {
        if (routing_keys?.isEmpty()==false) throw java.lang.RuntimeException("Not yet supported!")
        val cryptoMsg = packMessage(message?: Message(), my_vk, their_vk.orEmpty())
        if (sender != null) {
            val isSend = sender!!.sendTo(endpoint, cryptoMsg)
            //return new Pair<>(isSend, null);
        }
    }

  /*  fun getWebSocket(endpoint: String?): WebSocketConnector? {
        return if (webSockets.containsKey(endpoint)) {
            webSockets[endpoint]
        } else {
            val webSocket = WebSocketConnector(endpoint ?:"", "", null)
            val fAgent = this
            webSocket.readCallback = object : java.util.function.Function<ByteArray?, java.lang.Void?>() {

                override fun apply(bytes: ByteArray?): Void? {
                    fAgent.receiveMsg(bytes)
                    return null
                }
            }
            webSocket.open()
            webSockets[endpoint?:""] = webSocket
            webSocket
        }
    }*/

    fun connect(endpoint: String?) {
        sender!!.open(endpoint)
    }

    fun packMessage(msg: Message, myVk: String?, theirVk: List<String?>): ByteArray? {
        val receivers: JSONArray = JSONArray(theirVk)
        try {
            return Crypto.packMessage(
                indyWallet, receivers.toString(),
                myVk, StringUtils.stringToBytes(msg.messageObj.toString(), UTF_8)
            ).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun receiveMsg(bytes: ByteArray) {
        try {
            val unpackedMessageBytes: ByteArray
            val eventMessage: JSONObject
            if (JSONObject(String(bytes)).has("protected")) {
                unpackedMessageBytes =
                    Crypto.unpackMessage(indyWallet, bytes)
                        .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
                val unpackedMessage: JSONObject = JSONObject(String(unpackedMessageBytes))
                eventMessage =
                    JSONObject().put("@type", "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/event")
                        .put("content_type", "application/ssi-agent-wire").put("@id", UUID.randomUUID)
                        .put("message", JSONObject(unpackedMessage.optString("message")))
                        .put("recipient_verkey", unpackedMessage.optString("recipient_verkey"))
                if (unpackedMessage.has("sender_verkey")) {
                    eventMessage.put("sender_verkey", unpackedMessage.optString("sender_verkey"))
                }
            } else {
                unpackedMessageBytes = bytes
                val unpackedMessage: JSONObject = JSONObject(String(unpackedMessageBytes))
                eventMessage =
                    JSONObject().put("@type", "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/event")
                        .put("content_type", "application/ssi-agent-wire").put("@id", UUID.randomUUID)
                        .put("message", unpackedMessage)
            }
            for ( one : Pair<MobileAgentEvents, Listener> in events) {
                one.first.future?.complete(Message(eventMessage))
            }
        } catch (e: java.lang.InterruptedException) {
            e.printStackTrace()
        } catch (e: java.util.concurrent.ExecutionException) {
            e.printStackTrace()
        } catch (e: java.util.concurrent.TimeoutException) {
            e.printStackTrace()
        } catch (e: IndyException) {
            e.printStackTrace()
        }
    }

    override fun close() {
        sender!!.close()
        try {
            indyWallet!!.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun checkIsOpen(): Boolean {
        return indyWallet != null
    }

    override fun subscribe(): Listener {
        val e: MobileAgentEvents = MobileAgentEvents()
        val listener = Listener(e, this)
        events.add(Pair(e, listener))
        return Listener(e, this)
    }


    override fun unsubscribe(listener: Listener?) {
        for (e in events) {
            if (e.second === listener) {
                events.remove(e)
                break
            }
        }
    }

    override fun generateQrCode(value: String?): String? {
        return null
    }


    override fun acquire(
        resources: List<String?>?,
        lockTimeoutSec: Int?,
        enterTimeoutSec: Int?
    ): Pair<Boolean, List<String>> {
        return Pair(false, listOf())
    }

    override fun release() {}


    override fun spawn(my_verkey: String, endpoint: TheirEndpoint): AbstractCoProtocolTransport? {
        return TheirEndpointMobileCoProtocolTransport(this, my_verkey, endpoint)
    }

    override fun spawn(pairwise: Pairwise): AbstractCoProtocolTransport? {
        return PairwiseMobileCoProtocolTransport(this, pairwise)
    }

    override fun spawn(thid: String, pairwise: Pairwise): AbstractCoProtocolTransport? {
        return null
    }

    override fun spawn(thid: String): AbstractCoProtocolTransport? {
        return null
    }

    override fun spawn(thid: String, pairwise: Pairwise, pthid: String): AbstractCoProtocolTransport? {
        return null
    }


    override fun spawn(thid: String, pthid: String): AbstractCoProtocolTransport? {
        return null
    }

    init {
        this.walletConfig = walletConfig
        this.walletCredentials = walletCredentials
        LibIndy.init()
    }
}

