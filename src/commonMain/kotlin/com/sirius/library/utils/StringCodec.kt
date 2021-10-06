package com.sirius.library.utils

class StringCodec(var encodeCharset: String = US_ASCII) {
    companion object{
        const val US_ASCII  : String = "US_ASCII"
        const val UTF_8  : String = "UTF_8"
    }
    fun fromByteArrayToASCIIString(byteArray: ByteArray) : String{
        return ""
    }

    fun fromASCIIStringToByteArray(string: String?) : ByteArray{
        if(string ==null){
            return ByteArray(0)
        }
        return ByteArray(0)
    }
}