package com.sirius.library.agent.wallet.abstract_wallet

abstract class AbstractDID {
    /**
     * Creates keys (signing and encryption keys) for a new
     * DID (owned by the caller of the library).
     * Identity's DID must be either explicitly provided, or taken as the first 16 bit of verkey.
     * Saves the Identity DID with keys in a secured Wallet, so that it can be used to sign
     * and encrypt transactions.
     *
     * @param did  string, (optional)
     * if not provided and cid param is false then the first 16 bit of the verkey will be
     * used as a new DID;
     * if not provided and cid is true then the full verkey will be used as a new DID;
     * if provided, then keys will be replaced - key rotation use case)
     * @param seed string, (optional) Seed that allows deterministic key creation
     * (if not set random one will be created).
     * Can be UTF-8, base64 or hex string.
     * @param cid  bool, (optional; if not set then false is used;)
     * @return DID and verkey (for verification of signature)
     */
    abstract fun createAndStoreMyDid(did: String?, seed: String?, cid: Boolean?): Pair<String, String>

    /**
     * Overload method [.createAndStoreMyDid]
     */
    fun createAndStoreMyDid(did: String?, seed: String?): Pair<String, String> {
        return createAndStoreMyDid(did, seed, null)
    }

    /**
     * Overload method [.createAndStoreMyDid]
     */
    fun createAndStoreMyDid(did: String?): Pair<String, String> {
        return createAndStoreMyDid(did, null, null)
    }

    /**
     * Overload method [.createAndStoreMyDid]
     */
    fun createAndStoreMyDid(): Pair<String, String> {
        return createAndStoreMyDid(null, null, null)
    }

    /**
     * Saves their DID for a pairwise connection in a secured Wallet,
     * so that it can be used to verify transaction.
     * Updates DID associated verkey in case DID already exists in the Wallet.
     *
     * @param did    string, (required)
     * @param verkey string (optional, if only pk is provided),
     */
    abstract fun storeTheirDid(did: String?, verkey: String?)

    /**
     * Overload method [.storeTheirDid]
     */
    fun storeTheirDid(did: String?) {
        storeTheirDid(did, null)
    }

    /**
     * Saves/replaces the meta information for the giving DID in the wallet.
     *
     * @param did      the DID to store metadata.
     * @param metadata the meta information that will be store with the DID.
     * @return: Error code
     */
    abstract fun setDidMetadata(did: String?, metadata: String?)

    /**
     * Overload method [.setDidMetadata]
     */
    fun setDidMetadata(did: String?) {
        setDidMetadata(did, null)
    }

    /**
     * List DIDs and metadata stored in the wallet.
     *
     * @return List of DIDs with verkeys and meta data.
     */
    abstract fun listMyDidsWithMeta(): List<Any?>?

    /**
     * Retrieves the meta information for the giving DID in the wallet.
     *
     * @param did The DID to retrieve metadata.
     * @return The meta information stored with the DID; Can be null if no metadata was saved for this DID.
     */
    abstract fun getDidMetadata(did: String?): String?

    /**
     * Returns ver key (key id) for the given DID.
     *
     *
     * "key_for_local_did" call looks data stored in the local wallet only and skips freshness checking.
     *
     *
     * Note if you want to get fresh data from the ledger you can use "key_for_did" call
     * instead.
     *
     *
     * Note that "create_and_store_my_did" makes similar wallet record as "create_key".
     * As result we can use returned ver key in all generic crypto and messaging functions.
     *
     * @param did The DID to resolve key.
     * @return The DIDs ver key (key id).
     */
    abstract fun keyForLocalDid(did: String?): String?

    /**
     * Returns ver key (key id) for the given DID.
     *
     *
     * "key_for_did" call follow the idea that we resolve information about their DID from
     * the ledger with cache in the local wallet. The "open_wallet" call has freshness parameter
     * that is used for checking the freshness of cached pool value.
     *
     *
     * Note if you don't want to resolve their DID info from the ledger you can use
     * "key_for_local_did" call instead that will look only to local wallet and skip
     * freshness checking.
     *
     *
     * Note that "create_and_store_my_did" makes similar wallet record as "create_key".
     * As result we can use returned ver key in all generic crypto and messaging functions.
     *
     * @param poolName Pool Name.
     * @param did      The DID to resolve key.
     * @return The DIDs ver key (key id).
     */
    abstract fun keyForDid(poolName: String?, did: String?): String?

    /**
     * Creates keys pair and stores in the wallet.
     *
     * @param seed string, (optional) Seed that allows deterministic key creation
     * (if not set random one will be created).
     * Can be UTF-8, base64 or hex string.
     * @return Ver key of generated key pair, also used as key identifier
     */
    abstract fun createKey(seed: String?): String?

    /**
     * Overload method [.createKey]
     */
    fun createKey(): String? {
        return createKey(null)
    }

    /**
     * Generated new keys (signing and encryption keys) for an existing
     * DID (owned by the caller of the library).
     *
     * @param did  signing DID
     * @param seed string, (optional) Seed that allows deterministic key creation
     * (if not set random one will be created). Can be UTF-8, base64 or hex string.
     * @return
     */
    abstract fun replaceKeysStart(did: String?, seed: String?): String?

    /**
     * Overload method [.replaceKeysStart]
     */
    fun replaceKeysStart(did: String?): String? {
        return replaceKeysStart(did, null)
    }

    /**
     * Apply temporary keys as main for an existing DID (owned by the caller of the library).
     *
     * @param did The DID to resolve key.
     * @return: Error code
     */
    abstract fun replaceKeysApply(did: String?)

    /**
     * Creates keys pair and stores in the wallet.
     *
     * @param verkey   the key (verkey, key id) to store metadata.
     * @param metadata the meta information that will be store with the key.
     * @return: Error code
     */
    abstract fun setKeyMetadata(verkey: String?, metadata: String?)

    /**
     * Retrieves the meta information for the giving key in the wallet.
     *
     * @param verkey The key (verkey, key id) to retrieve metadata.
     * @return metadata: The meta information stored with the key; Can be null if no metadata was saved for this key.
     */
    abstract fun getKeyMetadata(verkey: String?): String?

    /**
     * Set/replaces endpoint information for the given DID.
     *
     * @param did          The DID to resolve endpoint.
     * @param address      The DIDs endpoint address.
     * @param transportKey The DIDs transport key (ver key, key id).
     * @return: Error code
     */
    abstract fun setEndpointForDid(did: String?, address: String?, transportKey: String?)

    /**
     * Returns endpoint information for the given DID.
     *
     * @param pooName Pool name.
     * @param did     The DID to resolve endpoint.
     * @return (endpoint, transport_vk)
     */
    abstract fun getEndpointForDid(pooName: String?, did: String?): Pair<String?, String?>?

    /**
     * Get DID metadata and verkey stored in the wallet.
     *
     * @param did The DID to retrieve metadata.
     * @return DID with verkey and metadata.
     */
    abstract fun getMyDidMeta(did: String?): Any?

    /**
     * Retrieves abbreviated verkey if it is possible otherwise return full verkey.
     *
     * @param did        The DID.
     * @param fullVerkey The DIDs verification key,
     * @return Either abbreviated or full verkey.
     */
    abstract fun abbreviateVerKey(did: String?, fullVerkey: String?): String?

    /**
     * Update DID stored in the wallet to make fully qualified, or to do other DID maintenance.
     * - If the DID has no prefix, a prefix will be appended (prepend did:peer to a legacy did)
     * - If the DID has a prefix, a prefix will be updated (migrate did:peer to did:peer-new)
     * Update DID related entities stored in the wallet.
     *
     * @param did    target DID stored in the wallet.
     * @param method method to apply to the DID.
     * @return fully qualified did
     */
    abstract fun qualifyDid(did: String?, method: String?): String?
}

