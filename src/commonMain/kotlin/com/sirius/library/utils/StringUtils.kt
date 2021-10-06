package com.sirius.library.utils

object StringUtils {
    val encodeCharset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.US_ASCII
    fun stringToBytes(string: String): ByteArray {
        return string.toByteArray(encodeCharset)
    }

    fun bytesToString(bytes: ByteArray?): String {
        return String(bytes, encodeCharset)
    }

    fun stringToBase58String(string: String): String {
        val bytes = stringToBytes(string)
        return bytesToBase58String(bytes)
    }

    fun bytesToBase58String(bytes: ByteArray?): String {
        return Base58.encode(bytes!!)
    }

    fun escapeStringLikePython(string: String): String {
        val chars = string.toCharArray()
        val escapedString: StringBuilder = StringBuilder()
        for (charOne in chars) {
            if (CharUtils.isAscii(charOne)) {
                escapedString.append(charOne)
            } else {
                val escapedStr: String = CharUtils.unicodeEscaped(charOne)
                escapedString.append(escapedStr)
            }
        }
        return escapedString.toString()
    }
}
