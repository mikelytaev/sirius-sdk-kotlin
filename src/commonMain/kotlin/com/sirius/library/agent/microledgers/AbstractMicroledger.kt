package com.sirius.library.agent.microledgers

abstract class AbstractMicroledger {
    abstract fun name(): String?
    abstract fun size(): Int
    abstract fun uncommittedSize(): Int
    abstract fun rootHash(): String?
    abstract fun uncommittedRootHash(): String?
    abstract fun seqNo(): Int?
    abstract fun reload()
    abstract fun rename(newName: String)
    abstract fun init(genesis: List<Transaction?>?): List<Transaction?>?
    abstract fun append(transactions: List<Transaction?>?, txnTime: String?): Triple<Int, Int, List<Transaction>>
    fun append(transactions: List<Transaction?>?): Triple<Int, Int, List<Transaction>> {
        return append(transactions, null)
    }

    abstract fun commit(count: Int): Triple<Int?, Int?, List<Transaction?>?>?
    abstract fun discard(count: Int)
    abstract fun getMerkleInfo(seqNo: Int): MerkleInfo?
    abstract fun getAuditProof(seqNo: Int): AuditProof?
    abstract fun resetUncommitted()
    abstract fun getTransaction(seqNo: Int): Transaction?
    abstract fun getUncommittedTransaction(seqNo: Int): Transaction?
    abstract val lastTransaction: Transaction?
    abstract val lastCommittedTransaction: Transaction?
    abstract val allTransactions: List<Any?>?
    abstract val uncommittedTransactions: List<Any?>?
}
