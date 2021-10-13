package com.sirius.library.base

import com.neovisionaries.ws.client.*
import com.sirius.library.messaging.Message
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.Logger
import com.sirius.library.utils.StringCodec
import com.sirius.library.utils.StringUtils
import io.ktor.util.date.*
import kotlinx.coroutines.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

actual class WebSocketConnector actual constructor() : BaseConnector() {
     var log: Logger = Logger.getLogger(WebSocketConnector::class.simpleName)
    var defTimeout = 30
    var encoding: String = StringCodec.UTF_8
    lateinit var serverAddress: String
    lateinit var path: String
    var credentials: ByteArray? = null
    var webSocket: WebSocket? = null
    var readCallback: java.util.function.Function<ByteArray, java.lang.Void>? = null

    actual constructor(
        defTimeout: Int,
        encoding: String,
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

    var webSocketListener: WebSocketListener = object : WebSocketListener {
        @Throws(Exception::class)
        override  fun onStateChanged(webSocket: WebSocket?, webSocketState: WebSocketState?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onConnected(webSocket: WebSocket?, map: Map<String?, List<String?>?>?) {
            log.log(Logger.Level.INFO, "Connected");
        }

        @Throws(java.lang.Exception::class)
        override fun onConnectError(webSocket: WebSocket?, e: WebSocketException?) {
            log.log(Logger.Level.WARNING, "Connect error")
        }

        @Throws(java.lang.Exception::class)
        override fun onDisconnected(
            webSocket: WebSocket?,
            webSocketFrame: WebSocketFrame?,
            webSocketFrame1: WebSocketFrame?,
            b: Boolean
        ) {
            //log.log(Level.INFO, "Disconnected");
        }

        @Throws(java.lang.Exception::class)
        override fun onFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onContinuationFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onTextFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
            read(webSocketFrame, null, defTimeout)
        }

        @Throws(java.lang.Exception::class)
        override fun onBinaryFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
            read(webSocketFrame, null, defTimeout)
        }

        @Throws(java.lang.Exception::class)
        override fun onCloseFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onPingFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onPongFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onTextMessage(webSocket: WebSocket?, s: String?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onTextMessage(webSocket: WebSocket?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onBinaryMessage(webSocket: WebSocket?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onSendingFrame(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onFrameSent(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onFrameUnsent(webSocket: WebSocket?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onThreadCreated(webSocket: WebSocket?, threadType: ThreadType?, thread: java.lang.Thread?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onThreadStarted(webSocket: WebSocket?, threadType: ThreadType?, thread: java.lang.Thread?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onThreadStopping(webSocket: WebSocket?, threadType: ThreadType?, thread: java.lang.Thread?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onError(webSocket: WebSocket?, e: WebSocketException?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onFrameError(webSocket: WebSocket?, e: WebSocketException?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onMessageError(webSocket: WebSocket?, e: WebSocketException?, list: List<WebSocketFrame?>?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onMessageDecompressionError(webSocket: WebSocket?, e: WebSocketException?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onTextMessageError(webSocket: WebSocket?, e: WebSocketException?, bytes: ByteArray?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onSendError(webSocket: WebSocket?, e: WebSocketException?, webSocketFrame: WebSocketFrame?) {
        }

        @Throws(java.lang.Exception::class)
        override fun onUnexpectedError(webSocket: WebSocket?, e: WebSocketException?) {
        }

        @Throws(java.lang.Exception::class)
        override fun handleCallbackError(webSocket: WebSocket?, throwable: Throwable?) {
        }

        @Throws(java.lang.Exception::class)
        override  fun onSendingHandshake(webSocket: WebSocket?, s: String?, list: List<Array<String?>?>?) {
        }
    }

    fun initWebSocket() {
        var url = "$serverAddress/$path"
        while (url.endsWith("/")) url = url.substring(0, url.length - 1)
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return  arrayOf()
                }

            }
        )
        // Install the all-trusting trust manager
        // Install the all-trusting trust manager
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()
            /*okHttpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            okHttpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });*/
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        try {
            webSocket = WebSocketFactory()
                .setVerifyHostname(false)
                .setSSLContext(sslContext)
                .setConnectionTimeout(defTimeout * 1000)
                .createSocket(url)
                .addListener(webSocketListener)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .setPingInterval(60 * 3 * 1000).addHeader("origin", serverAddress)
            if (credentials != null) {
                webSocket!!.addHeader("credentials", credentials!!.decodeToString())
            }
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        }
    }

    actual override val isOpen: Boolean
        get() = if (webSocket != null) {
            webSocket!!.isOpen()
        } else false

    actual override fun open() {
        if (!isOpen) {
            try {
                webSocket!!.connect()
            } catch (e: WebSocketException) {
                e.printStackTrace()
            }
        }
    }

    actual override fun close() {
        if (isOpen) {
            webSocket!!.disconnect()
        }
    }

    var readFuture: CompleteFuture<ByteArray?> =
        CompleteFuture()

    actual  override fun read(): CompleteFuture<ByteArray?> {
        readFuture = CompleteFuture()
        return readFuture
        //readFuture = ByteArray
      //  return  //readFuture.get(defTimeout.toLong(),TimeUnit.SECONDS)
    }


  //  var readed :ByteArray? = null
    private fun read(frame: WebSocketFrame?, exception: WebSocketException?, timeout: Int): ByteArray? {
        if (frame != null) {
          //  readed = frame.getPayload()

            log.log(Logger.Level.INFO, "REaded binary data"+frame.getPayload()?.decodeToString());
            readFuture.complete(frame.getPayload())
            if (readCallback != null) readCallback!!.apply(frame.getPayload())
            return frame.getPayload()
        }
        return null
    }

    actual override fun write(data: ByteArray?): Boolean {
        log.log(Logger.Level.INFO, "Sending binary data"+data?.decodeToString());
        webSocket!!.sendBinary(data)
        return true
    }

    fun write(message: Message): Boolean {
        val payload: String = message.serialize() ?:""
        log.log(Logger.Level.INFO, "Sending message="+payload);
        webSocket!!.sendText(payload)
        return true
    }


    actual suspend fun openConnector() {

    }

    actual suspend fun readBytes(): ByteArray? {
        return null
    }



}
