/*
package com.sirius.library.agent.wallet.impl

class CryptoMobile(wallet: Wallet) : AbstractCrypto() {
    var wallet: Wallet
    var timeoutSec = 60
    fun createKey(seed: String?, cryptoType: String?): String? {
        try {
            return Crypto.createKey(wallet, CreateKeyJSONParameter(seed, cryptoType).toJson())
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun setKeyMetadata(verkey: String?, metadata: String?) {
        try {
            Crypto.setKeyMetadata(wallet, verkey, metadata).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getKeyMetadata(verkey: String?): String? {
        try {
            return Crypto.getKeyMetadata(wallet, verkey).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun cryptoSign(signerVk: String?, msg: ByteArray?): ByteArray {
        try {
            return Crypto.cryptoSign(wallet, signerVk, msg).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    fun cryptoVerify(signerVk: String?, msg: ByteArray?, signature: ByteArray?): Boolean {
        try {
            return Crypto.cryptoVerify(signerVk, msg, signature).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun anonCrypt(recipentVk: String?, msg: ByteArray?): ByteArray {
        try {
            return Crypto.anonCrypt(recipentVk, msg).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    fun anonDecrypt(recipientVk: String?, encryptedMsg: ByteArray?): ByteArray {
        try {
            return Crypto.anonDecrypt(wallet, recipientVk, encryptedMsg)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    fun packMessage(message: Any, recipentVerkeys: List<String?>, senderVerkey: String?): ByteArray {
        val jsonArray: JSONArray = JSONArray()
        for (key in recipentVerkeys) {
            jsonArray.put(key)
        }
        val messageString = message.toString()
        val byteMessage: ByteArray = messageString.toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        try {
            return Crypto.packMessage(wallet, jsonArray.toString(), senderVerkey, byteMessage)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    fun unpackMessage(jwe: ByteArray?): String? {
        try {
            return String(
                Crypto.unpackMessage(wallet, jwe).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS),
                java.nio.charset.StandardCharsets.UTF_8
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    init {
        this.wallet = wallet
    }
}
*/
