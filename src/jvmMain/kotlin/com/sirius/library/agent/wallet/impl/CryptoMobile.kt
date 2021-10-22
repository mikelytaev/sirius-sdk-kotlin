
package com.sirius.library.agent.wallet.impl

import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.utils.JSONArray
import org.hyperledger.indy.sdk.crypto.Crypto
import org.hyperledger.indy.sdk.crypto.CryptoJSONParameters
import org.hyperledger.indy.sdk.wallet.Wallet

class CryptoMobile(wallet: Wallet) : AbstractCrypto() {
    var wallet: Wallet
    var timeoutSec = 60
    override fun createKey(seed: String?, cryptoType: String?): String? {
        try {
            return Crypto.createKey(wallet, CryptoJSONParameters.CreateKeyJSONParameter(seed, cryptoType).toJson())
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun setKeyMetadata(verkey: String?, metadata: String?) {
        try {
            Crypto.setKeyMetadata(wallet, verkey, metadata).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun getKeyMetadata(verkey: String?): String? {
        try {
            return Crypto.getKeyMetadata(wallet, verkey).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun cryptoSign(signerVk: String?, msg: ByteArray?): ByteArray {
        try {
            return Crypto.cryptoSign(wallet, signerVk, msg).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    override fun cryptoVerify(signerVk: String?, msg: ByteArray?, signature: ByteArray?): Boolean {
        try {
            return Crypto.cryptoVerify(signerVk, msg, signature).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun anonCrypt(recipentVk: String?, msg: ByteArray?): ByteArray {
        try {
            return Crypto.anonCrypt(recipentVk, msg).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    override fun anonDecrypt(recipientVk: String?, encryptedMsg: ByteArray?): ByteArray {
        try {
            return Crypto.anonDecrypt(wallet, recipientVk, encryptedMsg)
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    override fun packMessage(message: Any?, recipentVerkeys: List<String>?, senderVerkey: String?): ByteArray? {
        val jsonArray: JSONArray = JSONArray(recipentVerkeys)
        val messageString = message.toString()
        val byteMessage: ByteArray = messageString.toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        try {
            return Crypto.packMessage(wallet, jsonArray.toString(), senderVerkey, byteMessage)
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }



    override fun unpackMessage(jwe: ByteArray?): String? {
        try {
            return String(
                Crypto.unpackMessage(wallet, jwe).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS),
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

