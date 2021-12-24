package com.sirius.library

import com.sirius.library.utils.CompletableFutureKotlin
import kotlinx.coroutines.*
import org.hyperledger.indy.sdk.wallet.Wallet

actual class Platform actual constructor() {
    actual val platform: String
        get() = ""


    fun getFuture() : CompletableFutureKotlin<String>{

        val future = CompletableFutureKotlin<String> ()
        Thread(){

            Thread.sleep(2*1000);
            var data1: String? = null
            data1 = "data"
            future.complete(data1)
        }.start()

        return  future
    }

    suspend fun inside(){

    }
    fun main4() : String?{
        var data1: String? = null
        println("Hello1")
        runBlocking {
           // val job = launch {

                    withTimeout(4*1000){

                        var isDone = false
                        getFuture().whenComplete { data, error ->
                            println("getWalletKey error=" + error)
                            println("getWalletKey data=" + data)
                            if(this.isActive){
                                data1 = data
                            }
                            isDone = true
                        }
                        while (!isDone && this.isActive  ){

                            println("Notdone")
                        }
                    }

          //  }
            println("Hello")
           // job.join() // wait until child coroutine completes
            println("Done Inside")
        }
        println("data1="+data1)
        println("Done")
        return  data1
    }


    fun test() {
        runBlocking {
            val job = launch { // launch a new coroutine and keep a reference to its Job
                delay(1000L)
                println("World!")
            }
            println("Hello")
            job.join() // wait until child coroutine completes
            println("Done")
        }
    }

}