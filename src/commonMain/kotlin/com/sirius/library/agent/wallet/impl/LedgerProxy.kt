package com.sirius.library.agent.wallet.impl

import com.sirius.library.agent.RemoteParams
import com.sirius.library.agent.connections.AgentRPC
import com.sirius.library.agent.connections.RemoteCallWrapper
import com.sirius.library.agent.wallet.abstract_wallet.AbstractLedger
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.agent.wallet.abstract_wallet.model.NYMRole

class LedgerProxy(rpc: AgentRPC) : AbstractLedger() {
    var rpc: AgentRPC

    override fun readNym(poolName: String?, submitterDid: String?, targetDid: String?): Pair<Boolean, String?> {
        return object : RemoteCallWrapper<Pair<Boolean, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/read_nym",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitterDid).add("target_did", targetDid)
        ) ?: Pair(false, null)
    }

    override fun readAttribute(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        name: String?
    ): Pair<Boolean, String?> {
        return object : RemoteCallWrapper<Pair<Boolean, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/read_attribute",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitterDid)
                .add("target_did", targetDid).add("name", name)
        ) ?: Pair(false, null)
    }

    override fun writeNum(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        verKey: String?,
        alias: String?,
        role: NYMRole?
    ): Pair<Boolean, String?> {
        return object : RemoteCallWrapper<Pair<Boolean, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/write_nym",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitterDid)
                .add("target_did", targetDid).add("ver_key", verKey).add("alias", alias).add("role", role)
        ) ?: Pair(false, null)
    }

    override fun registerSchema(poolName: String?, submitterDid: String?, data: AnonCredSchema?): Pair<Boolean, String?> {
        return object : RemoteCallWrapper<Pair<Boolean, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/register_schema",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitterDid).add("data", data)
        ) ?: Pair(false, null)
    }

    override fun registerCredDef(poolName: String?, submitterDid: String?, data: Any?): Pair<Boolean, String?> {
        return object : RemoteCallWrapper<Pair<Boolean, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/register_cred_def",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitterDid)
                .add("data", data)
        ) ?: Pair(false, null)
    }

    override fun writeAttribute(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        name: String?,
        value: Any?
    ): Pair<Boolean, String?> {
        return object : RemoteCallWrapper<Pair<Boolean, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/write_attribute",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitterDid)
                .add("target_did", targetDid).add("name", name).add("value", value)
        ) ?: Pair(false, null)
    }

    override fun signAndSubmit(poolName: String?, submitterDid: String?, request: Any?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/sign_and_submit_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName).add("submitter_did", submitterDid)
                .add("request", request)
        )
    }

    override fun submitRequest(poolName: String?, request: Any?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/admin/1.0/submit_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName)
                .add("request", request)
        )
    }

    override fun submitAction(poolName: String?, request: String?, nodes: List<String?>?, timeout: Int?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/submit_action",
            RemoteParams.RemoteParamsBuilder.create()
                .add("pool_name", poolName)
                .add("request", request)
                .add("nodes", nodes)
                .add("timeout", timeout)
        )
    }

    override fun signRequest(submitterDid: String?, request: Any?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/sign_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("request", request)
        )
    }

    override fun multiSignRequest(submitterDid: String?, request: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/multi_sign_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("request", request)
        )
    }

    override fun buildGetDddoRequest(submitterDid: String?, targetDid: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_ddo_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("target_did", targetDid)
        )
    }

    override fun buildNymRequest(
        submitterDid: String?,
        targetDid: String?,
        verKey: String?,
        alias: String?,
        role: NYMRole?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_nym_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("target_did", targetDid)
                .add("ver_key", verKey)
                .add("alias", alias)
                .add("role", role)
        )
    }

    override  fun buildAttribRequest(
        submitterDid: String?,
        targetDid: String?,
        xhash: String?,
        raw: String?,
        enc: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_attrib_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("target_did", targetDid)
                .add("xhash", xhash)
                .add("raw", raw)
                .add("enc", enc)
        )
    }

    override fun buildGetAttribRequest(
        submitterDid: String?,
        targetDid: String?,
        raw: String?,
        xhash: String?,
        enc: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_attrib_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("target_did", targetDid)
                .add("raw", raw)
                .add("xhash", xhash)
                .add("enc", enc)
        )
    }

    override fun buildGetNymRequest(submitterDid: String?, targetDid: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_nym_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("target_did", targetDid)
        )
    }

    override  fun parseGetNymResponse(response: Any?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/parse_get_nym_response",
            RemoteParams.RemoteParamsBuilder.create()
                .add("response", response)
        )
    }

    override  fun buildSchemaRequest(submitterDid: String?, data: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_schema_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("data", data)
        )
    }

    override fun buildGetSchemaRequest(submitterDid: String?, id: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_schema_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("id", id)
        )
    }

    override fun parseGetSchemaResponse(getSchemaResponse: String?): Pair<String?, String?> {
        return object : RemoteCallWrapper<Pair<String?, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/parse_get_schema_response",
            RemoteParams.RemoteParamsBuilder.create()
                .add("get_schema_response", getSchemaResponse)
        ) ?: Pair(null,null)
    }

    override fun buildCredDefRequest(submitterDid: String?, data: Any?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/admin/1.0/build_cred_def_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("data", data)
        )
    }

    override fun buildGetCredDefRequest(submitterDid: String?, id: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_cred_def_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("id", id)
        )
    }

    override fun parseGetCredDefResponse(getCredDefResponse: String?): Pair<String?, String?> {
        return object : RemoteCallWrapper<Pair<String?, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/parse_get_cred_def_response",
            RemoteParams.RemoteParamsBuilder.create()
                .add("get_cred_def_response", getCredDefResponse)
        ) ?: Pair(null,null)
    }

    override  fun buildNodeRequest(submitterDid: String?, targetDid: String?, data: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_node_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("target_did", targetDid)
                .add("data", data)
        )
    }

    override fun buildGetValidatorInfoRequest(submitterDid: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_validator_info_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
        )
    }

    override fun buildGetTxnRequest(submitterDid: String?, ledgerType: String?, seq_no: Int): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_txn_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("ledger_type", ledgerType)
                .add("seq_no", seq_no)
        )
    }

    override fun buildPoolConfigRequest(submitterDid: String?, writes: Boolean, force: Boolean): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_pool_config_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("writes", writes)
                .add("force", force)
        )
    }

    override fun buildPoolRestart(submitterDid: String?, action: String?, datetime: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_pool_restart_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("action", action)
                .add("datetime", datetime)
        )
    }

    override fun buildPoolUpgradeRequest(
        submitter_did: String?, name: String?, version: String?, action: String?,
        sha256: String?, timeout: Int?, schedule: String?, justification: String?,
        reinstall: Boolean, force: Boolean, packageString: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_pool_upgrade_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitter_did)
                .add("name", name)
                .add("version", version)
                .add("action", action)
                .add("_sha256", sha256)
                .add("_timeout", timeout)
                .add("schedule", schedule)
                .add("justification", justification)
                .add("reinstall", reinstall)
                .add("force", force)
                .add("packageString", packageString)
        )
    }

    override fun buildRevocRegDefRequest(submitter_did: String?, data: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_revoc_reg_def_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitter_did)
                .add("data", data)
        )
    }

    override fun buildGetRevocRegDefRequest(submitter_did: String?, revRegDefId: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_revoc_reg_def_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitter_did)
                .add("rev_reg_def_id", revRegDefId)
        )
    }

    override  fun parseGetRevocRegDefResponse(getRevocRefDefResponse: String?): Pair<String?, String?> {
        return object : RemoteCallWrapper<Pair<String?, String?>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/parse_get_revoc_reg_def_response",
            RemoteParams.RemoteParamsBuilder.create()
                .add("get_revoc_ref_def_response", getRevocRefDefResponse)
        ) ?: Pair(null,null)
    }

    override fun buildRevocRegEntryRequest(
        submitterDid: String?,
        revocRegDefId: String?,
        revDefType: String?,
        value: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_revoc_reg_entry_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("revoc_reg_def_id", revocRegDefId)
                .add("rev_def_type", revDefType)
                .add("value", value)
        )
    }

    override fun buildGetREvocRegRequest(submitterDid: String?, revocRegDefId: String?, timestamp: Int): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_revoc_reg_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("revoc_reg_def_id", revocRegDefId)
                .add("timestamp", timestamp)
        )
    }

    override fun parseGetRevocRegResponse(getRevocRegResponse: String?): Triple<String?, String?, Int> {
        return object : RemoteCallWrapper<Triple<String?, String?, Int>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/parse_get_revoc_reg_response",
            RemoteParams.RemoteParamsBuilder.create()
                .add("get_revoc_reg_response", getRevocRegResponse)
        ) ?: Triple(null,null,0)
    }

    override fun buildGetRevocRegDeltaRequest(submitterDid: String?, revocRegDefId: String?, from: Int?, to: Int): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_revoc_reg_delta_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("revoc_reg_def_id", revocRegDefId)
                .add("from_", from)
                .add("to", to)
        )
    }

    override fun parseGetRevocRegDeltaResponse(getRevocRegDeltaResponse: String?): Triple<String?, String?, Int> {
        return object : RemoteCallWrapper<Triple<String?, String?, Int>>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/parse_get_revoc_reg_delta_response",
            RemoteParams.RemoteParamsBuilder.create()
                .add("get_revoc_reg_delta_response", getRevocRegDeltaResponse)
        ) ?:  Triple(null,null,0)
    }

    override fun responseMetadata(response: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/get_response_metadata",
            RemoteParams.RemoteParamsBuilder.create()
                .add("response", response)
        )
    }

    override fun buildAuthRuleRequest(
        submitterDid: String?, txnType: String?, action: String?,
        field: String?, old_value: String?, new_value: String?, constraint: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_auth_rule_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("txn_type", txnType)
                .add("action", action)
                .add("field", field)
                .add("old_value", old_value)
                .add("new_value", new_value)
                .add("constraint", constraint)
        )
    }

    override fun buildAuthRulesRequest(submitterDid: String?, data: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_auth_rules_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("data", data)
        )
    }

    override fun buildGetAuthRuleRequest(
        submitterDid: String?, txnType: String?, action: String?,
        field: String?, old_value: String?, new_value: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_auth_rule_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("txn_type", txnType)
                .add("action", action)
                .add("field", field)
                .add("old_value", old_value)
                .add("new_value", new_value)
        )
    }

    override fun buildTxnAuthorAgreementRequest(
        submitterDid: String?, text: String?, version: String?,
        ratification_ts: Int?, retirement_ts: Int?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_txn_author_agreement_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("text", text)
                .add("version", version)
                .add("ratification_ts", ratification_ts)
                .add("retirement_ts", retirement_ts)
        )
    }

    override fun buildDisableAllTxnAuthorAgreementsRequest(submitter_did: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_disable_all_txn_author_agreements_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitter_did)
        )
    }

    override fun buildGetTxnAuthorAgreementRequest(submitterDid: String?, data: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_txn_author_agreement_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("data", data)
        )
    }

    override fun buildAcceptanceMechanismsRequest(
        submitterDid: String?,
        aml: String?,
        version: String?,
        amlContext: String?
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_acceptance_mechanisms_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("aml", aml)
                .add("version", version)
                .add("amlContext", amlContext)
        )
    }

    override fun buildGetAcceptanceMechanismsRequest(submitterDid: String?, timestamp: Int?, version: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/build_get_acceptance_mechanisms_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("submitter_did", submitterDid)
                .add("timestamp", timestamp)
                .add("version", version)
        )
    }

    override fun appendTxnAuthorAgreementAcceptanceToRequest(
        request: String?, text: String?, version: String?,
        taa_digest: String?, mechanism: String?, time: Int
    ): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/append_txn_author_agreement_acceptance_to_request",
            RemoteParams.RemoteParamsBuilder.create()
                .add("request", request)
                .add("text", text)
                .add("version", version)
                .add("taa_digest", taa_digest)
                .add("mechanism", mechanism)
                .add("time", time)
        )
    }

    override fun appendRequestEndorser(request: String?, endorserDid: String?): String? {
        return object : RemoteCallWrapper<String?>(rpc) {}.remoteCall(
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/sirius_rpc/1.0/append_request_endorser",
            RemoteParams.RemoteParamsBuilder.create()
                .add("request", request)
                .add("endorserDid", endorserDid)
        )
    }

    init {
        this.rpc = rpc
    }
}
