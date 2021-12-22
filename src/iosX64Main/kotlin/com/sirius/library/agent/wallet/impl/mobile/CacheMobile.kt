package com.sirius.library.agent.wallet.impl.mobile

import Indy.IndyCache
import com.sirius.library.agent.wallet.LocalPool
import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCache
import com.sirius.library.agent.wallet.abstract_wallet.model.CacheOptions
import com.sirius.library.agent.wallet.abstract_wallet.model.PurgeOptions
import com.sirius.library.agent.wallet.results.ErrorHandler
import com.sirius.library.utils.CompletableFutureKotlin
import platform.Foundation.NSError


actual class CacheMobile actual constructor(val wallet: LocalWallet) : AbstractCache() {

    actual  var timeoutSec = 60L
    actual var poolMobile: PoolMobile = PoolMobile()

    override fun getSchema(poolName: String?, submitter_did: String?, id: String?, options: CacheOptions?): String? {
        val pool: LocalPool? = poolMobile.getPoolHandle(poolName?:"")
        val optionsStr: String? = options?.serialize()
        try {
            val future = CompletableFutureKotlin<String?>()
            IndyCache.getSchema(pool!!.poolHandle!!, wallet.walletHandle,submitter_did, id,optionsStr){
                    error: NSError?, data : String? ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()
        }catch (e  :Exception){
            e.printStackTrace()
        }
        return null
    }

    override fun getCredDef(poolName: String?, submitter_did: String?, id: String?, options: CacheOptions?): String? {

        val pool: LocalPool? = poolMobile.getPoolHandle(poolName?:"")
        val optionsStr: String? = options?.serialize()
        try{
            val future = CompletableFutureKotlin<String?>()
            IndyCache.getCredDef(pool!!.poolHandle!!,wallet.walletHandle,  submitter_did,id,optionsStr){
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

    override fun purgeSchemaCache(options: PurgeOptions?) {
        try{
            val future = CompletableFutureKotlin<Boolean>()
            IndyCache.purgeSchemaCache(wallet.walletHandle,options?.serialize()){
                    error: NSError? ->
                ErrorHandler(error).handleError()
                future.complete(true)
            }
            future.get()
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun purgeCredDefCache(options: PurgeOptions?) {
        try{
            val future = CompletableFutureKotlin<Boolean>()
            IndyCache.purgeCredDefCache(wallet.walletHandle,options?.serialize()){
                    error: NSError? ->
                ErrorHandler(error).handleError()
                future.complete(true)
            }
            future.get()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }


}