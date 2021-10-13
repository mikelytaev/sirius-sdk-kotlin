package com.sirius.library.utils



expect class CompletableFutureKotlin<T> {

    fun get(timeout : Long) : T?
}