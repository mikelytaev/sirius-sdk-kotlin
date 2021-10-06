/*
package com.sirius.library.agent.wallet.impl

class LedgerMobile(wallet: Wallet) : AbstractLedger() {
    var wallet: Wallet
    var timeoutSec = 60
    fun readNym(poolName: String?, submitterDid: String?, targetDid: String?): Pair<Boolean, String>? {
        return null
    }

    fun readAttribute(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        name: String?
    ): Pair<Boolean, String>? {
        return null
    }

    fun writeNum(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        verKey: String?,
        alias: String?,
        role: NYMRole?
    ): Pair<Boolean, String>? {
        return null
    }

    fun registerSchema(poolName: String?, submitterDid: String?, data: AnonCredSchema?): Pair<Boolean, String>? {
        return null
    }

    fun registerCredDef(poolName: String?, submitterDid: String?, data: Any?): Pair<Boolean, String>? {
        return null
    }

    fun writeAttribute(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        name: String?,
        value: Any?
    ): Pair<Boolean, String>? {
        return null
    }

    fun signAndSubmit(poolName: String?, submitterDid: String?, request: Any?): String? {
        return null
    }

    fun submitRequest(poolName: String?, request: Any?): String? {
        return null
    }

    fun submitAction(poolName: String?, request: String?, nodes: List<String?>?, timeout: Int?): String? {
        return null
    }

    fun signRequest(submitterDid: String?, request: Any?): String? {
        return null
    }

    fun multiSignRequest(submitterDid: String?, request: String?): String? {
        return null
    }

    fun buildGetDddoRequest(submitterDid: String?, targetDid: String?): String? {
        return null
    }

    fun buildNymRequest(
        submitterDid: String?,
        targetDid: String?,
        verKey: String?,
        alias: String?,
        role: NYMRole?
    ): String? {
        return null
    }

    fun buildAttribRequest(
        submitterDid: String?,
        targetDid: String?,
        xhash: String?,
        raw: String?,
        enc: String?
    ): String? {
        return null
    }

    fun buildGetAttribRequest(
        submitterDid: String?,
        targetDid: String?,
        raw: String?,
        xhash: String?,
        enc: String?
    ): String? {
        return null
    }

    fun buildGetNymRequest(submitterDid: String?, targetDid: String?): String? {
        return null
    }

    fun parseGetNymResponse(response: Any?): String? {
        return null
    }

    fun buildSchemaRequest(submitterDid: String?, data: String?): String? {
        return null
    }

    fun buildGetSchemaRequest(submitterDid: String?, id: String?): String? {
        return null
    }

    fun parseGetSchemaResponse(getSchemaResponse: String?): Pair<String, String>? {
        return null
    }

    fun buildCredDefRequest(submitterDid: String?, data: Any?): String? {
        return null
    }

    fun buildGetCredDefRequest(submitterDid: String?, id: String?): String? {
        return null
    }

    fun parseGetCredDefResponse(getCredDefResponse: String?): Pair<String, String>? {
        return null
    }

    fun buildNodeRequest(submitterDid: String?, targetDid: String?, data: String?): String? {
        return null
    }

    fun buildGetValidatorInfoRequest(submitterDid: String?): String? {
        return null
    }

    fun buildGetTxnRequest(submitterDid: String?, ledgerType: String?, seq_no: Int): String? {
        return null
    }

    fun buildPoolConfigRequest(submitterDid: String?, writes: Boolean, force: Boolean): String? {
        return null
    }

    fun buildPoolRestart(submitterDid: String?, action: String?, datetime: String?): String? {
        return null
    }

    fun buildPoolUpgradeRequest(
        submitter_did: String?,
        name: String?,
        version: String?,
        action: String?,
        sha256: String?,
        timeout: Int?,
        schedule: String?,
        justification: String?,
        reinstall: Boolean,
        force: Boolean,
        packageString: String?
    ): String? {
        return null
    }

    fun buildRevocRegDefRequest(submitter_did: String?, data: String?): String? {
        return null
    }

    fun buildGetRevocRegDefRequest(submitter_did: String?, revRegDefId: String?): String? {
        return null
    }

    fun parseGetRevocRegDefResponse(getRevocRefDefResponse: String?): Pair<String, String>? {
        return null
    }

    fun buildRevocRegEntryRequest(
        submitterDid: String?,
        revocRegDefId: String?,
        revDefType: String?,
        value: String?
    ): String? {
        return null
    }

    fun buildGetREvocRegRequest(submitterDid: String?, revocRegDefId: String?, timestamp: Int): String? {
        return null
    }

    fun parseGetRevocRegResponse(getRevocRegResponse: String?): Triple<String, String, Int>? {
        return null
    }

    fun buildGetRevocRegDeltaRequest(submitterDid: String?, revocRegDefId: String?, from: Int?, to: Int): String? {
        return null
    }

    fun parseGetRevocRegDeltaResponse(getRevocRegDeltaResponse: String?): Triple<String, String, Int>? {
        return null
    }

    fun responseMetadata(response: String?): String? {
        return null
    }

    fun buildAuthRuleRequest(
        submitterDid: String?,
        txnType: String?,
        action: String?,
        field: String?,
        old_value: String?,
        new_value: String?,
        constraint: String?
    ): String? {
        return null
    }

    fun buildAuthRulesRequest(submitterDid: String?, data: String?): String? {
        return null
    }

    fun buildGetAuthRuleRequest(
        submitterDid: String?,
        txnType: String?,
        action: String?,
        field: String?,
        old_value: String?,
        new_value: String?
    ): String? {
        return null
    }

    fun buildTxnAuthorAgreementRequest(
        submitterDid: String?,
        text: String?,
        version: String?,
        ratification_ts: Int?,
        retirement_ts: Int?
    ): String? {
        return null
    }

    fun buildDisableAllTxnAuthorAgreementsRequest(submitter: String?): String? {
        return null
    }

    fun buildGetTxnAuthorAgreementRequest(submitterDid: String?, data: String?): String? {
        return null
    }

    fun buildAcceptanceMechanismsRequest(
        submitterDid: String?,
        aml: String?,
        version: String?,
        amlContext: String?
    ): String? {
        return null
    }

    fun buildGetAcceptanceMechanismsRequest(submitterDid: String?, timestamp: Int?, version: String?): String? {
        return null
    }

    fun appendTxnAuthorAgreementAcceptanceToRequest(
        request: String?,
        text: String?,
        version: String?,
        taa_digest: String?,
        mechanism: String?,
        time: Int
    ): String? {
        return null
    }

    fun appendRequestEndorser(request: String?, endorserDid: String?): String? {
        return null
    }

    init {
        this.wallet = wallet
    }
}
*/
