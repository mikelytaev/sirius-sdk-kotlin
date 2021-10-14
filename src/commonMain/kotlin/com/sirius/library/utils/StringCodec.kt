package com.sirius.library.utils

import com.sirius.library.utils.StringUtils.US_ASCII

expect class StringCodec() {

    fun fromByteArrayToASCIIString(byteArray: ByteArray) : String

    fun fromASCIIStringToByteArray(string: String?) : ByteArray
}