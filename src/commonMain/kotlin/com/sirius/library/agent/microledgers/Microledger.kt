package com.sirius.library.agent.microledgers

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.errors.sirius_exceptions.SiriusContextError
import com.sirius.library.utils.JSONObject

class Microledger : AbstractMicroledger {
    var name: String
    var api: AgentRPC
    var state: JSONObject? = null

    constructor(name: String, api: AgentRPC, state: JSONObject?) {
        this.name = name
        this.api = api
        this.state = state
    }

    constructor(name: String, api: AgentRPC) {
        this.name = name
        this.api = api
    }

    fun assignTo(other: AbstractMicroledger?) {
        if (other is Microledger) {
            other.state = state
        }
    }


    override fun size(): Int {
        checkStateIsExists()
        return state?.getInt("size") ?: 0
    }

    override fun uncommittedSize(): Int {
        checkStateIsExists()
        return state?.getInt("uncommitted_size") ?: 0
    }

    override fun rootHash(): String? {
        checkStateIsExists()
        return state?.getString("root_hash")
    }

    override fun uncommittedRootHash(): String? {
        checkStateIsExists()
        return state?.getString("uncommitted_root_hash")
    }

    override fun seqNo(): Int? {
        checkStateIsExists()
        return state?.getInt("seqNo")
    }

    override fun reload() {
        val state: JSONObject? = object : RemoteCallWrapper<JSONObject?>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/state",
            RemoteParams.RemoteParamsBuilder.create().add("name", name)
        )
        this.state = state
    }

    fun rename(newName: String) {
        object : RemoteCallWrapper<Unit>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/rename",
            RemoteParams.RemoteParamsBuilder.create().add("name", name).add("new_name", newName)
        )
        name = newName
    }

    override fun init(genesis: List<Transaction?>?): List<Transaction?>? {
        val res: Pair<String, List<String>> =
            object : RemoteCallWrapper<Pair<String?, List<String?>>?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/initialize",
                RemoteParams.RemoteParamsBuilder.create().add("name", name).add("genesis_txns", genesis)
            )
        var txns: MutableList<Transaction>? = null
        if (res != null) {
            txns = ArrayList<Transaction>()
            state = JSONObject(res.first)
            for (txn in res.second) {
                txns.add(Transaction(JSONObject(txn)))
            }
        }
        return txns
    }

    override fun append(transactions: List<Transaction?>?, txnTime: String?): Triple<Int, Int, List<Transaction>> {
        val transactionsWithMetaStr: List<String> = object : RemoteCallWrapper<List<String?>?>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/append_txns_metadata",
            RemoteParams.RemoteParamsBuilder.create().add("name", name).add("txns", transactions)
                .add("txn_time", txnTime)
        )
        val transactionsWithMeta: MutableList<JSONObject> = ArrayList<JSONObject>()
        for (s in transactionsWithMetaStr) {
            transactionsWithMeta.add(JSONObject(s))
        }
        val appendTxnsRes: List<Any> = object : RemoteCallWrapper<List<Any?>?>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/append_txns",
            RemoteParams.RemoteParamsBuilder.create().add("name", name).add("txns", transactionsWithMeta)
        )
        state = JSONObject(appendTxnsRes[0].toString())
        val appendedTxns: MutableList<Transaction> = ArrayList<Transaction>()
        val appendedTxnsStr = appendTxnsRes[3] as List<String>
        for (s in appendedTxnsStr) {
            appendedTxns.add(Transaction(JSONObject(s)))
        }
        return Triple(appendTxnsRes[1] as Int, appendTxnsRes[2] as Int, appendedTxns)
    }

    override fun commit(count: Int): Triple<Int, Int, List<Transaction>> {
        val commitTxns: List<Any> = object : RemoteCallWrapper<List<Any?>?>(api) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/commit_txns",
            RemoteParams.RemoteParamsBuilder.create().add("name", name).add("count", count)
        )
        state = JSONObject(commitTxns[0].toString())
        val committedTxns: MutableList<Transaction> = ArrayList<Transaction>()
        val committedTxnsStr = commitTxns[3] as List<String>
        for (s in committedTxnsStr) {
            committedTxns.add(Transaction(JSONObject(s)))
        }
        return Triple(commitTxns[1] as Int, commitTxns[2] as Int, committedTxns)
    }

    override fun discard(count: Int) {
        state = JSONObject(
            object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/discard_txns",
                RemoteParams.RemoteParamsBuilder.create().add("name", name).add("count", count)
            )
        )
    }

    override fun getMerkleInfo(seqNo: Int): MerkleInfo {
        val merkleInfoJson: JSONObject = JSONObject(
            object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/merkle_info",
                RemoteParams.RemoteParamsBuilder.create().add("name", name).add("seqNo", seqNo)
            )
        )
        val auditPathJson: JSONArray = merkleInfoJson.getJSONArray("auditPath")
        val auditPath: MutableList<String?> = ArrayList<String>()
        for (o in auditPathJson) {
            auditPath.add(o as String)
        }
        return MerkleInfo(merkleInfoJson.getString("rootHash"), auditPath)
    }

    override fun getAuditProof(seqNo: Int): AuditProof {
        val merkleInfoJson: JSONObject = JSONObject(
            object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/audit_proof",
                RemoteParams.RemoteParamsBuilder.create().add("name", name).add("seqNo", seqNo)
            )
        )
        val auditPathJson: JSONArray = merkleInfoJson.getJSONArray("auditPath")
        val auditPath: MutableList<String?> = ArrayList<String>()
        for (o in auditPathJson) {
            auditPath.add(o as String)
        }
        return AuditProof(merkleInfoJson.getString("rootHash"), auditPath, merkleInfoJson.getInt("ledgerSize"))
    }

    override fun resetUncommitted() {
        state = JSONObject(
            object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/reset_uncommitted",
                RemoteParams.RemoteParamsBuilder.create().add("name", name)
            )
        )
    }

    override fun getTransaction(seqNo: Int): Transaction {
        val txn: JSONObject = JSONObject(
            object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/get_by_seq_no",
                RemoteParams.RemoteParamsBuilder.create().add("name", name).add("seqNo", seqNo)
            )
        )
        return Transaction(txn)
    }

    override fun getUncommittedTransaction(seqNo: Int): Transaction {
        val txn: JSONObject = JSONObject(
            object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/get_by_seq_no_uncommitted",
                RemoteParams.RemoteParamsBuilder.create().add("name", name).add("seqNo", seqNo)
            )
        )
        return Transaction(txn)
    }

    override val lastTransaction: Transaction
        get() {
            val txn: JSONObject = JSONObject(
                object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/get_last_txn",
                    RemoteParams.RemoteParamsBuilder.create().add("name", name)
                )
            )
            return Transaction(txn)
        }
    override val lastCommittedTransaction: Transaction
        get() {
            val txn: JSONObject = JSONObject(
                object : RemoteCallWrapper<String?>(api) {}.remoteCall(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/get_last_committed_txn",
                    RemoteParams.RemoteParamsBuilder.create().add("name", name)
                )
            )
            return Transaction(txn)
        }
    override val allTransactions: List<Transaction>
        get() {
            val txns: List<List<String?>> = object : RemoteCallWrapper<List<List<String?>?>?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/get_all_txns",
                RemoteParams.RemoteParamsBuilder.create().add("name", name)
            )
            val res: MutableList<Transaction> = ArrayList<Transaction>()
            for (s in txns) {
                res.add(Transaction(JSONObject(s[1])))
            }
            return res
        }
    override val uncommittedTransactions: List<Transaction>
        get() {
            val txns: List<String> = object : RemoteCallWrapper<List<String?>?>(api) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/microledgers/1.0/get_uncommitted_txns",
                RemoteParams.RemoteParamsBuilder.create().add("name", name)
            )
            val res: MutableList<Transaction> = ArrayList<Transaction>()
            for (s in txns) {
                res.add(Transaction(JSONObject(s)))
            }
            return res
        }

    private fun checkStateIsExists() {
        if (state == null) {
            throw SiriusContextError("Load state of Microledger at First!")
        }
    }
}
