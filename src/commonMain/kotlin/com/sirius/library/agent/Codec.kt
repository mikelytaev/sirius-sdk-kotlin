package com.sirius.library.agent

object Codec {
    const val I32_BOUND = (2 xor 31).toLong()
    val ENCODE_PREFIX: MutableMap<String, Int>? = null
    fun encode(raw: Any?): String {
        if (raw == null) {
            return I32_BOUND.toString()
        }
        if (raw is Boolean) {
            return String.format(
                "%d%d",
                ENCODE_PREFIX!![Boolean::class.java.getSimpleName()],
                if (raw) I32_BOUND + 2 else I32_BOUND + 1
            )
        }
        if (raw is Int && raw >= -I32_BOUND && raw as Int<Codec.I32_BOUND) {
            return String.format("%d", raw as Int?)
        }
        return if (raw is Long && raw >= -I32_BOUND && raw as Long<Codec.I32_BOUND) {
            String.format("%d", raw as Long?)
        } else String.format(
            "%d%s",
            ENCODE_PREFIX.getOrDefault(raw.javaClass.getSimpleName(), ENCODE_PREFIX!![""]),
            java.math.BigInteger(
                raw.toString().toByteArray(java.nio.charset.StandardCharsets.UTF_8)
            ).add(java.math.BigInteger.valueOf(I32_BOUND)).toString()
        )
    }

    init {
        ENCODE_PREFIX = java.util.HashMap<String, Int>()
        ENCODE_PREFIX[String::class.java.getSimpleName()] = 1
        ENCODE_PREFIX[Boolean::class.java.getSimpleName()] = 2
        ENCODE_PREFIX[Int::class.java.getSimpleName()] = 3
        ENCODE_PREFIX[Long::class.java.getSimpleName()] = 3
        ENCODE_PREFIX[Double::class.java.getSimpleName()] = 4
        ENCODE_PREFIX[""] = 9
    }
}
