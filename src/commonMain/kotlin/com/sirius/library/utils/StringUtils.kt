package com.sirius.library.utils

object StringUtils {

    val encodeCharset: String = StringCodec.US_ASCII

    fun stringToBytes(string: String): ByteArray {
        if (encodeCharset == StringCodec.US_ASCII) {
            val codec = StringCodec()
            return codec.fromASCIIStringToByteArray(string)
        } else if (encodeCharset == StringCodec.UTF_8) {
            return string.encodeToByteArray()
        }
        return ByteArray(0)
    }

    fun bytesToString(bytes: ByteArray): String {
        if (encodeCharset == StringCodec.US_ASCII) {
            val codec = StringCodec()
            return codec.fromByteArrayToASCIIString(bytes)
        } else if (encodeCharset == StringCodec.UTF_8) {
            return bytes.decodeToString()
        }
        return ""
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
        //TODO CharUtils.isAscii(charOne)
        /*for (charOne in chars) {
            if (CharUtils.isAscii(charOne)) {
                escapedString.append(charOne)
            } else {
                val escapedStr: String = CharUtils.unicodeEscaped(charOne)
                escapedString.append(escapedStr)
            }
        }*/
        return escapedString.toString()
    }
}
