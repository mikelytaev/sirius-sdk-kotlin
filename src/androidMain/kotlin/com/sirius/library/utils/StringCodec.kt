package com.sirius.library.utils


import java.nio.charset.StandardCharsets

actual class StringCodec actual constructor() {

    actual fun fromByteArrayToASCIIString(byteArray: ByteArray): String {
        return String(byteArray, StandardCharsets.US_ASCII)
    }

    actual fun fromASCIIStringToByteArray(string: String?): ByteArray {
        if (string==null) {
            return ByteArray(0)
        }
        return string.toByteArray(StandardCharsets.US_ASCII)
    }


}