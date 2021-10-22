package com.sirius.library.mobile.helpers





import com.google.gson.Gson
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.pairwise.WalletPairwiseList
import com.sirius.library.mobile.SiriusSDK
import com.sirius.library.mobile.models.CredentialsRecord
import com.sirius.library.utils.JSONObject


class PairwiseHelper {

    companion object {
        private var pairwiseHelper: PairwiseHelper? = null

        @JvmStatic
        fun getInstance(): PairwiseHelper {
            if (pairwiseHelper == null) {
                pairwiseHelper = PairwiseHelper()
            }
            return pairwiseHelper!!
        }
    }


    fun getAllPairwise(): List<Pairwise> {
        val list =
            (SiriusSDK.getInstance().context.currentHub.agent?.wallet?.pairwise?.listPairwise() as? List<String>).orEmpty()
        val mutableList: MutableList<Pairwise> = mutableListOf()
        list.forEach {
            val pairwiseObj = JSONObject(it)
            val metadata = pairwiseObj.optString("metadata")
            metadata?.let { metadataObj ->
                val pairwise = WalletPairwiseList.restorePairwise(JSONObject(metadataObj))
                mutableList.add(pairwise)
            }

        }

        return mutableList
    }


    fun getAllCredentials(): List<CredentialsRecord> {
        val list =
            (SiriusSDK.getInstance().context.currentHub.agent?.wallet?.anoncreds?.proverGetCredentials("{}"))
        return list?.mapNotNull {
            Gson().fromJson(it, CredentialsRecord::class.java)
        }.orEmpty()
    }


    fun getPairwise(theirDid: String? = null, theirVerkey: String? = null): Pairwise? {
        if (!theirDid.isNullOrEmpty()) {
            val pairwise =  SiriusSDK.getInstance().context.currentHub.pairwiseList?.loadForDid(theirDid)
           pairwise?.let {
               return it
           }
        }
        if (!theirVerkey.isNullOrEmpty()) {
            return SiriusSDK.getInstance().context.currentHub.pairwiseList?.loadForVerkey(theirVerkey)
        }
        return null;
    }
}