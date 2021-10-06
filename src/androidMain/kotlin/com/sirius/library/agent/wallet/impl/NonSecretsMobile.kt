/*
package com.sirius.library.agent.wallet.impl

class NonSecretsMobile(wallet: Wallet) : AbstractNonSecrets() {
    var wallet: Wallet
    var timeoutSec = 60
    fun addWalletRecord(type: String?, id: String?, value: String?, tags: String?) {
        try {
            WalletRecord.add(wallet, type, id, value, tags).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            if (!e.message.contains("WalletItemAlreadyExists")) e.printStackTrace()
        }
    }

    fun updateWalletRecordValue(type: String?, id: String?, value: String?) {
        try {
            WalletRecord.updateValue(wallet, type, id, value).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun updateWalletRecordTags(type: String?, id: String?, tags: String?) {
        try {
            WalletRecord.updateTags(wallet, type, id, tags).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun addWalletRecordTags(type: String?, id: String?, tags: String?) {
        try {
            WalletRecord.addTags(wallet, type, id, tags).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun deleteWalletRecord(type: String?, id: String?, tagNames: List<String?>?) {
        try {
            val arrayTag: String = JSONArray(tagNames).toString()
            WalletRecord.deleteTags(wallet, type, id, arrayTag).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun deleteWalletRecord(type: String?, id: String?) {
        try {
            WalletRecord.delete(wallet, type, id).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getWalletRecord(type: String?, id: String?, options: RetrieveRecordOptions): String? {
        try {
            return WalletRecord.get(wallet, type, id, options.serialize())
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            if (!e.message.contains("WalletItemNotFoundException")) e.printStackTrace()
        }
        return null
    }

    fun walletSearch(
        type: String?,
        query: String?,
        options: RetrieveRecordOptions,
        limit: Int
    ): Pair<List<String>, Int> {
        options.setRetrieveRecords(true)
        options.setRetrieveTotalCount(true)
        val optionStr: String = options.serialize()
        try {
            val search: WalletSearch =
                WalletSearch.open(wallet, type, query, optionStr).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            val searchListString: String =
                search.fetchNextRecords(wallet, limit).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            WalletSearch.closeSearch(search)
            return if (searchListString == null) {
                Pair(ArrayList<E>(), 0)
            } else {
                val searchObj: JSONObject = JSONObject(searchListString)
                val records: JSONArray = searchObj.optJSONArray("records")
                val lis: MutableList<String> = ArrayList<String>()
                if (records != null) {
                    for (rec in records) {
                        lis.add(rec.toString())
                    }
                }
                val totalCount: Int = searchObj.optInt("totalCount")
                Pair(lis, totalCount)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Pair(ArrayList<E>(), 0)
    }

    init {
        this.wallet = wallet
    }
}
*/
