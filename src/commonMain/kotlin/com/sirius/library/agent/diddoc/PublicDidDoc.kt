package com.sirius.sdk.agent.diddoc

import com.sirius.library.agent.wallet.abstract_wallet.AbstractNonSecrets
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.hub.Context
import com.sirius.library.utils.JSONObject
import com.sirius.sdk.agent.aries_rfc.DidDoc

abstract class PublicDidDoc : DidDoc() {
    abstract fun submitToLedger(context: Context<*>?): Boolean
    fun saveToWallet(nonSecrets: AbstractNonSecrets) {
        val tags: JSONObject = JSONObject().
        put("tag1", NON_SECRET_WALLET_NAME).
        put("id", did)
        val opts = RetrieveRecordOptions(false, false, false)
        if (nonSecrets.walletSearch(NON_SECRET_WALLET_NAME, tags.toString(), opts, 1).second == 0) {
            nonSecrets.addWalletRecord(NON_SECRET_WALLET_NAME, did, payload.toString(), tags.toString())
        } else {
            nonSecrets.updateWalletRecordValue(NON_SECRET_WALLET_NAME, did, payload.toString())
        }
    }

    companion object {
        const val NON_SECRET_WALLET_NAME = "PublicDidDoc"
    }
}