package com.sirius.library.naclJava

import com.ionspin.kotlin.crypto.aead.AuthenticatedEncryptionWithAssociatedData
import com.ionspin.kotlin.crypto.box.Box
import com.ionspin.kotlin.crypto.util.LibsodiumUtil
import com.sirius.library.utils.Key
import com.sirius.library.utils.KeyPair
import com.sirius.library.utils.StringUtils
import com.sirius.library.utils.StringUtils.US_ASCII
import com.sirius.library.utils.StringUtils.UTF_8
import com.sodium.LibSodium
import com.sodium.SodiumException

class CryptoAead {

    fun decrypt(
        cipher: ByteArray,
        additionalData: ByteArray,
        nPub: ByteArray,
        k: Key,
        method: String
    ): ByteArray? {
        return this.decrypt(cipher, additionalData, null as ByteArray?, nPub, k, method)
    }

    fun decrypt(
        cipher: ByteArray,
        additionalData: ByteArray,
        nSec: ByteArray?,
        nPub: ByteArray,
        k: Key,
        method: String
    ): ByteArray {
        //byte[] cipherBytes = cipher.getBytes(StandardCharsets.US_ASCII);
        /*  val additionalDataBytes = additionalData ?: ByteArray(0)
          val additionalBytesLen = if (additionalData == null) 0L else additionalDataBytes.size.toLong()
          val keyBytes: ByteArray = k.getAsBytes()
          val messageBytes: ByteArray
          if (method.equals(com.goterl.lazycode.lazysodium.interfaces.AEAD.Method.CHACHA20_POLY1305_IETF)) {
              messageBytes = ByteArray(cipher.size - 16)
              val mlen = LongArray(messageBytes.size)
              LibSodium.getInstance().getNativeAaed().cryptoAeadChaCha20Poly1305IetfDecrypt(
                  messageBytes,
                  null,
                  nSec,
                  cipher,
                  cipher.size.toLong(),
                  additionalDataBytes,
                  additionalBytesLen,
                  nPub,
                  keyBytes
              )
              return messageBytes
          }*/
        return AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305IetfDecrypt(cipher.toUByteArray(),additionalData.toUByteArray(),nPub.toUByteArray(),k.asBytes.toUByteArray()).toByteArray()
      //  return null
    }

    @Throws(SodiumException::class)
    fun cryptoBoxSeal(messageBytes: ByteArray, publicKey: Key): ByteArray? {
        val keyBytes: ByteArray = publicKey.asBytes
        return LibSodium.getInstance()
            .cryptoBoxSeal(messageBytes, keyBytes)
        /* val cipher = ByteArray(48 + messageBytes.size)
         return if (!LibSodium.getInstance().getNativeBox()
                 .cryptoBoxSeal(cipher, messageBytes, messageBytes.size.toLong(), keyBytes)
         ) {
             throw SodiumException("Could not encrypt message.")
         } else {
             cipher
         }*/
    }


    fun cryptoBoxSeal(messageString: String, publicKey: Key): ByteArray? {
        val keyBytes: ByteArray = publicKey.asBytes
        val message: ByteArray = StringUtils.stringToBytes(messageString, US_ASCII)
        // val _mlen = message.size
        // val _clen = 48 + _mlen
        //  val ciphertext = ByteArray(_clen)
        return LibSodium.getInstance()
            .cryptoBoxSeal(message, keyBytes)
    }

    fun encrypt(
        m: String,
        additionalData: String?,
        nPub: ByteArray,
        k: Key,
        method:String
    ): ByteArray? {
        return this.encrypt(m, additionalData, null as ByteArray?, nPub, k, method)
    }

    fun encrypt(
        m: String,
        additionalData: String?,
        nSec: ByteArray?,
        nPub: ByteArray,
        k: Key,
        method: String
    ): ByteArray? {
        val messageBytes: ByteArray = StringUtils.stringToBytes(m,US_ASCII)
        val additionalDataBytes =
            if (additionalData == null) ByteArray(0) else StringUtils.stringToBytes(additionalData,US_ASCII)
        val additionalBytesLen = if (additionalData == null) 0L else additionalDataBytes.size.toLong()
        val keyBytes: ByteArray = k.asBytes
        val cipherBytes1: ByteArray
        if (method == "CHACHA20_POLY1305_IETF") {
            val cipherBytes = ByteArray(messageBytes.size + 16)
            cipherBytes1 =   LibSodium.getInstance().cryptoAeadChaCha20Poly1305IetfEncrypt(
                cipherBytes, null as LongArray?, messageBytes,
                messageBytes.size.toLong(), additionalDataBytes, additionalBytesLen, nSec, nPub, keyBytes
            )
            println("cipherBytes="+cipherBytes1)
            println("cipherBytes="+StringUtils.bytesToString(cipherBytes1, US_ASCII))
            println("cipherBytes="+StringUtils.bytesToString(cipherBytes1, UTF_8))
            return cipherBytes1
        }
        return null
    }


    @Throws(SodiumException::class)
    fun cryptoBox(messageBytes: ByteArray, nonce: ByteArray, keyPair: KeyPair): ByteArray? {
       /* val bObj: java.io.ByteArrayOutputStream = java.io.ByteArrayOutputStream()
        bObj.reset()
        val cipherBytesPadding = ByteArray(32)
        for (cipherBytesPadding1 in cipherBytesPadding) {
            bObj.write(cipherBytesPadding1.toInt())
        }
        for (mesByte in messageBytes) {
            bObj.write(mesByte.toInt())
        }*/

         val cipherBytes = ByteArray(32 + messageBytes.size)
        val messageBytesPadded: ByteArray =   LibsodiumUtil.pad(messageBytes.toUByteArray(), 32).toByteArray()
        val res: ByteArray = LibSodium.getInstance().cryptoBox(
            cipherBytes,
            messageBytes,
            messageBytes.size.toLong(),
            nonce,
            keyPair.getPublicKey().asBytes,
            keyPair.getSecretKey().asBytes
        )
        return   res.copyOfRange(16, res.size)
        /*return if (!res) {
            throw SodiumException("Could not encrypt your message.")
        } else {*/
            /*val bObj2: java.io.ByteArrayOutputStream = java.io.ByteArrayOutputStream()
            bObj2.reset()
            var i = 0
            for (mesByte in cipherBytes) {
                if (i <= 15) {
                    i++
                    continue
                }
                bObj2.write(mesByte.toInt())
            }
            bObj2.toByteArray()*/

            //return new String(cipherBytes);
        //}
    }


    fun cryptoBoxOpen(cipherText: ByteArray, nonce: ByteArray, keyPair: KeyPair): ByteArray {
        return Box.openEasy(cipherText.toUByteArray(),nonce.toUByteArray(),keyPair.getPublicKey().asBytes.toUByteArray(),keyPair.getSecretKey().asBytes.toUByteArray()).toByteArray()
        /* val message = ByteArray(cipherText.size + 16)
         val cipherBytesPadding = ByteArray(16)
         val bObj: java.io.ByteArrayOutputStream = java.io.ByteArrayOutputStream()
         bObj.reset()
         for (cipherBytesPadding1 in cipherBytesPadding) {
             bObj.write(cipherBytesPadding1.toInt())
         }
         for (mesByte in cipherText) {
             bObj.write(mesByte.toInt())
         }
         val padded: ByteArray = bObj.toByteArray()
         val res: Boolean = LibSodium.getInstance().getNativeBox().cryptoBoxOpen(
             message,
             padded,
             padded.size.toLong(),
             nonce,
             keyPair.getPublicKey().getAsBytes(),
             keyPair.getSecretKey().getAsBytes()
         )
         return if (!res) {
             throw SodiumException("Could not decrypt your message.")
         } else {
             // new byte[32]{message}
             val bObj2: java.io.ByteArrayOutputStream = java.io.ByteArrayOutputStream()
             bObj2.reset()
             var i = 0
             for (mesByte in message) {
                 if (i <= 31) {
                     i++
                     continue
                 }
                 bObj2.write(mesByte.toInt())
             }
             bObj2.toByteArray()
         }*/
    }


    fun cryptoBoxSealOpen(cipherString: String, keyPair: KeyPair): String? {
        /*  val cipherText: ByteArray = cipherString.toByteArray(java.nio.charset.StandardCharsets.US_ASCII)
          val _clen = cipherText.size
          val _mlen = _clen - 48
          val plaintext = ByteArray(_mlen)
          val res: Boolean = LibSodium.getInstance().getNativeBox().cryptoBoxSealOpen(
              plaintext,
              cipherText,
              _clen.toLong(),
              keyPair.getPublicKey().getAsBytes(),
              keyPair.getSecretKey().getAsBytes()
          )
          return if (!res) {
              throw SodiumException("Could not decrypt your message.")
          } else {
              String(plaintext, java.nio.charset.StandardCharsets.US_ASCII)
          }*/
        return null
    }


    fun cryptoBoxSealOpen(cipherText: ByteArray, keyPair: KeyPair): ByteArray {
        println("open cipherText="+StringUtils.bytesToString(cipherText, US_ASCII))
        println("open getPublicKey="+StringUtils.bytesToString(keyPair.getPublicKey().asBytes, US_ASCII))
        println("open getSecretKey="+StringUtils.bytesToString(keyPair.getSecretKey().asBytes, US_ASCII))
        return Box.sealOpen(cipherText.toUByteArray(),keyPair.getPublicKey().asBytes.toUByteArray(),keyPair.getSecretKey().asBytes.toUByteArray()).toByteArray()
        // byte[] cipherText = cipherString.getBytes(StandardCharsets.US_ASCII);
        /* val _clen = cipherText.size
         val _mlen = _clen - 48
         val plaintext = ByteArray(_mlen)
         val res: Boolean = LibSodium.getInstance().getNativeBox().cryptoBoxSealOpen(
             plaintext,
             cipherText,
             _clen.toLong(),
             keyPair.getPublicKey().getAsBytes(),
             keyPair.getSecretKey().getAsBytes()
         )
         return if (!res) {
             throw SodiumException("Could not decrypt your message.")
         } else {
             plaintext
         }*/
      //  return null
    }
}


