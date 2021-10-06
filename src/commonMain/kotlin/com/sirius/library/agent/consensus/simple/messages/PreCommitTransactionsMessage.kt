package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.aries_rfc.Utils
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

/**
 * Message to accumulate participants signed accepts for transactions list
 */
class PreCommitTransactionsMessage(msg: String) : BaseTransactionsMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return PreCommitTransactionsMessageBuilder()
        }

        init {
            Message.registerMessageClass(
                PreCommitTransactionsMessage::class,
                SimpleConsensusMessage.PROTOCOL,
                "stage-pre-commit"
            )
        }
    }

    fun signState(api: AbstractCrypto, me: Pairwise.Me) {
        val signed: JSONObject = Utils.sign(api, hash, me.verkey)
        getMessageObj().put("hash~sig", signed)
        getMessageObj().remove("state")
    }

    fun verifyState(api: AbstractCrypto, expectedVerkey: String): Pair<Boolean, String?> {
        val hashSigned: JSONObject? = getMessageObj().optJSONObject("hash~sig")
        if (hashSigned != null) {
            if (hashSigned.optString("signer") == expectedVerkey) {
                val (first, second) = Utils.verifySigned(api, hashSigned)
                return Pair(second, first)
            }
        }
        return Pair(false, null)
    }

    abstract class Builder<B : Builder<B>> :
        BaseTransactionsMessage.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): PreCommitTransactionsMessage {
            return PreCommitTransactionsMessage(generateJSON().toString())
        }
    }

    private class PreCommitTransactionsMessageBuilder :
        Builder<PreCommitTransactionsMessageBuilder>() {
        protected override fun self(): PreCommitTransactionsMessageBuilder {
            return this
        }
    }
}
