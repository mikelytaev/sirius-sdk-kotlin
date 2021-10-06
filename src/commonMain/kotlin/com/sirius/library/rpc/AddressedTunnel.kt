package com.sirius.library.rpc

import com.sirius.library.base.ReadOnlyChannel
import com.sirius.library.base.WriteOnlyChannel
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidPayloadStructure
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger

/**
 * Transport abstraction that help build tunnels (p2p pairwise relationships) over channel layer.
 */
class AddressedTunnel(var address: String, input: ReadOnlyChannel, output: WriteOnlyChannel, p2p: P2PConnection) {
    var log: Logger = Logger.getLogger(AddressedTunnel::class.simpleName)
    //var ENC: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8
    var input: ReadOnlyChannel
    var output: WriteOnlyChannel
    var p2p: P2PConnection
    var context: Context

    /**
     * Read message.
     *
     *
     * Tunnel allows to receive non-encrypted messages, high-level logic may control message encryption flag
     * via context.encrypted field
     *
     * @param timeout timeout in seconds
     * @return received packet
     */
    @Throws(SiriusInvalidPayloadStructure::class)
    fun receive(timeout: Int): Message? {
        var payload = ByteArray(0)
        payload = try {
            input.read().get(timeout, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.InterruptedException) {
            e.printStackTrace()
            return null
        } catch (e: java.util.concurrent.ExecutionException) {
            e.printStackTrace()
            return null
        } catch (e: java.util.concurrent.TimeoutException) {
            e.printStackTrace()
            return null
        }
        return try {
            val payloadString = String(payload, java.nio.charset.StandardCharsets.US_ASCII)
            val jsonObject = JSONObject(payloadString)
            if (jsonObject.has("protected")) {
                val unpacked: String? = p2p.unpack(String(payload, java.nio.charset.StandardCharsets.US_ASCII))
                //log.log(Level.INFO, "Received protected message. Unpacked: " + unpacked);
                context.isEncrypted = true
                unpacked?.let {
                    Message(unpacked)
                }
            } else {
                context.isEncrypted = false
                //log.log(Level.INFO, "Received message: " + payload);
                Message(String(payload, java.nio.charset.StandardCharsets.US_ASCII))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw SiriusInvalidPayloadStructure("Invalid packed message")
        }
    }

    fun post(message: Message): Boolean {
        return post(message, true)
    }

    /**
     * Write message
     *
     * @param message message to send
     * @param encrypt do encryption
     */
    fun post(message: Message, encrypt: Boolean): Boolean {
        var payload: String? = null
        payload = if (encrypt) {
            p2p.pack(message.serialize()?:"")
        } else {
            message.serialize()
        }
        return output.write(payload.toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
    }

    /**
     * Tunnel instance context
     */
    internal class Context {
        var isEncrypted = false
    }

    /**
     * @param address communication address of transport environment on server-side
     * @param input   channel of input stream
     * @param output  channel of output stream
     * @param p2p     pairwise connection that configured and prepared outside
     */
    init {
        this.input = input
        this.output = output
        this.p2p = p2p
        context = Context()
    }
}

