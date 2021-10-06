package com.sirius.library.agent.microledgers

abstract class AbstractMicroledgerList {
    abstract fun create(name: String?, genesis: List<Transaction?>?): Pair<AbstractMicroledger?, List<Transaction?>?>?
    abstract fun getLedger(name: String?): AbstractMicroledger?
    abstract fun reset(name: String?)
    abstract fun isExists(name: String?): Boolean
    abstract fun leafHash(txn: Transaction?): ByteArray?
    abstract val list: List<Any?>?
    open val batched: AbstractBatchedAPI?
        get() = null
}
