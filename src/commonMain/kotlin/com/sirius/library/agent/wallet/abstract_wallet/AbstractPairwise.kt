package com.sirius.library.agent.wallet.abstract_wallet

import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmOverloads

abstract class AbstractPairwise {
    /**
     * Check if pairwise is exists.
     * @param theirDid encoded Did.
     * @return true - if pairwise is exists, false - otherwise
     */
    abstract fun isPairwiseExist(theirDid: String?): Boolean

    /**
     *
     * @param theirDid encrypting DID
     * @param myDid encrypting DID
     * @param metadata (Optional) extra information for pairwise
     * @param tags: tags for searching operations
     * @return  Error code
     */
    abstract fun createPairwise(theirDid: String?, myDid: String?, metadata: JSONObject?, tags: JSONObject?)
    /**
     * Overload method [.createPairwise]
     */
    /**
     * Overload method [.createPairwise]
     */
    @JvmOverloads
    fun createPairwise(theirDid: String?, myDid: String?, metadata: JSONObject? = null) {
        createPairwise(theirDid, myDid, metadata, null)
    }

    /**
     * Get list of saved pairwise.
     * @return pairwise_list: list of saved pairwise
     */
    abstract fun listPairwise(): List<Any?>?

    /**
     * Gets pairwise information for specific their_did.
     * @param thierDid: encoded Did
     * @return
     */
    abstract fun getPairwise(thierDid: String?): String?

    /**
     * Save some data in the Wallet for pairwise associated with Did.
     * @param theirDid  encoded DID
     * @param metadata some extra information for pairwise
     * @param tags tags for searching operation
     */
    abstract fun setPairwiseMetadata(theirDid: String?, metadata: JSONObject?, tags: JSONObject?)

    /**
     * Overload method [.setPairwiseMetadata]
     */
    fun setPairwiseMetadata(theirDid: String?, metadata: JSONObject?) {
        setPairwiseMetadata(theirDid, metadata, null)
    }

    /**
     * Overload method [.setPairwiseMetadata]
     */
    fun setPairwiseMetadata(theirDid: String?) {
        setPairwiseMetadata(theirDid, null)
    }

    /**
     * Search Pairwises
     * @param tags tags based query
     * @param limit limit: max items count
     * @return Results, TotalCount
     */
    abstract fun search(tags: JSONObject?, limit: Int?): Pair<List<String>, Int>

    /**
     * Overload method [.search]
     */
    fun search(tags: JSONObject?): Pair<List<String>, Int> {
        return search(tags, null)
    }
}


