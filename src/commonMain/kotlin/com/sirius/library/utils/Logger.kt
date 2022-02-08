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


    fun logLongText(sb: String) {
        if (sb == null) {
            info(sb)
            return
        }
        if (sb.length > 2900) {
            info("sb.length = " + sb.length)
            val chunkCount = sb.length / 2900 // integer division
            for (i in 0..chunkCount) {
                val max = 2900 * (i + 1)
                if (max >= sb.length) {
                    info( "chunk " + i + " of " + chunkCount + ":" + sb.substring(2900 * i))
                } else {
                    info("chunk " + i + " of " + chunkCount + ":" + sb.substring(2900 * i, max))
                }
            }
        } else {
            info(sb)
        }
    }

    fun info(string: String){
        log(Level.INFO,string)
    }

    fun verbose(string: String){
        log(Level.DEBUG,string)
    }
}