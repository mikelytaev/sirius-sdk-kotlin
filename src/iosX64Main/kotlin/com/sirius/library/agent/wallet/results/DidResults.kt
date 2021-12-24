package com.sirius.library.agent.wallet.results

/**
 * did.rs results
 */
/**
 * did.rs results
 */
/**
 * Result classes for Did operations.
 */
class DidResults private constructor() {
    /**
     * Result from calling createAndStoreMyDid.
     */
    class CreateAndStoreMyDidResult internal constructor(
        /**
         * Gets the DID.
         *
         * @return The DID.
         */
        val did: String,
        /**
         * Gets the verification key.
         *
         * @return The verification key.
         */
        val verkey: String
    ) : Result() {


    }

    /**
     * Result from calling encrypt.
     */
    class EncryptResult internal constructor(encryptedMessage: ByteArray, nonce: ByteArray) :
        Result() {
        /**
         * Gets the encrypted message.
         *
         * @return The encrypted message.
         */
        val encryptedMessage: ByteArray

        /**
         * Gets the nonce.
         *
         * @return The nonce.
         */
        val nonce: ByteArray

        init {
            this.encryptedMessage = encryptedMessage
            this.nonce = nonce
        }
    }

    /**
     * Result from calling endpointForDid.
     */
    class EndpointForDidResult internal constructor(address: String, transportKey: String) :
        Result() {
        /**
         * Gets the Endpoint.
         *
         * @return The Endpoint.
         */
        val address: String

        /**
         * Gets the transport key.
         *
         * @return The transport key.
         */
        val transportKey: String

        init {
            this.address = address
            this.transportKey = transportKey
        }
    }
}
