package examples

import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.system.measureTimeMillis

object  TestCoroutine {

    @JvmStatic
    fun main(args: Array<String>) {
        println("START")
        startTimer()
        main()
        println("END")
    }




    var intTime = 0;
    fun startTimer(){
        Timer().schedule(0,100){
            println("intTime=$intTime")
            intTime++        }
    }
    fun main() = CoroutineScope(Dispatchers.Default).launch {

        val time = measureTimeMillis {
            println("The answer is ${concurrentSum()}")
        }
        println("Completed in $time ms")
    }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // pretend we are doing something useful here
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // pretend we are doing something useful here, too
        return 29
    }
}