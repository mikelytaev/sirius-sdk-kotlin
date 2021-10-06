package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.microledgers.Transaction
import com.sirius.library.errors.sirius_exceptions.SiriusValidationError
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

/**
 * Message to process transactions propose by Actor
 */
class ProposeTransactionsMessage(msg: String) : BaseTransactionsMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return ProposeTransactionsMessageBuilder()
        }

        init {
            Message.registerMessageClass(
                ProposeTransactionsMessage::class,
                SimpleConsensusMessage.PROTOCOL,
                "stage-propose"
            )
        }
    }

    val timeoutSec: Int?
        get() = getMessageObj().optInt("timeout_sec", null)

    @Throws(SiriusValidationError::class)
    override fun validate() {
        super.validate()
        val txns: List<Transaction>? = transactions()
        if (txns == null || txns.isEmpty()) {
            throw SiriusValidationError("Empty transactions list")
        }
        for (txn in txns) {
            if (!txn.hasMetadata()) throw SiriusValidationError("Transaction has no metadata")
        }
        if (this.state == null) {
            throw SiriusValidationError("Empty state")
        }
        if (this.hash.isNullOrEmpty()) {
            throw SiriusValidationError("Empty hash")
        }
    }

    abstract class Builder<B : Builder<B>> :
        BaseTransactionsMessage.Builder<B>() {
        var timeoutSec: Int? = null
        fun setTimeoutSec(timeoutSec: Int): B {
            this.timeoutSec = timeoutSec
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            if (timeoutSec != null) {
                jsonObject.put("timeout_sec", timeoutSec)
            }
            return jsonObject
        }

        fun build(): ProposeTransactionsMessage {
            return ProposeTransactionsMessage(generateJSON().toString())
        }
    }

    private class ProposeTransactionsMessageBuilder :
        Builder<ProposeTransactionsMessageBuilder>() {
        protected override fun self(): ProposeTransactionsMessageBuilder {
            return this
        }
    }
}
