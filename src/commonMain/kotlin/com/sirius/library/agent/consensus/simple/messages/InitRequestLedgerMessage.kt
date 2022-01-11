package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.aries_rfc.Utils
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.errors.sirius_exceptions.SiriusContextError
import com.sirius.library.errors.sirius_exceptions.SiriusValidationError
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

open class InitRequestLedgerMessage(msg: String) : BaseInitLedgerMessage(msg) {
    companion object {
        fun builder(): Builder<*> {
            return InitRequestLedgerMessageBuilder()
        }


    }

    @Throws(SiriusContextError::class)
    fun addSignature(api: AbstractCrypto?, me: Pairwise.Me) {
        if (!this.participants.contains(me.did)) {
            throw SiriusContextError("Signer must be a participant")
        }
        if (ledgerHash() != null) {
            val hashSignature: JSONObject = Utils.sign(api!!, ledgerHash().toString(), me.verkey)
            val signatures: JSONArray = signatures()
            for (i in signatures.length() - 1 downTo 0) {
                if (signatures.getJSONObject(i)?.optString("participant") == me.did) {
                    signatures.remove(i)
                }
            }
            signatures.put(JSONObject().put("participant", me.did).put("signature", hashSignature))
            getMessageObjec().put("signatures", signatures)
        } else {
            throw SiriusContextError("Ledger Hash description is empty")
        }
    }

    @Throws(SiriusContextError::class)
    fun checkLedgerHash() {
        if (ledgerHash() == null) throw SiriusContextError("Ledger hash is empty")
        if (this.ledger != null) throw SiriusContextError("Ledger body is empty")
    }

    val timeoutSec: Int?
        get() = getMessageObjec().optInt("timeout_sec")

    @Throws(SiriusValidationError::class)
    override fun validate() {
        super.validate()
        if (this.ledger == null) throw SiriusValidationError("Ledger body is empty")
        if (!this.ledger.keySet()
                .containsAll(listOf("root_hash", "name", "genesis"))
        ) throw SiriusValidationError("Expected field does not exists in Ledger container")
        if (ledgerHash() == null) throw SiriusValidationError("Ledger hash is empty")
        if (!ledgerHash()!!.keySet()
                .containsAll(listOf("func", "base58"))
        ) throw SiriusValidationError("Expected field does not exists in Ledger Hash")
    }

    abstract class Builder<B : Builder<B>> :
        BaseInitLedgerMessage.Builder<B>() {
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

       open fun build(): InitRequestLedgerMessage {
           val json = generateJSON()
          val string =  json.toString()
           println("json=$json")
           println("string=$string")
            return InitRequestLedgerMessage(string)
        }
    }

    private class InitRequestLedgerMessageBuilder :
        Builder<InitRequestLedgerMessageBuilder>() {
         override fun self(): InitRequestLedgerMessageBuilder {
            return this
        }
        override fun getClass(): KClass<out Message> {
            return InitRequestLedgerMessage::class
        }
    }
}
