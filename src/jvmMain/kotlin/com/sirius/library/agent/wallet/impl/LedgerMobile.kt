
package com.sirius.library.agent.wallet.impl

import com.sirius.library.agent.wallet.abstract_wallet.AbstractLedger
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.agent.wallet.abstract_wallet.model.NYMRole
import org.hyperledger.indy.sdk.wallet.Wallet

class LedgerMobile(wallet: Wallet) : AbstractLedger() {
    var wallet: Wallet
    var timeoutSec = 60
    override fun readNym(poolName: String?, submitterDid: String?, targetDid: String?): Pair<Boolean, String>? {
        return null
    }

    override fun readAttribute(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        name: String?
    ): Pair<Boolean, String>? {
        return null
    }

    override fun writeNum(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        verKey: String?,
        alias: String?,
        role: NYMRole?
    ): Pair<Boolean, String>? {
        return null
    }

    override fun registerSchema(poolName: String?, submitterDid: String?, data: AnonCredSchema?): Pair<Boolean, String>? {
        return null
    }

    override fun registerCredDef(poolName: String?, submitterDid: String?, data: Any?): Pair<Boolean, String>? {
        return null
    }

    override fun writeAttribute(
        poolName: String?,
        submitterDid: String?,
        targetDid: String?,
        name: String?,
        value: Any?
    ): Pair<Boolean, String>? {
        return null
    }

    override fun signAndSubmit(poolName: String?, submitterDid: String?, request: Any?): String? {
        return null
    }

    override fun submitRequest(poolName: String?, request: Any?): String? {
        return null
    }

    override fun submitAction(poolName: String?, request: String?, nodes: List<String?>?, timeout: Int?): String? {
        return null
    }

    override fun signRequest(submitterDid: String?, request: Any?): String? {
        return null
    }

    override fun multiSignRequest(submitterDid: String?, request: String?): String? {
        return null
    }

    override fun buildGetDddoRequest(submitterDid: String?, targetDid: String?): String? {
        return null
    }

    override fun buildNymRequest(
        submitterDid: String?,
        targetDid: String?,
        verKey: String?,
        alias: String?,
        role: NYMRole?
    ): String? {
        return null
    }

    override fun buildAttribRequest(
        submitterDid: String?,
        targetDid: String?,
        xhash: String?,
        raw: String?,
        enc: String?
    ): String? {
        return null
    }

    override fun buildGetAttribRequest(
        submitterDid: String?,
        targetDid: String?,
        raw: String?,
        xhash: String?,
        enc: String?
    ): String? {
        return null
    }

    override fun buildGetNymRequest(submitterDid: String?, targetDid: String?): String? {
        return null
    }

    override fun parseGetNymResponse(response: Any?): String? {
        return null
    }

    override fun buildSchemaRequest(submitterDid: String?, data: String?): String? {
        return null
    }

    override fun buildGetSchemaRequest(submitterDid: String?, id: String?): String? {
        return null
    }

    override fun parseGetSchemaResponse(getSchemaResponse: String?): Pair<String, String>? {
        return null
    }

    override fun buildCredDefRequest(submitterDid: String?, data: Any?): String? {
        return null
    }

    override fun buildGetCredDefRequest(submitterDid: String?, id: String?): String? {
        return null
    }

    override fun parseGetCredDefResponse(getCredDefResponse: String?): Pair<String, String>? {
        return null
    }

    override fun buildNodeRequest(submitterDid: String?, targetDid: String?, data: String?): String? {
        return null
    }

    override fun buildGetValidatorInfoRequest(submitterDid: String?): String? {
        return null
    }

    override fun buildGetTxnRequest(submitterDid: String?, ledgerType: String?, seq_no: Int): String? {
        return null
    }

    override fun buildPoolConfigRequest(submitterDid: String?, writes: Boolean, force: Boolean): String? {
        return null
    }

    override fun buildPoolRestart(submitterDid: String?, action: String?, datetime: String?): String? {
        return null
    }

    override fun buildPoolUpgradeRequest(
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

    override fun buildRevocRegDefRequest(submitter_did: String?, data: String?): String? {
        return null
    }

    override fun buildGetRevocRegDefRequest(submitter_did: String?, revRegDefId: String?): String? {
        return null
    }

    override fun parseGetRevocRegDefResponse(getRevocRefDefResponse: String?): Pair<String, String>? {
        return null
    }

    override fun buildRevocRegEntryRequest(
        submitterDid: String?,
        revocRegDefId: String?,
        revDefType: String?,
        value: String?
    ): String? {
        return null
    }

    override fun buildGetREvocRegRequest(submitterDid: String?, revocRegDefId: String?, timestamp: Int): String? {
        return null
    }

    override fun parseGetRevocRegResponse(getRevocRegResponse: String?): Triple<String, String, Int>? {
        return null
    }

    override fun buildGetRevocRegDeltaRequest(submitterDid: String?, revocRegDefId: String?, from: Int?, to: Int): String? {
        return null
    }

    override fun parseGetRevocRegDeltaResponse(getRevocRegDeltaResponse: String?): Triple<String, String, Int>? {
        return null
    }

    override fun responseMetadata(response: String?): String? {
        return null
    }

    override fun buildAuthRuleRequest(
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

    override fun buildAuthRulesRequest(submitterDid: String?, data: String?): String? {
        return null
    }

    override fun buildGetAuthRuleRequest(
        submitterDid: String?,
        txnType: String?,
        action: String?,
        field: String?,
        old_value: String?,
        new_value: String?
    ): String? {
        return null
    }

    override fun buildTxnAuthorAgreementRequest(
        submitterDid: String?,
        text: String?,
        version: String?,
        ratification_ts: Int?,
        retirement_ts: Int?
    ): String? {
        return null
    }

    override fun buildDisableAllTxnAuthorAgreementsRequest(submitter: String?): String? {
        return null
    }

    override fun buildGetTxnAuthorAgreementRequest(submitterDid: String?, data: String?): String? {
        return null
    }

    override fun buildAcceptanceMechanismsRequest(
        submitterDid: String?,
        aml: String?,
        version: String?,
        amlContext: String?
    ): String? {
        return null
    }

    override fun buildGetAcceptanceMechanismsRequest(submitterDid: String?, timestamp: Int?, version: String?): String? {
        return null
    }

    override fun appendTxnAuthorAgreementAcceptanceToRequest(
        request: String?,
        text: String?,
        version: String?,
        taa_digest: String?,
        mechanism: String?,
        time: Int
    ): String? {
        return null
    }

    override fun appendRequestEndorser(request: String?, endorserDid: String?): String? {
        return null
    }

    init {
        this.wallet = wallet
    }
}

