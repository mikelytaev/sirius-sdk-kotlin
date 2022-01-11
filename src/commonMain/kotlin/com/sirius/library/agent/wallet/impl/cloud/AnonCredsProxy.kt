package com.sirius.library.agent.wallet.impl.cloud

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.agent.wallet.abstract_wallet.AbstractAnonCreds
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.errors.indy_exceptions.DuplicateMasterSecretNameException
import com.sirius.library.errors.indy_exceptions.WalletItemNotFoundException
import com.sirius.library.utils.JSONObject

class AnonCredsProxy(rpc: AgentRPC) : AbstractAnonCreds() {
    var rpc: AgentRPC
    override fun hashCode(): Int {
        return rpc.hashCode()
    }

    override fun issuerCreateSchema(
        issuerDid: String?,
        name: String?,
        version: String?,
        attrs: List<String?>?
    ): Pair<String?, AnonCredSchema?> {
        val params = RemoteParams.RemoteParamsBuilder.create()
            .add("issuer_did", issuerDid).add("name", name).add("version", version).add("attrs", attrs)
        val pair : Pair<String?, String?> = object : RemoteCallWrapper<Pair<String?, String?>>(rpc) {}.
        remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_create_schema", params) ?: Pair<String?, String?>(null,null)

        println("pair?.second?=${pair?.second}")
        println("pair?.first?=${pair?.first}")
        val anonCredSchema = AnonCredSchema(pair?.second?.toString() ?:"")
        return Pair(pair?.first ?:"", anonCredSchema)
    }

    override fun issuerCreateAndStoreCredentialDef(
        issuerDid: String?,
        schema: Any?,
        tag: String?,
        signatureType: String?,
        config: Any?
    ): Pair<String?, String?> {
        val pair : Pair<String?, String?> = object : RemoteCallWrapper<Pair<String?, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_create_and_store_credential_def",
            RemoteParams.RemoteParamsBuilder.create()
                .add("issuer_did", issuerDid)
                .add("schema", schema)
                .add("tag", tag)
                .add("signature_type", signatureType)
                .add("config", config)
        ) ?: Pair(null, null)
        return pair
    }

    override fun issuerRotateCredentialDefStart(credDefId: String?, config: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_rotate_credential_def_start",
            RemoteParams.RemoteParamsBuilder.create()
                .add("cred_def_id", credDefId)
                .add("config", config)
        )
    }

    override fun issuerRotateCredentialDefApply(credDefId: String?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_rotate_credential_def_apply",
            RemoteParams.RemoteParamsBuilder.create()
                .add("cred_def_id", credDefId)
        )
    }

    override fun issuerCreateAndStoreRevocReg(
        issuerDid: String?,
        revocDefType: String?,
        tag: String?,
        credDefId: String?,
        config: String?,
        tailsWriterHandle: Int
    ): Triple<String?, String?, String?> {
        return object : RemoteCallWrapper<Triple<String?, String?, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_create_and_store_revoc_reg",
            RemoteParams.RemoteParamsBuilder.create()
                .add("issuer_did", issuerDid)
                .add("revoc_def_type", revocDefType)
                .add("cred_def_id", credDefId)
                .add("config", config)
                .add("tails_writer_handle", tailsWriterHandle)
        ) ?: Triple(null,null,null)
    }

    override fun issuerCreateCredentialOffer(credDefId: String?): JSONObject {
        return JSONObject(
            object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_create_credential_offer",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("cred_def_id", credDefId)
            )
        )
    }

    override fun issuerCreateCredential(
        credOffer: JSONObject?,
        credReq: JSONObject?,
        credValues: JSONObject?,
        revRegId: String?,
        blobStorageReaderHandle: Int?
    ): Triple<JSONObject, String?, JSONObject?> {
        val first = object : RemoteCallWrapper<Triple<String?, String?, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_create_credential",
            RemoteParams.RemoteParamsBuilder.create()
                .add("cred_offer", credOffer)
                .add("cred_req", credReq)
                .add("cred_values", credValues)
                .add("rev_reg_id", revRegId)
                .add("blob_storage_reader_handle", blobStorageReaderHandle)
        ) ?: Triple("{}",null,null)
        val firstVal = first.first
        return Triple<JSONObject, String?, JSONObject?>(JSONObject(firstVal), null, null)
    }

    override fun issuerRevokeCredential(blobStorageReaderHandle: Int?, revRegId: String?, credRevocId: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_revoke_credential",
            RemoteParams.RemoteParamsBuilder.create()
                .add("blob_storage_reader_handle", blobStorageReaderHandle)
                .add("rev_reg_id", revRegId)
                .add("cred_revoc_id", credRevocId)
        )
    }

    override fun issuerMergeRevocationRegistryDeltas(revRegDelta: String?, otherRevRegDelta: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/issuer_merge_revocation_registry_deltas",
            RemoteParams.RemoteParamsBuilder.create()
                .add("rev_reg_delta", revRegDelta)
                .add("other_rev_reg_delta", otherRevRegDelta)
        )
    }

    @Throws(DuplicateMasterSecretNameException::class)
    override fun proverCreateMasterSecret(masterSecretName: String?): String? {
        try {
            return object : RemoteCallWrapper<String?>(rpc) {}.remoteCallEx(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_create_master_secret",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("master_secret_name", masterSecretName)
            )
        } catch (e: DuplicateMasterSecretNameException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun proverCreateCredentialReq(
        proverDid: String?,
        credOffer: JSONObject?,
        credDef: JSONObject?,
        masterSecretId: String?
    ): Pair<JSONObject, JSONObject> {
        val firstSecond  = object : RemoteCallWrapper<Pair<String?, String?>?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_create_credential_req",
            RemoteParams.RemoteParamsBuilder.create()
                .add("prover_did", proverDid)
                .add("cred_offer", credOffer)
                .add("cred_def", credDef)
                .add("master_secret_id", masterSecretId)
        )
        val firstVal = firstSecond?.first ?: "{}"
        val secondVal = firstSecond?.second ?: "{}"
        return Pair<JSONObject, JSONObject>(JSONObject(firstVal), JSONObject(secondVal))
    }

    override fun proverSetCredentialAttrTagPolicy(credDefId: String?, taAttrs: String?, retroactive: Boolean) {
        object : RemoteCallWrapper<Pair<String?, String?>?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_set_credential_attr_tag_policy",
            RemoteParams.RemoteParamsBuilder.create()
                .add("cred_def_id", credDefId)
                .add("tag_attrs", taAttrs)
                .add("retroactive", retroactive)
        )
    }

    override fun proverGetCredentialAttrTagPolicy(credDefId: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_get_credential_attr_tag_policy",
            RemoteParams.RemoteParamsBuilder.create()
                .add("cred_def_id", credDefId)
        )
    }

    override fun proverStoreCredential(
        credId: String?,
        credReqMetadata: JSONObject?,
        cred: JSONObject?,
        credDef: JSONObject?,
        revRegDef: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_store_credential",
            RemoteParams.RemoteParamsBuilder.create()
                .add("cred_id", credId)
                .add("cred_req_metadata", credReqMetadata)
                .add("cred", cred)
                .add("cred_def", credDef)
                .add("rev_reg_def", revRegDef)
        )
    }

    @Throws(WalletItemNotFoundException::class)
    override fun proverGetCredential(credId: String?): String? {
        try {
            return object : RemoteCallWrapper<String?>(rpc) {}.remoteCallEx(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_get_credential",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("cred_id", credId)
            )
        } catch (e: WalletItemNotFoundException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun proverDeleteCredential(credId: String?) {
        object : RemoteCallWrapper<Unit>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_delete_credential",
            RemoteParams.RemoteParamsBuilder.create()
                .add("cred_id", credId)
        )
    }

    override fun proverGetCredentials(filters: String?): List<String> {
        return object : RemoteCallWrapper<List<String>?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_get_credentials",
            RemoteParams.RemoteParamsBuilder.create()
                .add("filters", filters)
        )?: listOf()
    }

    override fun proverSearchCredential(query: String?): List<String> {
        return object : RemoteCallWrapper<List<String>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_search_credentials",
            RemoteParams.RemoteParamsBuilder.create()
                .add("query", query)
        )?: listOf()
    }

    override fun proverGetCredentialsForProofReq(proofRequest: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_get_credentials_for_proof_req",
            RemoteParams.RemoteParamsBuilder.create()
                .add("proof_request", proofRequest)
        )
    }

    override fun proverSearchCredentialsForProofReq(
        proofRequest: JSONObject?,
        extraQuery: String?,
        limitReferents: Int
    ): JSONObject {
        return JSONObject(
            object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_search_credentials_for_proof_req",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("proof_request", proofRequest)
                    .add("extra_query", extraQuery)
                    .add("limit_referents", limitReferents)
            )
        )
    }

    override fun proverCreateProof(
        proofReq: JSONObject?,
        requestedCredentials: JSONObject?,
        masterSecretName: String?,
        schemas: JSONObject?,
        credentialDefs: JSONObject?,
        revStates: JSONObject?
    ): JSONObject {
        return JSONObject(
            object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
                "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/prover_create_proof",
                RemoteParams.RemoteParamsBuilder.create()
                    .add("proof_req", proofReq)
                    .add("requested_credentials", requestedCredentials)
                    .add("master_secret_name", masterSecretName)
                    .add("schemas", schemas)
                    .add("credential_defs", credentialDefs)
                    .add("rev_states", revStates)
            )
        )
    }

    override fun verifierVerifyProof(
        proofRequest:JSONObject?,
        proof: JSONObject?,
        schemas: JSONObject?,
        credentialDefs: JSONObject?,
        revRegDefs: JSONObject?,
        revRegs: JSONObject?
    ): Boolean {
        return object : RemoteCallWrapper<Boolean?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/verifier_verify_proof",
            RemoteParams.RemoteParamsBuilder.create()
                .add("proof_request", proofRequest)
                .add("proof", proof)
                .add("schemas", schemas)
                .add("credential_defs", credentialDefs)
                .add("rev_reg_defs", revRegDefs)
                .add("rev_regs", revRegs)
        ) ?: false
    }

    override fun createRevocation(
        blobStorageReaderHandle: Int,
        revRegDef: String?,
        revRegDelta: String?,
        timestamp: Int,
        credRevId: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/create_revocation_state",
            RemoteParams.RemoteParamsBuilder.create()
                .add("blob_storage_reader_handle", blobStorageReaderHandle)
                .add("rev_reg_def", revRegDef)
                .add("rev_reg_delta", revRegDelta)
                .add("timestamp", timestamp)
                .add("cred_rev_id", credRevId)
        )
    }

    override fun updateRevocationState(
        blobStorageReaderHandle: Int,
        revState: String?,
        revRegDef: String?,
        revRegDelta: String?,
        timestamp: Int,
        credRevId: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/update_revocation_state",
            RemoteParams.RemoteParamsBuilder.create()
                .add("blob_storage_reader_handle", blobStorageReaderHandle)
                .add("rev_state", revState)
                .add("rev_reg_def", revRegDef)
                .add("rev_reg_delta", revRegDelta)
                .add("timestamp", timestamp)
                .add("cred_rev_id", credRevId)
        )
    }

    override fun generateNonce(): String? {
        return object :
            RemoteCallWrapper<String?>(rpc) {}.remoteCall("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/generate_nonce")
    }

    override fun toUnqualified(entity: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/to_unqualified",
            RemoteParams.RemoteParamsBuilder.create()
                .add("entity", entity)
        )
    }

    init {
        this.rpc = rpc
    }
}
