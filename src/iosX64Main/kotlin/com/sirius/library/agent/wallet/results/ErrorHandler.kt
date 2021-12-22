package com.sirius.library.agent.wallet.results

import platform.Foundation.NSError

class ErrorHandler(val error : NSError?) {


    fun handleError(){
        printError()
    }

    fun printError(){
        error?.let {
            println("error=$error")
        }
    }
}