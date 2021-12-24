package com.sirius.library


import Indy.*

import kotlinx.coroutines.*
import platform.Foundation.NSError
import platform.UIKit.UIDevice
import platform.darwin.*


actual class Platform actual constructor() {
    actual val platform: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    val promiseQueue = dispatch_queue_create("com.basilsalad.promiseCompletion" , null)
    //val ff = dispatch_qu
    fun main() = runBlocking {
        launch {
            Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                println("getWalletKey error=" + error)
                println("getWalletKey data=" + data)

            }
        }

    }

    fun main3() : String?{
        var data1: String? = null
        runBlocking {

            val job = launch {
                Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                    println("getWalletKey error=" + error)
                    println("getWalletKey data=" + data)
                    data1 = data
                }
            }
            println("Hello")
            job.join() // wait until child coroutine completes
            println("Done Inside")
        }
        println("data1="+data1)
        println("Done")
        return  data1
    }

    suspend fun insideBlock(){

    }

    fun mainTimeout() : String?{
        var data1: String? = null
        println("Hello1")
        runBlocking {
            // val job = launch {

            withTimeout(2*1000){

                var isDone = false
                try{


                Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                    println("getWalletKey error=" + error)
                    println("getWalletKey data=" + data)
                    if(this.isActive){
                        data1 = data
                    }
                    isDone = true

                }

                }catch (e : Exception){
                    e.printStackTrace()
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

    fun main4() : String?{
        var data1: String? = null
        println("Hello1")
        runBlocking {
            // val job = launch {

            withTimeout(60*1000){



                var isDone = false
                try{
                Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                    println("getWalletKey error=" + error)
                    println("getWalletKey data=" + data)
                    if(this.isActive){
                        data1 = data
                    }
                    isDone = true

                }
                }catch (e : Exception){
                    e.printStackTrace()
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

    fun main6() : String?{
        var data1: String? = null
        println("Hello1")
        runBlocking {
            val job = async {

                withTimeout(60*1000){



                    var isDone = false
                    try{
                        Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                            println("getWalletKey error=" + error)
                            println("getWalletKey data=" + data)
                            if(this.isActive){
                                data1 = data
                            }
                            isDone = true

                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                        isDone = true
                    }
                    while (!isDone && this.isActive  ){

                        println("Notdone")
                    }
                }

            }
            println("Hello")
            job.join() // wait until child coroutine completes
            println("Done Inside")
        }
        println("data1="+data1)
        println("Done")
        return  data1
    }
    fun main5() : String?{
        var data1: String? = null
        println("Hello1")
        runBlocking {
             val job = launch {

            withTimeout(60*1000){



                var isDone = false
                try{
                    Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                        println("getWalletKey error=" + error)
                        println("getWalletKey data=" + data)
                        if(this.isActive){
                            data1 = data
                        }
                        isDone = true

                    }
                }catch (e : Exception){
                    e.printStackTrace()
                    isDone = true
                }
                while (!isDone && this.isActive  ){

                    println("Notdone")
                }
            }

              }
            println("Hello")
            job.join() // wait until child coroutine completes
            println("Done Inside")
        }
        println("data1="+data1)
        println("Done")
        return  data1
    }


    fun main2() {
        runBlocking {
            var data1: String? = null
            var isEvenFired  = false
            val job = launch {
                Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                    println("getWalletKey error=" + error)
                    println("getWalletKey data=" + data)
                    isEvenFired = true;
                    data1 = data

                }
            }

            while (!isEvenFired) {

            }

            println("Hello")
            job.join() // wait until child coroutine completes

            println("data1="+data1)
            println("Done")
        }
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

    fun getWalletKey10(): String? {
        var key: String? = ""
        dispatch_sync(dispatch_get_current_queue()) {
            Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                println("key10 getWalletKey error=" + error)
                println("key10 getWalletKey data=" + data)
                key = data
            }
        }
        return key
    }


    fun getWalletKey(): String? {
        var key: String? = ""
        runBlocking {
            launch {
                Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                    println("getWalletKey error=" + error)
                    println("getWalletKey data=" + data)
                    key = data
                }
            }
        }
        return key
    }

    fun getWalletKey3(): String? {
        var key: String? = ""
        runBlocking {
            Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                println("getWalletKey3() error=" + error)
                println("getWalletKey3() data=" + data)
                key = data
            }
        }
        return key
    }

    fun getWalletKey4(): String? {
        var key: String? = ""
        runBlocking {
            Indy.IndyWallet.run {
                generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                    run {
                        println("getWalletKey3() error=" + error)
                        println("getWalletKey3() data=" + data)
                        key = data
                    }
                }
            }
        }
        return key
    }


    fun getWalletKey2(): String? {
        var key: String? = ""
        Indy.IndyWallet.run {
            generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                println("getWalletKey2 error=" + error)
                println("getWalletKey2 data=" + data)
                key = data
            }
            return key
        }
    }


    fun test1() : String?{
        var error1 :String?  = null
        var isDone = false
        Indy.IndyWallet.run {
            generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                println("getWalletKey2 error=" + error)
                println("getWalletKey2 data=" + data)
                error1 = data
                isDone = true
            }
            return error1
        }

        while (!isDone){
            //print("NotDone")
        }
        return error1
    }


    fun test12() : String? {
        var error1: String? = null
        var isDone = false

       // promiseQueue.
        try{
            Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
                println("getWalletKey2 error=" + error)
                println("getWalletKey2 data=" + data)
                error1 = data
                isDone = true
            }
        }catch (e : Exception){
            e.printStackTrace()
            println("stacktrace"+e.getStackTrace())
        }

        while (!isDone){
            //print("NotDone")
        }
        return error1
    }

    fun test122() : String? {
        var error1: String? = null
        var isDone = false
       // dispatch_sync(dispatch_get_current_queue())
        Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->

            dispatch_sync(dispatch_get_current_queue()) {
                println("getWalletKey2 error=" + error)
                println("getWalletKey2 data=" + data)
                error1 = data
                isDone = true
            }

        }
        while (!isDone){
            //print("NotDone")
        }
        return error1
    }

    fun test1222() : String? {
        var error1: String? = null
        var isDone = false
        dispatch_async(dispatch_get_main_queue()) {
            Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->


                println("getWalletKey2 error=" + error)
                println("getWalletKey2 data=" + data)
                error1 = data
                isDone = true

            }
        }
        while (!isDone){
            //print("NotDone")
        }
        return error1
    }


    fun test12222() : String? {
        var error1: String? = null
        var isDone = false
        Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->

            dispatch_sync(dispatch_get_main_queue()) {
                println("getWalletKey2 error=" + error)
                println("getWalletKey2 data=" + data)
                error1 = data
                isDone = true
            }

        }
        while (!isDone){
            //print("NotDone")
        }
        return error1
    }



    fun testIndyFramework() {
        runBlocking {
            var key = getWalletKey()
            println("key44=" + key)
        }


        var key = getWalletKey()
        println("key33=" + key)


        var key2 = getWalletKey2()
        println("key2=" + key2)
        var key3 = getWalletKey3()
        println("key3=" + key3)

        var key4 = getWalletKey4()
        println("key4=" + key4)


        runBlocking {
            var key5 = getWalletKey4()
            println("key5=" + key5)
        }

        var key10 = getWalletKey10()
        println("key10=" + key10)



        println("IosX64")
        val logger = Indy.IndyLogger.setDefaultLogger("Logger")
        Indy.IndyWallet.generateWalletKeyForConfig("{}") { error: NSError?, data: String? ->
            run {
                println("error=" + error)
                println("data=" + data)
            }
        }
        Indy.IndyDid.abbreviateVerkey(
            "did",
            "fullverkey"
        ) { error: NSError?, data: String? ->
            run {
                println("error=" + error)
                println("data=" + data)
            }
        }
        println("logger=" + logger)


    }


}