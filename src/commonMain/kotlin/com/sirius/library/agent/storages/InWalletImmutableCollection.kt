package com.sirius.library.agent.storages

import com.sirius.library.agent.wallet.abstract_wallet.AbstractNonSecrets
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.storage.abstract_storage.AbstractImmutableCollection
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID

class InWalletImmutableCollection(storage: AbstractNonSecrets) : AbstractImmutableCollection() {
    var DEFAULT_FETCH_LIMIT = 1000
    var selectedDb: String? = null
    var storage: AbstractNonSecrets

    override fun selectDb(name: String) {
        selectedDb = name
    }


    override fun add(value: Any?, tags: String?) {
        storage.addWalletRecord(selectedDb, UUID.randomUUID.toString(), value.toString(), tags)
    }

    override fun fetch(tags: String?, limit: Int?): Pair<List<Any>, Int> {
        var limit = limit
        if (limit == null) {
            limit = DEFAULT_FETCH_LIMIT
        }
        val (first, second) = storage.walletSearch(
            selectedDb, tags,
            RetrieveRecordOptions(false, true, false), limit
        )
        return if (!first.isNullOrEmpty()) {
            val listValue: MutableList<Any> = ArrayList<Any>()
            for (i in 0 until first!!.size) {
                val `object`: Any = first!![i]
                val jsonObject = JSONObject(`object`.toString())
                val values: String? = jsonObject.optString("value")
                if (values != null) {
                    listValue.add(values)
                }
            }
            Pair(listValue, second)
        } else {
            Pair(ArrayList<Any>(), second)
        }
    }

    init {
        this.storage = storage
    }
}

