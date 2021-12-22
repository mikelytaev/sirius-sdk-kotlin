package com.sirius.library.agent

object Codec {

    const  val I32_BOUND = (2 xor 31).toLong()
    val ENCODE_PREFIX: MutableMap<String, Int>  = HashMap<String, Int>()
    fun encode(raw: Any?): String {
        /*
        if (raw == null) {
            return I32_BOUND.toString()
        }
        if (raw is Boolean) {
            return String.format(
                "%d%d",
                ENCODE_PREFIX!![Boolean::class.simpleName],
                if (raw) I32_BOUND + 2 else I32_BOUND + 1
            )
        }
        if (raw is Int && raw >= -I32_BOUND && (raw as Int)< I32_BOUND) {
            return String.format("%d", raw as Int?)
        }
        return if (raw is Long && raw >= -I32_BOUND && (raw as Long)<I32_BOUND) {
            String.format("%d", raw as Long?)
        } else String.format(
            "%d%s",
            ENCODE_PREFIX.getOrDefault(raw::class.simpleName, ENCODE_PREFIX!![""]),
            java.math.BigInteger(
                raw.toString().encodeToByteArray()
            ).add(java.math.BigInteger.valueOf(I32_BOUND)).toString()
        )

         */
        return ""
    }

    init {
        ENCODE_PREFIX[String::class.simpleName?:"string"] = 1
        ENCODE_PREFIX[Boolean::class.simpleName?:"boolean"] = 2
        ENCODE_PREFIX[Int::class.simpleName?:"int"] = 3
        ENCODE_PREFIX[Long::class.simpleName?:"long"] = 3
        ENCODE_PREFIX[Double::class.simpleName?:"double"] = 4
        ENCODE_PREFIX[""] = 9
    }
}
