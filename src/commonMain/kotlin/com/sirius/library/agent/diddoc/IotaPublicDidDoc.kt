package com.sirius.sdk.agent.diddoc

import com.danubetech.keyformats.crypto.ByteSigner
import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCrypto
import com.sirius.library.encryption.IndyWalletSigner
import com.sirius.library.hub.Context
import com.sirius.library.naclJava.LibSodium
import foundation.identity.jsonld.JsonLDObject
import info.weboftrust.ldsignatures.signer.JcsEd25519Signature2020LdSigner
import info.weboftrust.ldsignatures.signer.LdSigner
import info.weboftrust.ldsignatures.suites.JcsEd25519Signature2020SignatureSuite
import info.weboftrust.ldsignatures.verifier.JcsEd25519Signature2020LdVerifier
import info.weboftrust.ldsignatures.verifier.LdVerifier
import io.ipfs.multibase.Multibase
import org.bitcoinj.core.Base58
import org.iota.client.Client
import org.iota.client.Message
import org.iota.client.MessageId
import org.iota.client.MessageMetadata
import org.json.JSONArray
import org.json.JSONObject

class IotaPublicDidDoc : PublicDidDoc {
    var log: java.util.logging.Logger = java.util.logging.Logger.getLogger(IotaPublicDidDoc::class.java.getName())
    private var meta: JSONObject = JSONObject()
    var publicKey: ByteArray
    var tag: String
    var previousMessageId = ""

    constructor(crypto: AbstractCrypto) {
        publicKey = Base58.decode(crypto.createKey())
        tag = generateTag(publicKey)
        payload.put("id", "did:iota:$tag")
    }

    private constructor(msg: Message) {
        val obj = JSONObject(String(msg.payload().get().asIndexation().data()))
        this.payload = obj.optJSONObject("doc")
        meta = obj.optJSONObject("meta")
        previousMessageId = msg.id().toString()
        tag = this.payload.optString("id").substring("did:iota:".length)
        val verificationMethod: JSONObject? = getVerificationMethod(obj)
        publicKey = Multibase.decode(verificationMethod.optString("publicKeyMultibase"))
    }

    val didDoc: JSONObject
        get() = this.payload

    override fun submitToLedger(context: Context): Boolean {
        val o: JSONObject = generateIntegrationMessage(context.getCrypto()) ?: return false
        val iota: Client = node()
        val message: Message
        return try {
            message = iota.message().withIndexString(tag)
                .withData(o.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)).finish()
            previousMessageId = message.id().toString()
            saveToWallet(context.getNonSecrets())
            true
        } catch (ex: java.lang.Exception) {
            false
        }
    }

    private fun generateIntegrationMessage(crypto: AbstractCrypto): JSONObject? {
        val byteSigner: ByteSigner = IndyWalletSigner(crypto, Base58.encode(publicKey))
        val capabilityInvocation = JSONArray()
        capabilityInvocation.put(
            JSONObject()
                .put("id", this.payload.optString("id") + "#sign-0")
                .put("controller", this.payload.optString("id"))
                .put("type", "Ed25519VerificationKey2018")
                .put("publicKeyMultibase", Multibase.encode(Multibase.Base.Base58BTC, publicKey))
        )
        val dateFormat: java.text.DateFormat = java.text.SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss'Z'")
        this.payload.put("capabilityInvocation", capabilityInvocation)
        if (previousMessageId.isEmpty()) {
            meta.put("created", dateFormat.format(java.util.Date()))
        } else {
            meta.put("previousMessageId", previousMessageId)
        }
        meta.put("updated", dateFormat.format(java.util.Date()))
        val ldSigner: LdSigner = JcsEd25519Signature2020LdSigner(byteSigner)
        ldSigner.setVerificationMethod(java.net.URI.create(this.payload.optString("id") + "#sign-0"))
        val resMsg: JSONObject = JSONObject().put("doc", payload).put("meta", meta)
        val jsonLdObject: JsonLDObject = JsonLDObject.fromJson(resMsg.toString())
        var proof: JSONObject? = null
        try {
            proof = JSONObject(ldSigner.sign(jsonLdObject).toJson())
            resMsg.put("proof", proof)
            return resMsg
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        var msgComparator: java.util.Comparator<Message> = object : java.util.Comparator<Message?>() {
            override fun compare(o1: Message, o2: Message): Int {
                val meta1: MessageMetadata = node().getMessage().metadata(o1.id())
                val meta2: MessageMetadata = node().getMessage().metadata(o2.id())
                return if (meta1.milestoneIndex() < meta2.milestoneIndex()) -1 else if (meta1.milestoneIndex() > meta2.milestoneIndex()) 1 else o1.id()
                    .toString().compareTo(o2.id().toString())
            }
        }

        private fun generateTag(publicKey: ByteArray): String {
            val s: LazySodiumJava = LibSodium.getInstance().getLazySodium()
            val outputBytes = ByteArray(32)
            s.cryptoGenericHash(outputBytes, 32, publicKey, publicKey.size, null, 0)
            return Base58.encode(outputBytes)
        }

        fun load(did: String): IotaPublicDidDoc? {
            val msg: Message? = loadLastValidIntegrationMessage(did)
            return if (msg == null || !msg.payload().isPresent()) null else IotaPublicDidDoc(msg)
        }

        private fun loadLastValidIntegrationMessage(did: String): Message? {
            return try {
                val tag = tagFromId(did)
                val fetchedMessageIds: Array<MessageId> = node().getMessage().indexString(tag)
                val map: java.util.HashMap<String, List<Message>> = java.util.HashMap<String, List<Message>>()
                for (msgId in fetchedMessageIds) {
                    val msg: Message = node().getMessage().data(msgId)
                    if (msg.payload().isPresent()) {
                        val obj = JSONObject(String(msg.payload().get().asIndexation().data()))
                        val previousMessageId: String = obj.optJSONObject("meta").optString("previousMessageId", "")
                        if (!map.containsKey(previousMessageId)) map.put(
                            previousMessageId,
                            java.util.Arrays.asList<Message>(msg)
                        ) else map.get(previousMessageId).add(msg)
                    }
                }
                if (!map.containsKey("")) return null
                var prevMessageId = ""
                var prevMessage: Message? = null
                while (!map.isEmpty()) {
                    if (map.containsKey(prevMessageId)) {
                        val finalPrevMessage: Message? = prevMessage
                        val list: List<Message> = map.get(prevMessageId).stream()
                            .filter(java.util.function.Predicate<Message> { m: Message ->
                                checkMessage(
                                    m,
                                    finalPrevMessage
                                )
                            }).sorted(
                            msgComparator
                        ).collect(java.util.stream.Collectors.toList<Any>())
                        if (list.isEmpty()) {
                            return prevMessage
                        } else {
                            map.remove(prevMessageId)
                            prevMessage = list[list.size - 1]
                            prevMessageId = prevMessage.id().toString()
                        }
                    } else {
                        break
                    }
                }
                prevMessage
            } catch (ex: java.lang.Exception) {
                null
            }
        }

        private fun getVerificationMethod(obj: JSONObject, verificationMethodId: String): JSONObject? {
            val verificationMethods: JSONArray = obj.optJSONObject("doc").optJSONArray("capabilityInvocation")
            for (o in verificationMethods) {
                val verificationMethod: JSONObject = o as JSONObject
                if (verificationMethod.optString("id").equals(verificationMethodId)) {
                    return verificationMethod
                }
            }
            return null
        }

        private fun getVerificationMethod(obj: JSONObject): JSONObject? {
            val verificationMethodId: String = obj.optJSONObject("proof").optString("verificationMethod")
            return getVerificationMethod(obj, verificationMethodId)
        }

        private fun tagFromId(id: String): String {
            return if (id.startsWith("did:iota:")) id.substring("did:iota:".length) else id
        }

        private fun isFirstMessage(jsonMsg: JSONObject): Boolean {
            return jsonMsg.optJSONObject("meta").has("previousMessageId")
        }

        private fun checkFirstMessageTag(jsonMsg: JSONObject): Boolean {
            val verificationMethod: JSONObject = getVerificationMethod(jsonMsg)
                ?: return false
            val pubKeyMultibase: String = verificationMethod.optString("publicKeyMultibase")
            val tag = tagFromId(jsonMsg.optJSONObject("doc").optString("id"))
            return tag == generateTag(Multibase.decode(pubKeyMultibase))
        }

        private fun checkMessage(integrationMsg: Message, prevIntegrationMsg: Message?): Boolean {
            var prevIntegrationMsg: Message? = prevIntegrationMsg
            if (prevIntegrationMsg == null) prevIntegrationMsg = integrationMsg
            if (!integrationMsg.payload().isPresent() && !prevIntegrationMsg.payload().isPresent()) return false
            val integrationMsgJson = JSONObject(String(integrationMsg.payload().get().asIndexation().data()))
            val prevIntegrationMsgJson = JSONObject(String(prevIntegrationMsg.payload().get().asIndexation().data()))
            if (isFirstMessage(integrationMsgJson) && !checkFirstMessageTag(integrationMsgJson)) return false
            val verificationMethodId: String = integrationMsgJson.optJSONObject("proof").optString("verificationMethod")
            val verificationMethod: JSONObject = getVerificationMethod(prevIntegrationMsgJson, verificationMethodId)
                ?: return false
            val pubKeyMultibase: String = verificationMethod.optString("publicKeyMultibase")
            val ldVerifier: LdVerifier<JcsEd25519Signature2020SignatureSuite> =
                JcsEd25519Signature2020LdVerifier(Multibase.decode(pubKeyMultibase))
            val ldObject: JsonLDObject = JsonLDObject.fromJson(integrationMsgJson.toString())
            try {
                return ldVerifier.verify(ldObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return false
        }

        private const val MAINNET = "https://chrysalis-nodes.iota.cafe:443"
        private fun node(): Client {
            return Client.Builder().withNode(MAINNET).finish()
        }
    }
}