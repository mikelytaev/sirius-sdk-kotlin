package com.sirius.library.agent.pairwise

abstract class AbstractPairwiseList {
    abstract fun create(pairwise: Pairwise)
    abstract fun update(pairwise: Pairwise)
    abstract fun isExists(theirDid: String): Boolean
    abstract fun ensureExists(pairwise: Pairwise)
    abstract fun loadForDid(theirDid: String): Pairwise?
    abstract fun loadForVerkey(theirVerkey: String): Pairwise?
}