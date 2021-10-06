package com.sirius.library.agent.aries_rfc

import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.utils.Base64
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonObject

object Utils {
    fun utcToStr(date: java.util.Date?): String {
        //    dt.strftime('%Y-%m-%dT%H:%M:%S') + '+0000'
        return ""
    }

    fun sign(crypto: AbstractCrypto, value: Any?, verkey: String?, excludeSigData: Boolean): JSONObject {
        val timestampBytes: ByteArray =
            java.nio.ByteBuffer.allocate(8).putLong(java.lang.System.currentTimeMillis() / 1000).array()
        val sigDataBytes: ByteArray =
            ArrayUtils.addAll(timestampBytes, value.toString().toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
        val sigSata =
            String(Base64.getUrlEncoder().encode(sigDataBytes), java.nio.charset.StandardCharsets.US_ASCII)
        val signatureBytes: ByteArray = crypto.cryptoSign(verkey, sigDataBytes)
        val signature =
            String(Base64.getUrlEncoder().encode(signatureBytes), java.nio.charset.StandardCharsets.US_ASCII)
        val data: JSONObject =
            JSONObject().put("@type", "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/signature/1.0/ed25519Sha512_single")
                .put("signer", verkey).put("signature", signature)
        if (!excludeSigData) {
            data.put("sig_data", sigSata)
        }
        return data
    }

    fun sign(crypto: AbstractCrypto, value: Any?, verkey: String?): JSONObject {
        return sign(crypto, value, verkey, false)
    }

    fun verifySigned(crypto: AbstractCrypto, signed: JSONObject): Pair<String, Boolean> {
        val signatureBytes: ByteArray = Base64.getUrlDecoder()
            .decode(signed.optString("signature").toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
        val sigDataBytes: ByteArray = java.util.Base64.getUrlDecoder()
            .decode(signed.optString("sig_data").toByteArray(java.nio.charset.StandardCharsets.US_ASCII))
        val sigVerified: Boolean = crypto.cryptoVerify(signed.optString("signer"), sigDataBytes, signatureBytes)
        val dataBytes: ByteArray = java.util.Base64.getUrlDecoder().decode(signed.optString("sig_data"))
        val field = String(
            java.util.Arrays.copyOfRange(dataBytes, 8, dataBytes.size),
            java.nio.charset.StandardCharsets.US_ASCII
        )
        return Pair(field, sigVerified)
    }
}
