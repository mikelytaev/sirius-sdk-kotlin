package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.aries_rfc.Utils.verifySigned
import com.sirius.library.agent.microledgers.Transaction
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.errors.sirius_exceptions.SiriusContextError
import com.sirius.library.errors.sirius_exceptions.SiriusValidationError
import com.sirius.library.messaging.Message
import com.sirius.library.utils.Base58
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

open class BaseInitLedgerMessage(msg: String) : SimpleConsensusMessage(msg) {
    companion object {
        init {
            Message.registerMessageClass(
                BaseInitLedgerMessage::class,
                SimpleConsensusMessage.PROTOCOL,
                "initialize"
            )
        }
    }

    fun ledgerHash(): JSONObject? {
        return getMessageObj().optJSONObject("ledger~hash")
    }

    val ledger: JSONObject
        get() = getMessageObj().optJSONObject("ledger")

    fun signatures(): JSONArray {
        if (!getMessageObj().has("signatures")) getMessageObj().put("signatures", JSONArray())
        return getMessageObj().optJSONArray("signatures")
    }

    @Throws(SiriusContextError::class, SiriusValidationError::class)
    fun checkSignatures(api: AbstractCrypto?, participant: String): JSONObject {
        if (ledgerHash() == null) {
            throw SiriusContextError("Ledger Hash description is empty")
        }
        val signatures: JSONArray
        if (participant.isEmpty()) {
            signatures = signatures()
        } else {
            signatures = JSONArray()
            for (s in signatures()) {
                if ((s as JSONObject).optString("participant") == participant) {
                    signatures.put(s)
                }
            }
        }
        if (signatures.isEmpty()) {
            throw SiriusContextError("Signatures list is empty!")
        }
        val response: JSONObject = JSONObject()
        for (o in signatures) {
            val item: JSONObject = o as JSONObject
            val (first, second) = verifySigned(api, item.optJSONObject("signature"))
            val signedLedgerHash: JSONObject = JSONObject(first)
            if (!second) {
                throw SiriusValidationError("Invalid Sign for participant: " + item.optString("participant"))
            }
            if (!signedLedgerHash.similar(ledgerHash())) {
                throw SiriusValidationError("NonConsistent Ledger hash for participant: " + item.optString("participant"))
            }
            response.put(item.optString("participant"), signedLedgerHash)
        }
        return response
    }

    @Throws(SiriusContextError::class, SiriusValidationError::class)
    fun checkSignatures(api: AbstractCrypto?): JSONObject {
        return checkSignatures(api, "")
    }

    abstract class Builder<B : Builder<B>> protected constructor() :
        SimpleConsensusMessage.Builder<B>() {
        var ledgerName: String? = null
        var rootHash: String? = null
        var genesis: List<Transaction>? = null
        fun setLedgerName(ledgerName: String?): B {
            this.ledgerName = ledgerName
            return self()
        }

        fun setRootHash(rootHash: String?): B {
            this.rootHash = rootHash
            return self()
        }

        fun setGenesis(genesis: List<Transaction>?): B {
            this.genesis = genesis
            return self()
        }

         override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            val ledger: JSONObject = JSONObject()
            if (ledgerName != null) {
                ledger.put("name", ledgerName)
            }
            if (rootHash != null) {
                ledger.put("root_hash", rootHash)
            }
            if (genesis != null) {
                val gArr: JSONArray = JSONArray()
                for (tr in genesis!!) {
                    gArr.put(tr)
                }
                ledger.put("genesis", gArr)
            }
            if (!ledger.isEmpty()) {
                jsonObject.put("ledger", ledger)
                try {
                    val digest: java.security.MessageDigest = java.security.MessageDigest.getInstance("SHA-256")
                    val base58: String = Base58.encode(digest.digest(Utils.serializeOrdering(ledger)))
                    jsonObject.put("ledger~hash", JSONObject().put("func", "sha256").put("base58", base58))
                } catch (e: java.security.NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
            }
            return jsonObject
        }
    }
}
