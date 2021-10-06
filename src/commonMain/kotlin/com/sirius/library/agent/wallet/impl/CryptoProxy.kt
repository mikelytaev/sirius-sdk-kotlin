package com.sirius.library.agent.wallet.impl

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto

class CryptoProxy(rpc: AgentRPC) : AbstractCrypto() {
    var rpc: AgentRPC
    override  fun createKey(seed: String?, cryptoType: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/create_key",
            RemoteParams.RemoteParamsBuilder.create()
                .add("seed", seed)
                .add("crypto_type", cryptoType)
        )
    }

    override fun setKeyMetadata(verkey: String?, metadata: String?) {
        object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/set_key_metadata",
            RemoteParams.RemoteParamsBuilder.create()
                .add("verkey", verkey).add("metadata", metadata)
        )
    }

    override fun getKeyMetadata(verkey: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_key_metadata",
            RemoteParams.RemoteParamsBuilder.create()
                .add("verkey", verkey)
        )
    }

    override fun cryptoSign(signerVk: String?, msg: ByteArray?): ByteArray? {
        return object : RemoteCallWrapper<ByteArray?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/crypto_sign",
            RemoteParams.RemoteParamsBuilder.create()
                .add("signer_vk", signerVk)
                .add("msg", msg)
        )
    }

    override fun cryptoVerify(signerVk: String?, msg: ByteArray?, signature: ByteArray?): Boolean {
        return object : RemoteCallWrapper<Boolean?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/crypto_verify",
            RemoteParams.RemoteParamsBuilder.create()
                .add("signer_vk", signerVk)
                .add("msg", msg)
                .add("signature", signature)
        ) ?: false
    }

    override fun anonCrypt(recipentVk: String?, msg: ByteArray?): ByteArray? {
        return object : RemoteCallWrapper<ByteArray?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/anon_crypt",
            RemoteParams.RemoteParamsBuilder.create()
                .add("recipient_vk", recipentVk).add("msg", msg)
        )
    }

    override fun anonDecrypt(recipientVk: String?, encryptedMsg: ByteArray?): ByteArray? {
        return object : RemoteCallWrapper<ByteArray?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/anon_decrypt",
            RemoteParams.RemoteParamsBuilder.create()
                .add("recipient_vk", recipientVk).add("encrypted_msg", encryptedMsg)
        )
    }

    override fun packMessage(message: Any?, recipentVerkeys: List<String?>?, senderVerkey: String?): ByteArray? {
        return object : RemoteCallWrapper<ByteArray?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/pack_message",
            RemoteParams.RemoteParamsBuilder.create()
                .add("message", message)
                .add("recipient_verkeys", recipentVerkeys)
                .add("sender_verkey", senderVerkey)
        )
    }

    override fun unpackMessage(jwe: ByteArray?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/unpack_message",
            RemoteParams.RemoteParamsBuilder.create()
                .add("jwe", jwe)
        )
    }

    init {
        this.rpc = rpc
    }
}
