package com.sirius.library.utils

class Logger {

    var tag = "DEFAULT_LOG"
    enum class Level {
        INFO,
        DEBUG,
        WARNING,
        ERROR
    }

    companion object {

        fun getLogger(tag : String?) : Logger {
            val logger = Logger()
            logger.tag = tag?:"DEFAULT_LOG"
            return logger
        }
    }

    fun log(level : Level, string: String){
        println(string)
    }

    fun info(string: String){
        log(Level.INFO,string)
    }

}