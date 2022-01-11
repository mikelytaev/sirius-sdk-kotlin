package com.sirius.library.agent.consensus.simple.messages

import com.ionspin.kotlin.crypto.util.LibsodiumUtil
import com.sirius.library.agent.microledgers.AbstractMicroledger
import com.sirius.library.mobile.utils.HashUtils
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.JSONUtils
import com.sirius.library.utils.StringUtils

class MicroLedgerState(obj: JSONObject) : JSONObject(obj.toString()) {
    val isFilled: Boolean
        get() = this.keySet().containsAll(
            listOf(
                "name",
                "seq_no",
                "size",
                "uncommitted_size",
                "root_hash",
                "uncommitted_root_hash"
            )
        )
    var name: String?
        get() = optString("name")
        set(name) {
            put("name", name)
        }
    var seqNo: Int?
        get() = optInt("seq_no")
        set(seqNo) {
            put("seq_no", seqNo)
        }
    var size: Int?
        get() = optInt("size")
        set(size) {
            put("size", size)
        }
    var uncommittedSize: Int?
        get() = optInt("uncommitted_size")
        set(uncommittedSize) {
            put("uncommitted_size", uncommittedSize)
        }
    var rootHash: String?
        get() = optString("root_hash")
        set(rootHash) {
            put("root_hash", rootHash)
        }
    var uncommittedRootHash: String?
        get() = optString("uncommitted_root_hash")
        set(uncommittedRootHash) {
            put("uncommitted_root_hash", uncommittedRootHash)
        }
    val hash: String?
        get() {
           try {
               val json = JSONUtils.JSONObjectToString(this, true)
                val bytes = StringUtils.stringToBytes(json, StringUtils.CODEC.UTF_8)
                val digest: ByteArray = HashUtils.hashMD5(bytes)
                return LibsodiumUtil.toHex(digest.toUByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    companion object {
        fun fromLedger(ledger: AbstractMicroledger): MicroLedgerState {
            return MicroLedgerState(
                JSONObject().put("name", ledger.name()).put("seq_no", ledger.seqNo())
                    .put("size", ledger.size()).put("uncommitted_size", ledger.uncommittedSize())
                    .put("root_hash", ledger.rootHash()).put("uncommitted_root_hash", ledger.uncommittedRootHash())
            )
        }
    }
}
