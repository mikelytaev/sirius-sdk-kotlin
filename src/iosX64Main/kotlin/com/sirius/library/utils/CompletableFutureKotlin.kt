package com.sirius.library.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull



actual open class CompletableFutureKotlin<T> actual constructor() {


    actual fun isDone(): Boolean {
       return isComplete
    }

    var promisedObject: T? = null
    var isComplete = false
    var isCanceled = false
    var childFuture  : CompletableFutureKotlin<Any?>? = null

    var callback  : ((promisedObject: T?)->Any?)? = null



    fun  completeChild(){
       val type =    callback?.invoke(promisedObject)
        childFuture?.complete(type)
    }

    fun <U> thenApply(callback : (promisedObject: T?) ->  U? ): CompletableFutureKotlin<U> {
            val future = CompletableFutureKotlin<Any?>()
            this.childFuture = future
             this.callback = callback
            return future as CompletableFutureKotlin<U>
    }

    fun cancel() {
        isCanceled = true
    }

    open fun get(): T? {
       return get(60)
    }

    actual  open fun get(timeout: Long ): T? {
        runBlocking {

            withTimeoutOrNull(timeout * 1000) {
                while (!isComplete ) {

                }
            }
        }
        return promisedObject
    }




    actual fun complete(data: T?) : Boolean {
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.Default) {
            promisedObject = data
            completeChild()
            isComplete = true
        }
        return true
    }



}