package com.sirius.library.agent.wallet.impl.mobile

import Indy.IndyCrypto
import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.agent.wallet.results.CryptoJSONParameters
import com.sirius.library.agent.wallet.results.ErrorHandler
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.StringUtils
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.darwin.Byte
import platform.posix.exception
import platform.posix.memcpy


actual  class CryptoMobile actual constructor(val wallet: LocalWallet) : AbstractCrypto() {

    actual var timeoutSec = 60L
    override fun createKey(seed: String?, cryptoType: String?): String? {
        try{
            val keyJson = CryptoJSONParameters.CreateKeyJSONParameter(seed, cryptoType)
            val future = CompletableFutureKotlin<String?>()
            IndyCrypto.createKey(keyJson.toString(), wallet.walletHandle){
                    error: NSError?, data : String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        }catch (e : Exception){
            e.printStackTrace()
        }
        return null
    }

    override fun setKeyMetadata(verkey: String?, metadata: String?) {

        try{

            val future = CompletableFutureKotlin<Boolean>()
            IndyCrypto.setMetadata(metadata,verkey, wallet.walletHandle){
                    error: NSError?->
                ErrorHandler(error).handleError()
                future.complete(true)
            }
             future.get()
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun getKeyMetadata(verkey: String?): String? {
        try{
            val future = CompletableFutureKotlin<String?>()
            IndyCrypto.getMetadataForKey(verkey,  wallet.walletHandle){
                    error: NSError?, data : String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        }catch (e : Exception){
            e.printStackTrace()
        }
        return null

    }

    override fun cryptoSign(signerVk: String?, msg: ByteArray?): ByteArray {
        try{
            val future = CompletableFutureKotlin<ByteArray?>()
            msg?.let {
                val messData = memScoped{
                    NSData.create(bytes = allocArrayOf(msg), length = msg.size.toULong())
                }
                IndyCrypto.signMessage(messData, signerVk, wallet.walletHandle){
                        error: NSError?, data : NSData? ->
                    ErrorHandler(error).handleError()
                    val d = memScoped{data}
                    var  byteArray = ByteArray(0)
                    d?.let {
                        byteArray =   ByteArray(d.length.toInt()).apply {
                            usePinned {
                                memcpy(it.addressOf(0), d.bytes,d.length)
                            }
                        }
                    }
                    future.complete(byteArray)
                }
            }

            return future.get() ?: ByteArray(0)
        }catch (e : Exception){
            e.printStackTrace()
        }
        return ByteArray(0)

    }

    override fun cryptoVerify(signerVk: String?, msg: ByteArray?, signature: ByteArray?): Boolean {

        try{
            if(msg == null || signature == null){
                return false
            }
            val messData = memScoped{
                NSData.create(bytes = allocArrayOf(msg), length = msg.size.toULong())
            }

            val signatureData = memScoped{
                NSData.create(bytes = allocArrayOf(signature), length = signature.size.toULong())
            }

            val future = CompletableFutureKotlin<Boolean>()
            IndyCrypto.verifySignature(signatureData,messData,signerVk){
                    error: NSError?, data : Boolean? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get() ?: false
        }catch (e :  Exception){
            e.printStackTrace()
        }
        return false

    }

    override fun anonCrypt(recipentVk: String?, msg: ByteArray?): ByteArray {
        try{
            if(msg == null ){
                return ByteArray(0)
            }
            val messData = memScoped{
                NSData.create(bytes = allocArrayOf(msg), length = msg.size.toULong())
            }
            val future = CompletableFutureKotlin<ByteArray?>()
            IndyCrypto.anonCrypt(messData,recipentVk){
                    error: NSError?, data : NSData? ->
                ErrorHandler(error).handleError()
                val d = memScoped{data}
                var  byteArray = ByteArray(0)
                d?.let {
                    byteArray =   ByteArray(d.length.toInt()).apply {
                        usePinned {
                            memcpy(it.addressOf(0), d.bytes,d.length)
                        }
                    }
                }
                future.complete(byteArray)
            }
            return future.get() ?: ByteArray(0)
        }catch (e :  Exception){
            e.printStackTrace()
        }
        return ByteArray(0)

    }

    override fun anonDecrypt(recipientVk: String?, encryptedMsg: ByteArray?): ByteArray {
        try{
            if(encryptedMsg == null ){
                return ByteArray(0)
            }
            val messData = memScoped{
                NSData.create(bytes = allocArrayOf(encryptedMsg), length = encryptedMsg.size.toULong())
            }
            val future = CompletableFutureKotlin<ByteArray?>()
            IndyCrypto.anonDecrypt(messData,recipientVk,wallet.walletHandle){
                    error: NSError?, data : NSData? ->
                ErrorHandler(error).handleError()
                val d = memScoped{data}
                var  byteArray = ByteArray(0)
                d?.let {
                    byteArray =   ByteArray(d.length.toInt()).apply {
                        usePinned {
                            memcpy(it.addressOf(0), d.bytes,d.length)
                        }
                    }
                }
                future.complete(byteArray)
            }
            return future.get() ?: ByteArray(0)
        }catch (e :  Exception){
            e.printStackTrace()
        }
        return ByteArray(0)

    }

    override fun packMessage(message: Any?, recipentVerkeys: List<String>?, senderVerkey: String?): ByteArray? {
        try{
            val jsonArray: JSONArray = JSONArray(recipentVerkeys)
            val messageString = message.toString()
            val byteMessage: ByteArray = StringUtils.stringToBytes(messageString,StringUtils.CODEC.UTF_8)

            val messData = memScoped{
                NSData.create(bytes = allocArrayOf(byteMessage), length = byteMessage.size.toULong())
            }
            val future = CompletableFutureKotlin<ByteArray?>()
            IndyCrypto.packMessage(messData,jsonArray.toString(), senderVerkey,wallet.walletHandle){
                    error: NSError?, data : NSData? ->
                ErrorHandler(error).handleError()
                val d = memScoped{data}
                var  byteArray = ByteArray(0)
                d?.let {
                    byteArray =   ByteArray(d.length.toInt()).apply {
                        usePinned {
                            memcpy(it.addressOf(0), d.bytes,d.length)
                        }
                    }
                }
                future.complete(byteArray)
            }
            return future.get() ?: ByteArray(0)
        }catch (e :  Exception){
            e.printStackTrace()
        }
        return ByteArray(0)

    }



    override fun unpackMessage(jwe: ByteArray?): String? {
        try{
            if(jwe == null ){
                return null
            }
            val messData = memScoped{
                NSData.create(bytes = allocArrayOf(jwe), length = jwe.size.toULong())
            }
            val future = CompletableFutureKotlin<ByteArray?>()
            IndyCrypto.unpackMessage(messData,wallet.walletHandle){
                    error: NSError?, data : NSData? ->
                ErrorHandler(error).handleError()
                val d = memScoped{data}
                var  byteArray = ByteArray(0)
                d?.let {
                    byteArray =   ByteArray(d.length.toInt()).apply {
                        usePinned {
                            memcpy(it.addressOf(0), d.bytes,d.length)
                        }
                    }
                }
                future.complete(byteArray)
            }
            val bytes =  future.get() ?: ByteArray(0)
            return StringUtils.bytesToString(bytes, StringUtils.CODEC.UTF_8)
        }catch (e :  Exception){
            e.printStackTrace()
        }
        return null

    }


}