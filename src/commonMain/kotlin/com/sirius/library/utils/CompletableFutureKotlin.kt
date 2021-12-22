package com.sirius.library.utils



expect open class CompletableFutureKotlin<T>() {

    fun get(timeout : Long) : T?
    fun isDone(): Boolean
    fun complete(data: T?) : Boolean
}