package com.sirius.library.encryption

import com.sirius.library.errors.sirius_exceptions.SiriusCryptoError
import com.sirius.library.errors.sirius_exceptions.SiriusInvalidType

/**
 * Pairwise static connection compatible with Indy SDK
 */
class P2PConnection(var fromVerkey: String, var fromSigKey: String, var theirVerKey: String) {
    var ed25519: Ed25519

    /**
     * Encrypt message
     *
     * @param message
     * @return encrypted message
     */
    fun pack(message: String): String? {
        val toVerKeys: MutableList<String> = ArrayList<String>()
        toVerKeys.add(theirVerKey)
        try {
            return ed25519.packMessage(message, toVerKeys, fromVerkey, fromSigKey)
        } catch (siriusCryptoError: SiriusCryptoError) {
            siriusCryptoError.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Decrypt message
     *
     * @param encMessage   encoded message
     * @return decrypted message
     */
    fun unpack(encMessage: String?): String? {
        try {
            val unpackModel: UnpackModel = ed25519.unpackMessage(encMessage, fromVerkey, fromSigKey)
            return unpackModel.message
        } catch (siriusInvalidType: SiriusInvalidType) {
            siriusInvalidType.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * @param fromVerkey  verkey for encrypt/decrypt operations
     * @param fromSigKey  sigkey for encrypt/decrypt operations
     * @param theirVerKey their_verkey: verkey of the counterparty
     */
    init {
        ed25519 = Ed25519()
    }
}
