package com.sirius.library.agent.aries_rfc

import com.sirius.library.agent.wallet.abstract_wallet.AbstractNonSecrets
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonObject

object SchemasNonSecretStorage {
    fun storeCredSchemaNonSecret(ns: AbstractNonSecrets, schema: JSONObject) {
        ns.addWalletRecord("schemas", schema.optString("id"), schema.toString())
    }

    fun storeCredDefNonSecret(ns: AbstractNonSecrets, credDef: JSONObject) {
        ns.addWalletRecord("credDefs", credDef.getString("id"), credDef.toString())
    }

    fun getCredSchemaNonSecret(ns: AbstractNonSecrets, id: String?): JSONObject {
        val record: String? = ns.getWalletRecord("schemas", id, RetrieveRecordOptions(true, true, false))
        return if (record != null) {
            JSONObject(JSONObject(record).optString("value"))
        } else JSONObject()
    }

    fun getCredDefNonSecret(ns: AbstractNonSecrets, id: String?): JSONObject {
        val record: String? = ns.getWalletRecord("credDefs", id, RetrieveRecordOptions(true, true, false))
        return if (record != null) {
            JSONObject(JSONObject(record).optString("value"))
        } else JSONObject()
    }
}
