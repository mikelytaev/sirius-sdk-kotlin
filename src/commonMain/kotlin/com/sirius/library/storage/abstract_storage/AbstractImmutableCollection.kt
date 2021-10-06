package com.sirius.library.storage.abstract_storage

abstract class AbstractImmutableCollection {
    abstract fun selectDb(name: String)
    abstract fun add(value: Any?, tags: String?)
    abstract fun fetch(tags: String?, limit: Int?): Pair<List<Any>, Int>
    fun fetch(tags: String?): Pair<List<Any>, Int> {
        return fetch(tags, null)
    }
}