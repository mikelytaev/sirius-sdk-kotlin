package com.sirius.library.base

import com.sirius.library.utils.CompletableFutureKotlin
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

expect class WebSocketConnector() :BaseConnector  {



    constructor(
        defTimeout: Int,
        encoding: String,
        serverAddress: String,
        path: String,
        credentials: ByteArray?
    )

    constructor(serverAddress: String, path: String, credentials: ByteArray?)

    /* var log: Logger = Logger.getLogger(WebSocketConnector::class.simpleName)
    var defTimeout = 30
    var encoding: String = StringCodec.UTF_8
    var serverAddress: String
    var path: String
    var credentials: ByteArray? = null
    var webSocket: WebSocket? = null
    var readCallback: java.util.function.Function<ByteArray, java.lang.Void>? = null

    constructor(
        defTimeout: Int,
        encoding: String,
        serverAddress: String,
        path: String,
        credentials: ByteArray?
    ) {
        this.defTimeout = defTimeout
        this.encoding = encoding
        this.serverAddress = serverAddress
        this.path = path
        this.credentials = credentials
        initWebSocket()
    }

    constructor(serverAddress: String, path: String, credentials: ByteArray?) {
        this.serverAddress = serverAddress
        this.path = path
        this.credentials = credentials
        initWebSocket()
    }

    var webSocketListener: WebSocketListener = object : WebSocketListener() {
        @Throws(Exception::class)
        fun onStateChanged(webSocket: WebSocket?, webSocketState: WebSocketState?) {
        }

        @Throws(java.lang.Exception::class)
        fun onConnected(webSocket: WebSocket?, map: Map<String?, List<String?>?>?) {
            //log.log(Level.INFO, "Connected");
        }

        @Throws(java.lang.Exception::class)
        fun onConnectError(webSocket: WebSocket?, e: WebSocketException?) {
            log.log(java.util.logging.Level.WARNING, "Connect error")
        }

        @Throws(java.lang.Exception::class)
        fun onDisconnected(
            webSocket: WebSocket?,
            webSocketFrame: WebSocketFrame?,
            webSocketFrame1: WebSocketFrame?,
            b: Boolean
        ) {
            //log.log(Level.INFO, "Disconnected");
        }

        @Throws(java.lang.Exception::class)
        fun onFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onContinuationFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onTextFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
            read(webSocketFrame, null, defTimeout)
        }

        @Throws(java.lang.Exception::class)
        fun onBinaryFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
            read(webSocketFrame, null, defTimeout)
        }

        @Throws(java.lang.Exception::class)
        fun onCloseFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onPingFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onPongFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onTextMessage(webSocket: WebSocket?, s: String?) {
        }

        @Throws(java.lang.Exception::class)
        fun onTextMessage(webSocket: WebSocket?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        fun onBinaryMessage(webSocket: WebSocket?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        fun onSendingFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onFrameSent(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onFrameUnsent(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onThreadCreated(webSocket: WebSocket?, threadType: ThreadType?, thread: java.lang.Thread?) {
        }

        @Throws(java.lang.Exception::class)
        fun onThreadStarted(webSocket: WebSocket?, threadType: ThreadType?, thread: java.lang.Thread?) {
        }

        @Throws(java.lang.Exception::class)
        fun onThreadStopping(webSocket: WebSocket?, threadType: ThreadType?, thread: java.lang.Thread?) {
        }

        @Throws(java.lang.Exception::class)
        fun onError(webSocket: WebSocket?, e: WebSocketException?) {
        }

        @Throws(java.lang.Exception::class)
        fun onFrameError(webSocket: WebSocket?, e: WebSocketException?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onMessageError(webSocket: WebSocket?, e: WebSocketException?, list: List<WebSocketFrame?>?) {
        }

        @Throws(java.lang.Exception::class)
        fun onMessageDecompressionError(webSocket: WebSocket?, e: WebSocketException?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        fun onTextMessageError(webSocket: WebSocket?, e: WebSocketException?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        fun onSendError(webSocket: WebSocket?, e: WebSocketException?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        fun onUnexpectedError(webSocket: WebSocket?, e: WebSocketException?) {
        }

        @Throws(java.lang.Exception::class)
        fun handleCallbackError(webSocket: WebSocket?, throwable: Throwable?) {
        }

        @Throws(java.lang.Exception::class)
        fun onSendingHandshake(webSocket: WebSocket?, s: String?, list: List<Array<String?>?>?) {
        }
    }

    fun initWebSocket() {
        var url = "$serverAddress/$path"
        while (url.endsWith("/")) url = url.substring(0, url.length - 1)
        try {
            webSocket = WebSocketFactory()
                .setVerifyHostname(false)
                .setConnectionTimeout(defTimeout * 1000)
                .createSocket(url)
                .addListener(webSocketListener)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .setPingInterval(60 * 3 * 1000).addHeader("origin", serverAddress)
            if (credentials != null) {
                webSocket.addHeader("credentials", StringUtils.bytesToString(credentials))
            }
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        }
    }

    override val isOpen: Boolean
        get() = if (webSocket != null) {
            webSocket.isOpen()
        } else false

    override fun open() {
        if (!isOpen) {
            try {
                webSocket.connect()
            } catch (e: WebSocketException) {
                e.printStackTrace()
            }
        }
    }

    override fun close() {
        if (isOpen) {
            webSocket.disconnect()
        }
    }

    var readFuture: java.util.concurrent.CompletableFuture<ByteArray> =
        java.util.concurrent.CompletableFuture<ByteArray>()

    override fun read(): java.util.concurrent.CompletableFuture<ByteArray> {
        readFuture = java.util.concurrent.CompletableFuture<ByteArray>()
        return readFuture
    }

    private fun read(frame: WebSocketFrame?, exception: WebSocketException?, timeout: Int): ByteArray? {
        if (frame != null) {
            readFuture.complete(frame.getPayload())
            if (readCallback != null) readCallback.apply(frame.getPayload())
            return frame.getPayload()
        }
        return null
    }

    override fun write(data: ByteArray?): Boolean {
        //log.log(Level.INFO, "Sending binary data");
        webSocket.sendBinary(data)
        return true
    }

    fun write(message: Message): Boolean {
        val payload: String = message.serialize() ?:""
        //log.log(Level.INFO, "Sending message");
        webSocket.sendText(payload)
        return true
    }*/
    override fun open()

     override fun close()

     override val isOpen: Boolean


    override fun read(): CompletableFutureKotlin<ByteArray?>

     override fun write(data: ByteArray?): Boolean

    suspend fun openConnector()

    suspend fun readBytes(): ByteArray?
}
