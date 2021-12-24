package com.sirius.library.agent.wallet.impl.mobile

import Indy.IndyAnoncreds
import Indy.IndyBlobStorage
import Indy.IndyHandle
import Indy.IndyNonSecrets
import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractNonSecrets
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.agent.wallet.results.AnoncredsResults
import com.sirius.library.agent.wallet.results.ErrorHandler
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.Foundation.NSError
import platform.Foundation.NSNumber


actual class NonSecretsMobile actual constructor(val wallet: LocalWallet) : AbstractNonSecrets() {

    actual var timeoutSec = 60L
    override fun addWalletRecord(type: String?, id: String?, value: String?, tags: String?) {
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyNonSecrets.addRecordInWallet(wallet.walletHandle,type,id,value,tags ){
                future.complete(true)
                ErrorHandler(it).handleError()
            }
            future.get(timeoutSec)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun updateWalletRecordValue(type: String?, id: String?, value: String?) {
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyNonSecrets.updateRecordValueInWallet(wallet.walletHandle,type,id,value ){
                future.complete(true)
                ErrorHandler(it).handleError()
            }
            future.get(timeoutSec)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun updateWalletRecordTags(type: String?, id: String?, tags: String?) {

        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyNonSecrets.updateRecordTagsInWallet(wallet.walletHandle,type,id,tags ){
                future.complete(true)
                ErrorHandler(it).handleError()
            }
            future.get(timeoutSec)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun addWalletRecordTags(type: String?, id: String?, tags: String?) {
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyNonSecrets.addRecordTagsInWallet(wallet.walletHandle,type,id,tags ){
                future.complete(true)
                ErrorHandler(it).handleError()
            }
            future.get(timeoutSec)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun deleteWalletRecord(type: String?, id: String?, tagNames: List<String?>?) {

        try {
            val arrayTag: String = JSONArray(tagNames).toString()
            val future = CompletableFutureKotlin<Boolean>()
            IndyNonSecrets.deleteRecordTagsInWallet(wallet.walletHandle,type,id ,arrayTag){
                future.complete(true)
                ErrorHandler(it).handleError()
            }
            future.get(timeoutSec)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun deleteWalletRecord(type: String?, id: String?) {
        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyNonSecrets.deleteRecordInWallet(wallet.walletHandle,type,id ){
                future.complete(true)
                ErrorHandler(it).handleError()
            }
            future.get(timeoutSec)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getWalletRecord(type: String?, id: String?, options: RetrieveRecordOptions?): String? {
        try {
            val future = CompletableFutureKotlin<String?>()
            IndyNonSecrets.getRecordFromWallet(wallet.walletHandle,type,id ,options?.serialize())
            {error: NSError?, data : String? ->
                future.complete(data)
                ErrorHandler(error).handleError()
            }
           return future.get(timeoutSec)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    override fun walletSearch(
        type: String?,
        query: String?,
        options: RetrieveRecordOptions?,
        limit: Int
    ): Pair<List<String>, Int> {
        options?.setRetrieveRecords(true)
        options?.setRetrieveTotalCount(true)
        val optionStr: String? = options?.serialize()

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyNonSecrets.openSearchInWallet(wallet.walletHandle,type,query,optionStr)
            {error: NSError?, searchHandle : IndyHandle? ->
                searchHandle?.let {
                    IndyNonSecrets.fetchNextRecordsFromSearch(searchHandle,wallet.walletHandle, NSNumber(limit) ){
                            error: NSError?, data : String? ->
                        future.complete(data)
                        ErrorHandler(error).handleError()
                        IndyNonSecrets.closeSearchWithHandle(searchHandle){
                            ErrorHandler(it).handleError()
                        }
                    }
                }

            }

           val searchListString =  future.get(timeoutSec)
            return if (searchListString == null) {
                Pair(listOf(), 0)
            } else {
                val searchObj: JSONObject = JSONObject(searchListString)
                val records: JSONArray = searchObj.optJSONArray("records") ?: JSONArray()
                val lis: MutableList<String> = ArrayList<String>()
                if (records != null) {
                    for (rec in records) {
                        lis.add(rec.toString())
                    }
                }
                val totalCount: Int = searchObj.optInt("totalCount") ?: 0
                Pair(lis, totalCount)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair(listOf(), 0)
    }

}