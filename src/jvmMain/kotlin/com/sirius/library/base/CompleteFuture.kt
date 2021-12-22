package com.sirius.library.base

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

open class CompleteFuture<T> : CompletableFuture<T?>() {

    fun get(timeout:Long):T?{
        return get(timeout, TimeUnit.SECONDS)
    }


}