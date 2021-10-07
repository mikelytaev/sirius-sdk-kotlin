package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.microledgers.Transaction
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

open class BaseTransactionsMessage(msg: String) : SimpleConsensusMessage(msg) {
    companion object {
        init {
            Message.registerMessageClass(BaseTransactionsMessage::class, SimpleConsensusMessage.PROTOCOL, "stage")
        }
    }

    fun transactions(): List<Transaction>? {
        val trArr: JSONArray? = getMessageObjec().optJSONArray("transactions")
        if (trArr != null) {
            val res: MutableList<Transaction> = ArrayList<Transaction>()
            for (o in trArr) {
                res.add(Transaction(o as JSONObject))
            }
            return res
        }
        return null
    }

    val state: MicroLedgerState?
        get() {
            val jsonState: JSONObject? = getMessageObjec().optJSONObject("state")
            if (jsonState != null) {
                val state = MicroLedgerState(jsonState)
                if (state.isFilled) return state
            }
            return null
        }
    val hash: String?
        get() = getMessageObjec().optString("hash", null)

    abstract class Builder<B : Builder<B>> protected constructor() :
        SimpleConsensusMessage.Builder<B>() {
        var transactions: List<Transaction>? = null
        var state: MicroLedgerState? = null
        fun setTransactions(transactions: List<Transaction>?): B {
            this.transactions = transactions
            return self()
        }

        fun setState(state: MicroLedgerState?): B {
            this.state = state
            return self()
        }

         override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            if (transactions != null) {
                val trArr: JSONArray = JSONArray()
                for (tr in transactions!!) {
                    trArr.put(tr)
                }
                jsonObject.put("transactions", trArr)
            }
            if (state != null) {
                jsonObject.put("state", state)
                jsonObject.put("hash", state!!.hash)
            }
            return jsonObject
        }
    }
}
