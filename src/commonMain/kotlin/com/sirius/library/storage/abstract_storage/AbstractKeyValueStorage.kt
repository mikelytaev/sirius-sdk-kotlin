package com.sirius.library.storage.abstract_storage

abstract class AbstractKeyValueStorage {
    abstract fun selectDb(name: String)
    abstract operator fun set(key: String, value: Any?)
    abstract operator fun get(key: String): Any?
    abstract fun delete(key: String)
}