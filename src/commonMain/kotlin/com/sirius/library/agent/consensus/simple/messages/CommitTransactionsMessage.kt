package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.aries_rfc.Utils
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.errors.sirius_exceptions.SiriusContextError
import com.sirius.library.errors.sirius_exceptions.SiriusValidationError
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

/**
 * Message to commit transactions list
 */
class CommitTransactionsMessage(msg: String) : BaseTransactionsMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return CommitTransactionsMessageBuilder()
        }

        init {
            Message.registerMessageClass(
                CommitTransactionsMessage::class,
                SimpleConsensusMessage.PROTOCOL,
                "stage-commit"
            )
        }
    }

    val preCommits: JSONObject
        get() {
            val preCommits: JSONObject? = getMessageObj().optJSONObject("pre_commits")
            return if (preCommits != null) preCommits else JSONObject()
        }

    fun addPreCommit(participant: String, preCommit: PreCommitTransactionsMessage) {
        if (!preCommit.getMessageObj().has("hash~sig")) {
            SiriusContextError("Pre-Commit for participant" + participant + "does not have hash~sig attribute").printStackTrace()
            return
        }
        val preCommits: JSONObject = preCommits
        preCommits.put(participant, preCommit.getMessageObj().get("hash~sig"))
        getMessageObj().put("pre_commits", preCommits)
    }

    @Throws(SiriusValidationError::class)
    override fun validate() {
        super.validate()
        for (participant in this.participants) {
            if (!preCommits.has(participant)) {
                throw SiriusValidationError("Pre-Commit for participant" + participant + "does not exists")
            }
        }
    }

    @Throws(SiriusValidationError::class)
    fun verifyPreCommits(api: AbstractCrypto, expectedState: MicroLedgerState): JSONObject {
        val states: JSONObject = JSONObject()
        val preCommits: JSONObject = preCommits
        for (participant in preCommits.keySet()) {
            val signed: JSONObject = preCommits.optJSONObject(participant) ?: JSONObject()
            val (first, second) = Utils.verifySigned(api, signed)
            if (!second) {
                throw SiriusValidationError("Error verifying pre_commit for participant: $participant")
            }
            if (!first.equals(expectedState.hash)) {
                throw SiriusValidationError("Ledger state for participant " + participant + "is not consistent")
            }
            states.put(participant, JSONArray().put(expectedState).put(signed))
        }
        return states
    }

    abstract class Builder<B : Builder<B>> :
        BaseTransactionsMessage.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): CommitTransactionsMessage {
            return CommitTransactionsMessage(generateJSON().toString())
        }
    }

    private class CommitTransactionsMessageBuilder :
        Builder<CommitTransactionsMessageBuilder>() {
        protected override fun self(): CommitTransactionsMessageBuilder {
            return this
        }
    }
}
