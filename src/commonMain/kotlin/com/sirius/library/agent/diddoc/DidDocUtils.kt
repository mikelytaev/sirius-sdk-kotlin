package com.sirius.sdk.agent.diddoc

import com.sirius.sdk.agent.aries_rfc.DidDoc
import com.sirius.sdk.agent.diddoc.PublicDidDoc.NON_SECRET_WALLET_NAME
import com.sirius.sdk.agent.wallet.abstract_wallet.AbstractNonSecrets
import com.sirius.sdk.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import org.json.JSONObject

object DidDocUtils {
    fun resolve(did: String): PublicDidDoc? {
        return if (did.startsWith("did:iota:")) {
            IotaPublicDidDoc.load(did)
        } else null
    }

    fun publicDidList(ns: AbstractNonSecrets): List<String> {
        val query = JSONObject()
        query.put("tag1", NON_SECRET_WALLET_NAME)
        val opts = RetrieveRecordOptions(false, false, false)
        val (first) = ns.walletSearch(NON_SECRET_WALLET_NAME, query.toString(), opts, 10000)
        val res: MutableList<String> = java.util.ArrayList<String>()
        for (s in first) {
            res.add(JSONObject(s).optString("id"))
        }
        return res
    }

    fun fetchFromWallet(did: String?, ns: AbstractNonSecrets): DidDoc? {
        val record: String = ns.getWalletRecord(NON_SECRET_WALLET_NAME, did, RetrieveRecordOptions(true, true, true))
        return if (record != null) {
            DidDoc(JSONObject(JSONObject(record).optString("value")))
        } else null
    }
}