package com.sirius.library

import com.sirius.library.utils.CompletableFutureKotlin
import kotlin.test.Test

class CompletableFutureTest {

    @Test
    fun testThenApply() {
        val future = CompletableFutureKotlin<String>()
        val future2 = future.thenApply<Int> {
            println("future answer=$it")
            2
        }
        future.complete("test")

        val futureTest = future2?.get()


        println("futureTest=$futureTest")
    }

}