package com.sirius.library.agent.aries_rfc

import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.utils.Base64
import com.sirius.library.utils.Date
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.StringCodec

object Utils {
    fun utcToStr(date: Date?): String {
        //    dt.strftime('%Y-%m-%dT%H:%M:%S') + '+0000'
        return ""
    }

    fun sign(crypto: AbstractCrypto, value: Any?, verkey: String?, excludeSigData: Boolean): JSONObject {
        val codec = StringCodec()
        val timestampBytes: ByteArray =
            java.nio.ByteBuffer.allocate(8).putLong(Date().time / 1000).array()

        val sigDataBytes: ByteArray =
            ArrayUtils.addAll(timestampBytes,codec .fromASCIIStringToByteArray(value.toString()))
        val sigSata =codec.fromByteArrayToASCIIString(Base64.getUrlEncoder().encode(sigDataBytes))
        val signatureBytes: ByteArray? = crypto.cryptoSign(verkey, sigDataBytes)
        val signature = codec.fromByteArrayToASCIIString(Base64.getUrlEncoder().encode(signatureBytes))
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
        val codec = StringCodec()

        val signatureBytes: ByteArray = Base64.getUrlDecoder()
            .decode(codec.fromASCIIStringToByteArray(signed.optString("signature")))
        val sigDataBytes: ByteArray = Base64.getUrlDecoder()
            .decode(codec.fromASCIIStringToByteArray(signed.optString("sig_data")))
        val sigVerified: Boolean = crypto.cryptoVerify(signed.optString("signer"), sigDataBytes, signatureBytes)
        val dataBytes: ByteArray = Base64.getUrlDecoder().decode(signed.optString("sig_data")?:"")
        val field = codec.fromByteArrayToASCIIString(java.util.Arrays.copyOfRange(dataBytes, 8, dataBytes.size))
        return Pair(field, sigVerified)
    }
}
