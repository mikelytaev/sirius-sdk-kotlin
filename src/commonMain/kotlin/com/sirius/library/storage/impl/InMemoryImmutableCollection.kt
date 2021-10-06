package com.sirius.library.storage.impl

import com.sirius.library.storage.abstract_storage.AbstractImmutableCollection
import com.sirius.library.utils.JSONObject

class InMemoryImmutableCollection : AbstractImmutableCollection() {
    var databases: MutableMap<String, MutableList<Pair<String?, Any?>>> = HashMap<String, MutableList<Pair<String?, Any?>>>()
    var selectedDb: MutableList<Pair<String?, Any?>>? = null

    override fun selectDb(name: String) {
        if (databases.containsKey(name)) {
            selectedDb = databases[name]
        } else {
            val newDb: MutableList<Pair<String?, Any?>> = ArrayList<Pair<String?, Any?>>()
            databases[name] = newDb
            selectedDb = newDb
        }
    }

    override fun add(value: Any?, tags: String?) {
        val item: Pair<String?, Any?> = Pair(tags, value)
        selectedDb!!.add(item)
    }

    override fun fetch(tags: String?, limit: Int?): Pair<List<Any>, Int> {
        val result: MutableList<Any> = ArrayList<Any>()
        for (i in selectedDb!!.indices) {
            val (first, second) = selectedDb!![i]
            val tagsObj: JSONObject = JSONObject(tags)
            val tagsItemObj: JSONObject = JSONObject(first)
            for (key in tagsObj.keySet()) {
                val tag: String? = tagsObj.getString(key)
                if (tagsItemObj.has(key)) {
                    val itemTag: String? = tagsItemObj.getString(key)
                    if (itemTag == tag) {
                        second?.let {
                            result.add(second)
                        }
                    }
                }
            }
        }
        return Pair(result, result.size)
    }
}

