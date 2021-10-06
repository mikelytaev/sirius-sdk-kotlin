package com.sirius.library.agent.wallet.abstract_wallet

import kotlin.jvm.JvmOverloads

abstract class AbstractCrypto {
    /**
     * Creates keys pair and stores in the wallet.
     *
     * @param seed       string, (optional) Seed that allows deterministic key creation (if not set random one will be
     * created). Can be UTF-8, base64 or hex string.
     * @param cryptoType string, // Optional (if not set then ed25519 curve is used);
     * Currently only 'ed25519' value is supported for this field.
     * @return verkey: Ver key of generated key pair, also used as key identifier
     */
    abstract fun createKey(seed: String?, cryptoType: String?): String?
    /**
     * Overload method [.createKey]
     */
    /**
     * Overload method [.createKey]
     */
    @JvmOverloads
    fun createKey(seed: String? = null): String? {
        return createKey(seed, null)
    }

    /**
     * Saves/replaces the meta information for the giving key in the wallet.
     *
     * @param verkey   the key (verkey, key id) to store metadata.
     * @param metadata the meta information that will be store with the key.
     */
    abstract fun setKeyMetadata(verkey: String?, metadata: String?)

    /**
     * Retrieves the meta information for the giving key in the wallet.
     *
     * @param verkey The key (verkey, key id) to retrieve metadata.
     * @return The meta information stored with the key; Can be null if no metadata was saved for this key.
     */
    abstract fun getKeyMetadata(verkey: String?): String?

    /**
     * Signs a message with a key.
     *
     *
     * Note to use DID keys with this function you can call indy_key_for_did to get key id (verkey) for specific DID.
     *
     * @param signerVk id (verkey) of my key. The key must be created by calling create_key or create_and_store_my_did
     * @param msg      a message to be signed
     * @return a signature string
     */
    abstract fun cryptoSign(signerVk: String?, msg: ByteArray?): ByteArray?

    /**
     * Verify a signature with a verkey.
     *
     *
     * Note to use DID keys with this function you can call key_for_did to get key id (verkey) for specific DID.
     *
     * @param signerVk  verkey of signer of the message
     * @param msg       message that has been signed
     * @param signature a signature to be verified
     * @return valid: true - if signature is valid, false - otherwise
     */
    abstract fun cryptoVerify(signerVk: String?, msg: ByteArray?, signature: ByteArray?): Boolean

    /**
     * Encrypts a message by anonymous-encryption scheme.
     *
     *
     * Sealed boxes are designed to anonymously send messages to a Recipient given its public key.
     * Only the Recipient can decrypt these messages, using its private key.
     * While the Recipient can verify the integrity of the message, it cannot verify the identity of the Sender.
     *
     *
     * Note to use DID keys with this function you can call key_for_did to get key id (verkey)
     * for specific DID.
     * Note: use pack_message function for A2A goals.
     *
     * @param recipentVk verkey of message recipient
     * @param msg        a message to be signed
     * @return an encrypted message as an array of bytes
     */
    abstract fun anonCrypt(recipentVk: String?, msg: ByteArray?): ByteArray?

    /**
     * Decrypts a message by anonymous-encryption scheme.
     *
     *
     * Sealed boxes are designed to anonymously send messages to a Recipient given its public key.
     * Only the Recipient can decrypt these messages, using its private key.
     * While the Recipient can verify the integrity of the message, it cannot verify the identity of the Sender.
     *
     *
     * Note to use DID keys with this function you can call key_for_did to get key id (verkey)
     * for specific DID.
     *
     *
     * Note: use unpack_message function for A2A goals.
     *
     * @param recipientVk  id (verkey) of my key. The key must be created by calling indy_create_key or create_and_store_my_did
     * @param encryptedMsg : encrypted message
     * @return decrypted message as an array of bytes
     */
    abstract fun anonDecrypt(recipientVk: String?, encryptedMsg: ByteArray?): ByteArray?

    /**
     * Packs a message by encrypting the message and serializes it in a JWE-like format (Experimental)
     * Note to use DID keys with this function you can call did.key_for_did to get key id (verkey)
     * for specific DID.
     *
     * @param message         the message being sent as a string. If it's JSON formatted it should be converted to a string
     * @param recipentVerkeys a list of Strings which are recipient verkeys
     * @param senderVerkey    the sender's verkey as a string. -> When None is passed in this parameter, anoncrypt mode is used
     * @return an Agent Wire Message format as a byte array.
     */
    abstract fun packMessage(message: Any?, recipentVerkeys: List<String?>?, senderVerkey: String?): ByteArray?

    /**
     * Overload method [.]
     */
    fun packMessage(message: Any?, recipentVerkeys: List<String?>?): ByteArray? {
        return packMessage(message, recipentVerkeys, null)
    }

    /**
     * Unpacks a JWE-like formatted message outputted by pack_message (Experimental)
     *
     * @param jwe packed message
     * @return (Authcrypt mode)
     *
     *
     * {
     * "message": <decrypted message>,
     * "recipient_verkey": <recipient verkey used to decrypt>,
     * "sender_verkey": <sender verkey used to encrypt>
     * }
    </sender></recipient></decrypted> *
     *
     * (Anoncrypt mode)
     *
     *
     * {
     * "message": <decrypted message>,
     * "recipient_verkey": <recipient verkey used to decrypt>,
     * }
    </recipient></decrypted> */
    abstract fun unpackMessage(jwe: ByteArray?): String?
}
