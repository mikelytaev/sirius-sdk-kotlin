package com.sirius.library.mobile.utils

import java.math.BigInteger
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.experimental.or
import kotlin.experimental.xor

object HashUtils {
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateStorngPasswordHash(password: String): String {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt = salt
        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        return iterations.toString() + ":" + toHex(salt) + ":" + toHex(hash)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateHash(password: String, saltStr: String): String {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt = saltStr.toByteArray()
        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        return iterations.toString() + ":" + toHex(salt) + ":" + toHex(hash)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateHashWithoutStoredSalt(password: String, saltStr: String): String {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt = saltStr.toByteArray()
        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        return toHex(hash)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateHash(password: String): String {
        val iterations = 1000
        val saltStr = "ABC"
        val salt = saltStr.toByteArray()
        val chars = password.toCharArray()
        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        return toHex(hash)
    }

    @get:Throws(NoSuchAlgorithmException::class)
    private val salt: ByteArray
        private get() {
            val sr = SecureRandom.getInstance("SHA1PRNG")
            val salt = ByteArray(16)
            sr.nextBytes(salt)
            return salt
        }

    @Throws(NoSuchAlgorithmException::class)
    private fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0) {
            String.format("%0" + paddingLength + "d", 0) + hex
        } else {
            hex
        }
    }

    fun validatePassword(originalPassword: String, storedPassword: String): Boolean {
        try {
            val parts = storedPassword.split(":").toTypedArray()
            val iterations = parts[0].toInt()
            val salt = fromHex(parts[1])
            val hash = fromHex(parts[2])
            val spec = PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.size * 8)
            var skf: SecretKeyFactory? = null
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val testHash = skf.generateSecret(spec).encoded
            var diff = hash.size.toByte() xor testHash.size.toByte()
            var i = 0
            while (i < hash.size && i < testHash.size) {
                diff = diff or (hash[i] xor testHash[i])
                i++
            }
            return diff == 0.toByte()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        }
        return false
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun fromHex(hex: String): ByteArray {
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            bytes[i] = hex.substring(2 * i, 2 * i + 2).toInt(16).toByte()
        }
        return bytes
    }
}
