package com.sirius.library.agent.wallet.impl.mobile

import Indy.IndyCrypto
import Indy.IndyDid
import com.sirius.library.agent.wallet.LocalPool
import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractDID
import com.sirius.library.agent.wallet.results.CryptoJSONParameters
import com.sirius.library.agent.wallet.results.DidJSONParameters
import com.sirius.library.agent.wallet.results.DidResults
import com.sirius.library.agent.wallet.results.ErrorHandler
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import platform.Foundation.NSError


actual class DIDMobile actual constructor(val wallet: LocalWallet) : AbstractDID() {

    actual var timeoutSec = 60L
    actual var poolMobile: PoolMobile = PoolMobile()
    override fun createAndStoreMyDid(did: String?, seed: String?, cid: Boolean?): Pair<String, String> {
        try {
            val future = CompletableFutureKotlin<Pair<String, String>>()
            val didJson = DidJSONParameters.CreateAndStoreMyDidJSONParameter(did, seed, null, cid)
            val didJsonString = didJson.toString()
            IndyDid.createAndStoreMyDid(
                didJsonString,
                wallet.walletHandle
            ) { error: NSError?, data: String?, data1: String? ->
                ErrorHandler(error).handleError()
                future.complete(Pair(data ?: "", data1 ?: ""))
            }
            return future.get() ?: Pair("", "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair<String, String>("", "")
    }

    override fun storeTheirDid(did: String?, verkey: String?) {
        val identityJson: JSONObject = JSONObject().put("did", did)
        if (verkey != null) identityJson.put("verkey", verkey)
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyDid.storeTheirDid(identityJson.toString(), wallet.walletHandle) {
                ErrorHandler(it).handleError()
                future.complete(true)
            }
            future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setDidMetadata(did: String?, metadata: String?) {
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyDid.setMetadata(metadata, did, wallet.walletHandle) {
                ErrorHandler(it).handleError()
                future.complete(true)
            }
            future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun listMyDidsWithMeta(): List<Any?>? {
        try {
            val future = CompletableFutureKotlin<String?>()
            IndyDid.listMyDidsWithMeta(wallet.walletHandle) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            val listDidsWithMetaJson = future.get()
            val listDidsWithMeta: JSONArray = JSONArray(listDidsWithMetaJson)
            return listDidsWithMeta.toList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getDidMetadata(did: String?): String? {
        try {
            val future = CompletableFutureKotlin<String?>()
            IndyDid.getMetadataForDid(did, wallet.walletHandle) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun keyForLocalDid(did: String?): String? {
        try {
            val future = CompletableFutureKotlin<String?>()
            IndyDid.keyForLocalDid(did, wallet.walletHandle) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun keyForDid(poolName: String?, did: String?): String? {
        try {
            val pool: LocalPool? = poolMobile.getPoolHandle(poolName ?: "")
            val future = CompletableFutureKotlin<String?>()
            IndyDid.keyForDid(did, pool!!.poolHandle!!, wallet.walletHandle) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun createKey(seed: String?): String? {
        try {
            val future = CompletableFutureKotlin<String?>()
            val jsonCrypto = CryptoJSONParameters.CreateKeyJSONParameter(seed, null)
            IndyCrypto.createKey(jsonCrypto.toString(), wallet.walletHandle) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun replaceKeysStart(did: String?, seed: String?): String? {

        try {
            val future = CompletableFutureKotlin<String?>()
            val jsonCrypto = CryptoJSONParameters.CreateKeyJSONParameter(seed, null)
            IndyDid.replaceKeysStartForDid(
                did,
                jsonCrypto.toString(),
                wallet.walletHandle
            ) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun replaceKeysApply(did: String?) {

        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyDid.replaceKeysApplyForDid(did, wallet.walletHandle) { error: NSError? ->
                ErrorHandler(error).handleError()
                future.complete(true)
            }
            future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun setKeyMetadata(verkey: String?, metadata: String?) {
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyCrypto.setMetadata(metadata, verkey, wallet.walletHandle) { error: NSError? ->
                ErrorHandler(error).handleError()
                future.complete(true)
            }
            future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getKeyMetadata(verkey: String?): String? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyCrypto.getMetadataForKey(verkey, wallet.walletHandle) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun setEndpointForDid(did: String?, address: String?, transportKey: String?) {
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyDid.setEndpointAddress(address, transportKey, did, wallet.walletHandle) { error: NSError? ->
                ErrorHandler(error).handleError()
                future.complete(true)
            }
            future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getEndpointForDid(pooName: String?, did: String?): Pair<String, String>? {

        try {
            val future = CompletableFutureKotlin<Pair<String, String>?>()
            val pool: LocalPool? = poolMobile.getPoolHandle(pooName ?: "")
            IndyDid.getEndpointForDid(
                did,
                wallet.walletHandle,
                pool!!.poolHandle!!
            ) { error: NSError?, data: String?, data1: String? ->
                ErrorHandler(error).handleError()
                future.complete(Pair(data!!, data1!!))
            }
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun getMyDidMeta(did: String?): Any? {
        try {
            val future = CompletableFutureKotlin<String?>()
            TODO("NOT IMPLEMENTED IN INDYWALLET!!")
            //IndyDid.listMyDidsWithMeta()
            future.complete("")
            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null


    }

    override fun abbreviateVerKey(did: String?, fullVerkey: String?): String? {

        try {
            val future = CompletableFutureKotlin<String?>()

            IndyDid.abbreviateVerkey(did, fullVerkey) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }

            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun qualifyDid(did: String?, method: String?): String? {

        try {
            val future = CompletableFutureKotlin<String?>()

            IndyDid.qualifyDid(did, method, wallet.walletHandle) { error: NSError?, data: String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }

            return future.get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}