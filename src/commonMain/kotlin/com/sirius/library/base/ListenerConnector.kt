package com.sirius.library.base

import com.sirius.library.messaging.Message
import com.sirius.library.utils.CompletableFuture
import com.sirius.library.utils.Logger
import com.sirius.library.utils.StringCodec

class ListenerConnector : BaseConnector {
    var log: Logger = Logger.getLogger(ListenerConnector::class.simpleName)
    var defTimeout = 30
    var encoding: String = StringCodec.UTF_8
    var serverAddress: String
    var path: String
    var credentials: ByteArray

    // WebSocket webSocket;
    constructor(
        defTimeout: Int,
        encoding: String,
        serverAddress: String,
        path: String,
        credentials: ByteArray
    ) {
        this.defTimeout = defTimeout
        this.encoding = encoding
        this.serverAddress = serverAddress
        this.path = path
        this.credentials = credentials
        initListener()
    }

    constructor(serverAddress: String, path: String, credentials: ByteArray) {
        this.serverAddress = serverAddress
        this.path = path

        this.credentials = credentials
        initListener()
    }

    fun initListener() {
        val url = "$serverAddress/$path"
        /* try {
            webSocket = new WebSocketFactory()
                    .setVerifyHostname(false)
                    .setConnectionTimeout(defTimeout * 1000)
                    .createSocket(url)
                    .addListener(webSocketListener)
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .setPingInterval(60 * 3 * 1000).
                            addHeader("origin", serverAddress).
                            addHeader("credentials", StringUtils.bytesToString(credentials));

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Throws(Exception::class)
    fun onTextFrame(text: String) {
        onBinaryFrame(text.encodeToByteArray())
    }

    @Throws(Exception::class)
    fun onBinaryFrame(byteArray: ByteArray?) {
        read(byteArray, defTimeout)
    }

    override val isOpen: Boolean
        get() = true

    override fun open() {
        /* if (!isOpen()) {
            try {
                webSocket.connect();
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
        }*/
    }

    override fun close() {
        /*if (isOpen()) {
            webSocket.disconnect();
        }*/
    }

    var readFuture: CompletableFuture<ByteArray?> =
        CompletableFuture<ByteArray?>()

    override fun read(): CompletableFuture<ByteArray?> {
        readFuture = CompletableFuture<ByteArray?>()
        return readFuture
    }

    private fun read(byteArray: ByteArray?, timeout: Int): ByteArray? {
        if (byteArray != null) {
           // readFuture.complete(byteArray)
            return byteArray
        }
        return null
        /*     try:
        msg = await self._ws.receive(timeout=timeout)
        except asyncio.TimeoutError as e:
        raise SiriusTimeoutIO() from e
        if msg.type in [aiohttp.WSMsgType.CLOSE, aiohttp.WSMsgType.CLOSING, aiohttp.WSMsgType.CLOSED]:
        raise SiriusConnectionClosed()
        elif msg.type == aiohttp.WSMsgType.TEXT:
        return msg.data.encode(self.ENC)
        elif msg.type == aiohttp.WSMsgType.BINARY:
        return msg.data
        elif msg.type == aiohttp.WSMsgType.ERROR:
        raise SiriusIOError()*/
    }

    override fun write(data: ByteArray?): Boolean {
       /* val httpclient: CloseableHttpClient = HttpClients.createDefault()
        val httpPost = HttpPost(serverAddress)
        val nvps: MutableList<NameValuePair> = ArrayList<NameValuePair>()
        nvps.add(BasicNameValuePair("name", data.decodeToString()))
        //nvps.add(new BasicNameValuePair("password", "secret"));
        try {
            httpPost.setEntity(UrlEncodedFormEntity(nvps))
        } catch (e: java.io.UnsupportedEncodingException) {
            e.printStackTrace()
        }
        var response2: CloseableHttpResponse? = null
        try {
            response2 = httpclient.execute(httpPost)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        }
        try {
            println(response2.getStatusLine())
            val entity2: HttpEntity = response2.getEntity()
            // do something useful with the response body
            // and ensure it is fully consumed
            try {
                EntityUtils.consume(entity2)
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
        } finally {
            try {
                response2.close()
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
        }*/
        return true
    }

    fun write(message: Message): Boolean {
      /*  val payload: String = message.serialize()
        val httpclient: CloseableHttpClient = HttpClients.createDefault()
        val httpPost = HttpPost(serverAddress)
        val nvps: MutableList<NameValuePair> = ArrayList<NameValuePair>()
        nvps.add(BasicNameValuePair("name", payload))
        //nvps.add(new BasicNameValuePair("password", "secret"));
        try {
            httpPost.setEntity(UrlEncodedFormEntity(nvps))
        } catch (e: java.io.UnsupportedEncodingException) {
            e.printStackTrace()
        }
        var response2: CloseableHttpResponse? = null
        try {
            response2 = httpclient.execute(httpPost)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        }
        try {
            println(response2.getStatusLine())
            val entity2: HttpEntity = response2.getEntity()
            // do something useful with the response body
            // and ensure it is fully consumed
            try {
                EntityUtils.consume(entity2)
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
        } finally {
            try {
                response2.close()
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
        }*/
        return true
    }
}


/*  WebSocket ws;
    private static final int TIMEOUT = 15000;

    private void connect() {

    }*/

/*
    DEF_TIMEOUT = 30.0
            ENC = 'utf-8'

            def __init__(
            self, server_address: str, path: str, credentials: bytes,
            timeout: float=DEF_TIMEOUT, loop: asyncio.AbstractEventLoop=None
            ):
            self.__session = aiohttp.ClientSession(
            loop=loop,
            timeout=aiohttp.ClientTimeout(total=timeout),
            headers={
            'origin': server_address,
            'credentials': credentials.decode('ascii')
            }
            )
            self._url = urljoin(server_address, path)
            self._ws = None

@property
    def is_open(self):
            return self._ws is not None

            async def open(self):
        if not self.is_open:
        self._ws = await self.__session.ws_connect(url=self._url)

        async def close(self):
        if not self.is_open:
        await self._ws.close()
        self._ws = None

        async def read(self, timeout: int=None) -> bytes:
        try:
        msg = await self._ws.receive(timeout=timeout)
        except asyncio.TimeoutError as e:
        raise SiriusTimeoutIO() from e
        if msg.type in [aiohttp.WSMsgType.CLOSE, aiohttp.WSMsgType.CLOSING, aiohttp.WSMsgType.CLOSED]:
        raise SiriusConnectionClosed()
        elif msg.type == aiohttp.WSMsgType.TEXT:
        return msg.data.encode(self.ENC)
        elif msg.type == aiohttp.WSMsgType.BINARY:
        return msg.data
        elif msg.type == aiohttp.WSMsgType.ERROR:
        raise SiriusIOError()

        async def write(self, message: Union[Message, bytes]) -> bool:
        if isinstance(message, Message):
        payload = message.serialize().encode(self.ENC)
        else:
        payload = message
        await self._ws.send_bytes(payload)
        return True*/
