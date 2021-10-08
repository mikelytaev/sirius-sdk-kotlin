package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.aries_rfc.Utils
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.errors.sirius_exceptions.SiriusValidationError
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

/**
 * Message to commit transactions list
 */
class PostCommitTransactionsMessage(msg: String) : BaseTransactionsMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return PostCommitTransactionsMessageBuilder()
        }


    }

    val commits: JSONArray
        get() {
            if (!getMessageObjec().has("commits")) getMessageObjec().put("commits", JSONArray())
            return getMessageObjec().optJSONArray("commits") ?: JSONArray()
        }

    fun addCommitSign(api: AbstractCrypto, commit: CommitTransactionsMessage, me: Pairwise.Me) {
        val signed: JSONObject = Utils.sign(api, commit.getMessageObjec(), me.verkey)
        val commits: JSONArray = commits
        commits.put(signed)
        getMessageObjec().put("commits", commits)
    }

    fun verifyCommits(api: AbstractCrypto, expected: CommitTransactionsMessage, verkeys: List<String>?): Boolean {
        val actualVerkeys: MutableList<String> = ArrayList<String>()
        for (o in commits) {
           val signer =  (o as JSONObject).getString("signer")
            signer?.let {
                actualVerkeys.add(signer)
            }

        }
        if (!HashSet<String>(actualVerkeys).containsAll(verkeys?: listOf())) {
            return false
        }
        for (signed in commits) {
            val (first, second) = Utils.verifySigned(api, signed as JSONObject)
            if (second) {
                val commit: JSONObject = JSONObject(first)
                val cleanedCommit: JSONObject = JSONObject()
                for (key in commit.keySet()) {
                    if (!key.startsWith("~")) cleanedCommit.put(key, commit.get(key))
                }
                val cleanedExpect: JSONObject = JSONObject()
                for (key in expected.getMessageObjec().keySet()) {
                    if (!key.startsWith("~")) cleanedExpect.put(key, expected.getMessageObjec().get(key))
                }
                if (!cleanedCommit.similar(cleanedExpect)) return false
            } else {
                return false
            }
        }
        return true
    }

    @Throws(SiriusValidationError::class)
    override fun validate() {
        super.validate()
        if (commits.isEmpty()) {
            throw SiriusValidationError("Commits collection is empty")
        }
    }

    abstract class Builder<B : Builder<B>> :
        BaseTransactionsMessage.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): PostCommitTransactionsMessage {
            return PostCommitTransactionsMessage(generateJSON().toString())
        }
    }

    private class PostCommitTransactionsMessageBuilder :
        Builder<PostCommitTransactionsMessageBuilder>() {
        protected override fun self(): PostCommitTransactionsMessageBuilder {
            return this
        }

        override fun getClass(): KClass<out Message> {
            return PostCommitTransactionsMessage::class
        }
    }
}
