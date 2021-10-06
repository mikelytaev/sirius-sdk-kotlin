package com.sirius.library.encryption

import com.sirius.library.errors.sirius_exceptions.SiriusCryptoError
import com.sirius.library.utils.Base58
import com.sirius.library.utils.Base64

object Custom {
    /**
     * Convert a base 64 string to bytes.
     *
     * @param value   input base64 value
     * @param urlSafe flag if needed to convert to urlsafe presentation
     * @return bytes array
     */
    fun b64ToBytes(value: String, urlSafe: Boolean): ByteArray {
        //java.nio.charset.StandardCharsets.US_ASCII
        val valueBytes: ByteArray = value.encodeToByteArray() //THIS IS UTF_8
        /*   if isinstance(value, str):
        value = value.encode('ascii')
        if urlsafe:
        missing_padding = len(value) % 4
        if missing_padding:
        value += b'=' * (4 - missing_padding)
        return base64.urlsafe_b64decode(value)
        return base64.b64decode(value)
*/

        val encodedByte : ByteArray = if (urlSafe) {
            val missing_padding = valueBytes.size % 4
            Base64.getUrlDecoder().decode(valueBytes)
        } else {
           Base64.getDecoder().decode(valueBytes)
        }
        return encodedByte
    }

    /**
     * Convert a bytes to base 64 string.
     *
     * @param bytes   input bytes array
     * @param urlSafe flag if needed to convert to urlsafe presentation
     * @return base64 presentation
     */
    fun bytesToB64(bytes: ByteArray?, urlSafe: Boolean): String? {
        if (bytes == null) {
            return null
        }
        val decodedByte: ByteArray
        decodedByte = if (urlSafe) {
            Base64.getUrlEncoder().encode(bytes)
        } else {
            Base64.getEncoder().encode(bytes)
        }
       // String(decodedByte, java.nio.charset.StandardCharsets.US_ASCII)
            //THIS DECODE to UTF-8
        return decodedByte.decodeToString()
    }

    /**
     * Convert a base 58 string to bytes.
     *
     *
     * Small cache provided for key conversions which happen frequently in pack
     * and unpack and message handling.
     */
    fun b58ToBytes(value: String): ByteArray {
        return Base58.decode(value)
    }

    /**
     * Convert a byte string to base 58.
     * Small cache provided for key conversions which happen frequently in pack
     * and unpack and message handling.
     */
    fun bytesToB58(value: ByteArray): String {
        return Base58.encode(value)
    }

    /**
     * Create a public and private signing keypair from a seed value.
     *
     * @param seed (bytes) Seed for keypair
     * @return A tuple of (public key, secret key)
     */
    @Throws(SiriusCryptoError::class, SodiumException::class)
    fun createKeypair(seed: ByteArray?): KeyPair {
        //  Sodium.crypto_sign_seed_keypair()
        var seed = seed
        if (seed != null) {
            validateSeed(seed)
        } else {
            seed = randomSeed()
        }
        return LibSodium.getInstance().getLazySodium().cryptoSignSeedKeypair(seed)
    }

    /**
     * Generate a random seed value.
     *
     * @return A new random seed
     */
    fun randomSeed(): ByteArray {
        return LibSodium.getInstance().getLazySodium().randomBytesBuf(SecretBox.KEYBYTES)

        //   return new Random().randomBytes(Sodium.crypto_secretbox_keybytes());
    }

    /**
     * Convert a seed parameter to standard format and check length.
     *
     * @param message The seed to validate
     * @return The validated and encoded seed
     */
    @Throws(SiriusCryptoError::class)
    fun validateSeed(message: String?): ByteArray? {
        if (message == null) {
            return null
        }
        val bytes: ByteArray
        if (message.contains("=")) {
            bytes = b64ToBytes(message, false)
        } else {
            //java.nio.charset.StandardCharsets.US_ASCII
            bytes = message.encodeToByteArray() //THIS TO UTF-8
        }
        return validateSeed(bytes)
    }

    /**
     * Convert a seed parameter to standard format and check length.
     *
     * @param bytes The seed to validate
     * @return The validated and encoded seed
     */
    @Throws(SiriusCryptoError::class)
    fun validateSeed(bytes: ByteArray?): ByteArray? {
        if (bytes == null) {
            return null
        }
        if (bytes.size != 32) {
            throw SiriusCryptoError("Seed value must be 32 bytes in length")
        }
        return bytes
    }

    /**
     * Sign a message using a private signing key.
     * @param message The message to sign
     * @param secret The private signing key
     * @return The signature
     */
    fun signMessage(message: ByteArray, secret: ByteArray?): ByteArray? {
        val signedMessage = ByteArray(Sign.BYTES + message.size)
        return if (LibSodium.getInstance().getLazySodium().cryptoSign(signedMessage, message, message.size, secret)) {
            java.util.Arrays.copyOfRange(signedMessage, 0, Sign.BYTES)
        } else null
    }

    /**
     * Verify a signed message according to a public verification key.
     * @param verkey The verkey to use in verification
     * @param message original message
     * @param signature signature
     * @return
     */
    fun verifySignedMessage(verkey: ByteArray?, message: ByteArray?, signature: ByteArray?): Boolean {
        val signedMessage: ByteArray = ArrayUtils.addAll(signature, message)
        return LibSodium.getInstance().getLazySodium()
            .cryptoSignOpen(message, signedMessage, signedMessage.size, verkey)
    }

    fun didFromVerkey(verkey: ByteArray?): ByteArray {
        return java.util.Arrays.copyOfRange(verkey, 0, 16)
    }
}
