package com.sirius.library.utils

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.Function

actual  open class CompletableFutureKotlin<T> {

    var future = CompletableFuture<T>()


    open fun <U> thenApply(fn: Function<in T?, out U>?): CompletableFutureKotlin<U>? {
        val kotlinFuture =  CompletableFutureKotlin<U>()
        val futureAfter =   this.future.thenApply<U> (fn)
        kotlinFuture.future = futureAfter
        return  kotlinFuture
    }


    actual fun get(timeout: Long): T? {
        return future.get(timeout, TimeUnit.SECONDS)
    }

    actual fun isDone(): Boolean {
        return future.isDone
    }

    actual fun complete(data: T?): Boolean {
        return future.complete(data)
    }

}

