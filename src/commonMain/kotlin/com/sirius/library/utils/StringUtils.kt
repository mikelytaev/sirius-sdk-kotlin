package com.sirius.library.utils

object StringUtils {

    enum class CODEC{
        US_ASCII,
        UTF_8
    }
 //   const val US_ASCII  : String = "US_ASCII"
    const val UTF_8  : String = "UTF_8"


    fun stringToBytes(string: String,encodeCharset: CODEC): ByteArray {
        if (encodeCharset == CODEC.US_ASCII) {
            val codec = StringCodec()
            return codec.fromASCIIStringToByteArray(string)
        } else if (encodeCharset == CODEC.UTF_8) {
            return string.encodeToByteArray()
        }
        return ByteArray(0)
    }

    fun bytesToString(bytes: ByteArray,encodeCharset: CODEC ): String {
        if (encodeCharset == CODEC.US_ASCII) {
            val codec = StringCodec()
            return codec.fromByteArrayToASCIIString(bytes)
        } else if (encodeCharset == CODEC.UTF_8) {
            return bytes.decodeToString()
        }
        return ""
    }

    fun stringToBase58String(string: String): String {
        val bytes = stringToBytes(string, CODEC.UTF_8)
        return bytesToBase58String(bytes)
    }

    fun bytesToBase58String(bytes: ByteArray?): String {
        return Base58.encode(bytes!!)
    }

    fun escapeStringLikePython(string: String): String {
        val chars = string.toCharArray()
        val escapedString: StringBuilder = StringBuilder()
        //TODO CharUtils.isAscii(charOne)
        /*for (charOne in chars) {
            if (CharUtils.isAscii(charOne)) {
                escapedString.append(charOne)
            } else {
                val escapedStr: String = CharUtils.unicodeEscaped(charOne)
                escapedString.append(escapedStr)
            }
        }
           return escapedString.toString()
           */

        return string
    }
}
