package com.sirius.library.agent.microledgers

abstract class AbstractBatchedAPI {
    fun open(ledgers: List<AbstractMicroledger>): List<AbstractMicroledger> {
        val namesToOpen: MutableList<String> = ArrayList<String>()
        for (ledger in ledgers) {
            ledger.name()?.let {
                namesToOpen.add(it)
            }

        }
        return openByLedgerNames(namesToOpen)
    }

    abstract fun openByLedgerNames(ledgerNames: List<String>?): List<AbstractMicroledger>
    abstract fun close()
    abstract val states: List<Any?>?

    abstract fun append(transactions: List<Transaction?>?, txnTime: String?): List<AbstractMicroledger>
    fun append(transactions: List<Transaction?>?): List<AbstractMicroledger> {
        return append(transactions, null)
    }

    abstract fun commit(): List<AbstractMicroledger?>?
    abstract fun resetUncommitted(): List<AbstractMicroledger?>?
}
