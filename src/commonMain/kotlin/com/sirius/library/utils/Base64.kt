package com.sirius.library.utils

import kotlinx.coroutines.*

class Base64 {

    companion object{
        fun getUrlDecoder() : Decoder{
           return Decoder()
        }

        fun getDecoder() : Decoder{
            return Decoder()
        }

        fun getUrlEncoder() : Encoder{
            return Encoder()
        }

        fun getEncoder() : Encoder{
            return Encoder()
        }
    }

    class Decoder(){


        fun decode(byteArray : ByteArray) : ByteArray{
            return ByteArray(0)
        }

        fun decode(string: String) : ByteArray{
            return ByteArray(0)
        }
    }

    class Encoder(){
        fun encode(byteArray : ByteArray?) : ByteArray{
            if(byteArray == null){
                return ByteArray(0)
            }
            return ByteArray(0)
        }
    }

}