package com.sirius.library.utils

class KeyPair(publicKey: Key, secretKey: Key) {
    private val secretKey: Key
    private val publicKey: Key
    fun getSecretKey(): Key {
        return secretKey
    }

    fun getPublicKey(): Key {
        return publicKey
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is KeyPair) return false
        val other = obj
        return (other.getSecretKey().equals(getSecretKey())
                && other.getPublicKey().equals(getPublicKey()))
    }

    init {
        this.publicKey = publicKey
        this.secretKey = secretKey
    }
}


class Key private constructor(val asBytes: ByteArray) {
    val asHexString: String
        get() = ""//LazySodium.toHex(asBytes)

    fun getAsPlainString(charset: String): String {
        return ""///String(asBytes, charset)""
    }

    val asPlainString: String
        get() = ""//getAsPlainString(java.nio.charset.Charset.forName("UTF-8"))

    override fun equals(obj: Any?): Boolean {
        if (obj !is Key) return false
        return obj.asHexString.equals(asHexString, ignoreCase = true)
    }

    companion object {
        /**
         * Create a Key from a hexadecimal string.
         * @param hexString A hexadecimal encoded string.
         * @return A new Key.
         */
        fun fromHexString(hexString: String?): Key {
            return Key(ByteArray(0))//Key(LazySodium.toBin(hexString))
        }

        /**
         * Create a Key from a base64 string.
         * @param base64String A base64 encoded string.
         * @return A new Key.
         */
        fun fromBase64String(base64String: String?): Key {
            return Key(ByteArray(0))//Key(java.util.Base64.getDecoder().decode(base64String))
        }

        /**
         * Create a Key from a regular, unmodified, not encoded string.
         * @param str A plain string.
         * @return A new Key.
         */
        fun fromPlainString(str: String): Key {
            return Key(ByteArray(0)) //Key(str.toByteArray(java.nio.charset.Charset.forName("UTF-8")))
        }

        /**
         * Create a Key from a regular, unmodified, not encoded string.
         * @param str A plain string.
         * @param charset The charset to use.
         * @return A new Key.
         */
        fun fromPlainString(str: String, charset: String?): Key {
            return Key(ByteArray(0)) //Key(str.toByteArray(charset))
        }

        /**
         * Create a Key by supplying raw bytes. The byte
         * array should not be encoded and should be from a plain string,
         * UNLESS you know what you are doing and actively want
         * to provide a byte array that has been encoded.
         * @param bytes A byte array.
         * @return A new Key.
         */
        fun fromBytes(bytes: ByteArray): Key {
            return Key(bytes)
        }

        /**
         * Generate a random Key with a given size.
         * @param ls LazySodium instance as we need to get true
         * random bytes.
         * @param size The size of the key to generate.
         * @return A new Key.
         */
        /*fun generate(ls: LazySodium, size: Int): Key {
            return Key(ls.randomBytesBuf(size))
        }*/
    }
}
