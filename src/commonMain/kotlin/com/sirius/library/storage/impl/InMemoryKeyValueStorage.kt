package com.sirius.library.storage.impl

import com.sirius.library.storage.abstract_storage.AbstractKeyValueStorage

class InMemoryKeyValueStorage : AbstractKeyValueStorage() {
    var databases: MutableMap<String, MutableMap<String, Any?>> = HashMap<String, MutableMap<String, Any?>>()
    var selectedDb: MutableMap<String, Any?>? = null
    override fun selectDb(name: String) {
        if (databases.containsKey(name)) {
            selectedDb = databases[name]
        } else {
            val newDb: MutableMap<String, Any?> = HashMap<String, Any?>()
            databases[name] = newDb
            selectedDb = newDb
        }
    }

    override fun set(key: String, value: Any?) {
        selectedDb!![key] = value
    }

    override operator fun get(key: String): Any? {
        return selectedDb!![key]
    }

    override fun delete(key: String) {
        selectedDb!!.remove(key)
    }
}