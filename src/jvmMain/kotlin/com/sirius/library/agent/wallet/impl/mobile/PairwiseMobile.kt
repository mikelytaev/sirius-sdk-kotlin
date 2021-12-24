package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractPairwise
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import org.hyperledger.indy.sdk.pairwise.Pairwise
import org.hyperledger.indy.sdk.wallet.Wallet

actual class PairwiseMobile actual constructor(val wallet: LocalWallet, actual var nonSecretsMobile: NonSecretsMobile) : AbstractPairwise() {

    actual var timeoutSec = 60L
    actual val STORAGE_TYPE = "pairwise"
    actual val CONST_VALUE = "pairwise"
    actual val DEFAULT_FETCH_LIMIT = 1000

    override fun isPairwiseExist(theirDid: String?): Boolean {
        try {
            return Pairwise.isPairwiseExists(wallet, theirDid).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun createPairwise(theirDid: String?, myDid: String?, metadata: JSONObject?, tags: JSONObject?) {
        try {
            updateWalletRecordValueTagsSafely(STORAGE_TYPE, theirDid, tags)
            Pairwise.createPairwise(wallet, theirDid, myDid, metadata.toString())
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    override fun listPairwise(): List<Any>? {
        try {
            val listPairwise: String =
                Pairwise.listPairwise(wallet).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
            val listPairwiseArray: JSONArray = JSONArray(listPairwise)
            val pairwiseList: MutableList<Any> = ArrayList<Any>()
            for (i in 0 until listPairwiseArray.length()) {
                val obect: Any? = listPairwiseArray.get(i)
                if (obect != null) {
                    if (obect is JSONObject) {
                        val theirDid: String? = (obect as JSONObject).optString("their_did")
                        val tagsObject: JSONObject? = getWalletRecordTags(STORAGE_TYPE, theirDid)
                        (obect as JSONObject).put("tags", tagsObject)
                    }
                    pairwiseList.add(obect)
                }

            }
            return pairwiseList
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getPairwise(thierDid: String?): String? {
        try {
            val pairwiseInfoJson: String =
                Pairwise.getPairwise(wallet, thierDid).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
            val info: JSONObject = JSONObject(pairwiseInfoJson)
            info.put("their_did", thierDid)
            val tagsObj: JSONObject? = getWalletRecordTags(STORAGE_TYPE, thierDid)
            info.put("tags", tagsObj)
            return info.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun setPairwiseMetadata(theirDid: String?, metadata: JSONObject?, tags: JSONObject?) {
        try {
            updateWalletRecordValueTagsSafely(STORAGE_TYPE, theirDid, tags)
            Pairwise.setPairwiseMetadata(wallet, theirDid, metadata.toString())
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    override fun search(tags: JSONObject?, limit: Int?): Pair<List<String>, Int> {
        var limit = limit
        val opts = RetrieveRecordOptions(false, false, true)
        if (limit == null) {
            limit = DEFAULT_FETCH_LIMIT
        }
        val (first, second) = nonSecretsMobile.walletSearch(STORAGE_TYPE, tags.toString(), opts, limit)
        return if (first == null) {
            Pair(ArrayList<String>(), second)
        } else {
            val pairwiseList: MutableList<String> = ArrayList<String>()
            for (item in first) {
                val itemObject: JSONObject = JSONObject(item)
                val pw = getPairwise(itemObject.optString("id"))
                if (pw != null) {
                    pairwiseList.add(pw)
                }
            }
            Pair(pairwiseList, second)
        }
    }

    fun updateWalletRecordValueTagsSafely(type: String?, id: String?, tags: JSONObject?) {
        var tags: JSONObject? = tags
        if (tags == null) {
            tags = JSONObject()
        }
        val opts = RetrieveRecordOptions(false, false, true)
        val record = nonSecretsMobile.getWalletRecord(type, id, opts)
        if (record == null) {
            nonSecretsMobile.addWalletRecord(type, id, CONST_VALUE, tags.toString())
        } else {
            nonSecretsMobile.updateWalletRecordTags(type, id, tags.toString())
        }
    }

    fun getWalletRecordTags(type: String?, id: String?): JSONObject {
        val opts = RetrieveRecordOptions(false, false, true)
        val record = nonSecretsMobile.getWalletRecord(type, id, opts)
        if (record.isNullOrEmpty()) {
            return JSONObject()
        } else {
            try{
                val recordObject: JSONObject = JSONObject(record)
                val tags: JSONObject = recordObject.optJSONObject("tags") ?: return JSONObject()
                return tags
            }catch (e : Exception){
                e.printStackTrace()
            }
            return JSONObject()
        }
    }


}