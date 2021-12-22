package com.sirius.library.base

import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.Logger
import com.sirius.library.utils.StringUtils

actual class WebSocketConnector actual constructor() : BaseConnector() {

    var log: Logger = Logger.getLogger(WebSocketConnector::class.simpleName)
    var defTimeout = 30
    var encoding: StringUtils.CODEC = StringUtils.CODEC.UTF_8
    lateinit var serverAddress: String
    lateinit var path: String
    var credentials: ByteArray? = null
    //var webSocket: WebSocket? = null
  //  var readCallback: java.util.function.Function<ByteArray, java.lang.Void>? = null

    actual constructor(
        defTimeout: Int,
        encoding: StringUtils.CODEC,
        serverAddress: String,
        path: String,
        credentials: ByteArray?
    ) : this() {
        this.defTimeout = defTimeout
        this.encoding = encoding
        this.serverAddress = serverAddress
        this.path = path
        this.credentials = credentials
        initWebSocket()
    }



    actual constructor(serverAddress: String, path: String, credentials: ByteArray?) : this() {
        this.serverAddress = serverAddress
        this.path = path
        this.credentials = credentials
        initWebSocket()
    }

    fun initWebSocket() {
        TODO("Not yet implemented")
    }

    actual override fun open() {
        TODO("Not yet implemented")
    }

    actual override fun close() {
        TODO("Not yet implemented")
    }

    actual override val isOpen: Boolean
        get() = TODO("Not yet implemented")

    var readFuture: CompletableFutureKotlin<ByteArray?> =
        CompletableFutureKotlin()

    actual override fun read(): CompletableFutureKotlin<ByteArray?> {
        readFuture = CompletableFutureKotlin()
        return readFuture
    }

    actual override fun write(data: ByteArray?): Boolean {

        val payload =  StringUtils.bytesToString(data ?: ByteArray(0),StringUtils.CODEC.US_ASCII)
        log.log(Logger.Level.INFO, "Sending binary data"+payload);
        TODO("Not yet implemented")
        return false
    }

    actual suspend fun openConnector() {
    }

    actual suspend fun readBytes(): ByteArray? {
        return null
    }


}