package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.state_machines

import com.sirius.library.agent.aries_rfc.feature_0015_ack.Ack
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.BaseIssueCredentialMessage
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.IssueProblemReport
import com.sirius.library.base.AbstractStateMachine
import com.sirius.library.hub.Context

abstract class BaseIssuingStateMachine(context: Context) : AbstractStateMachine(context) {
    var problemReport: IssueProblemReport? = null
    override fun protocols(): List<String> {
        return listOf(BaseIssueCredentialMessage.PROTOCOL, Ack.PROTOCOL)
    }

    companion object {
        const val PROPOSE_NOT_ACCEPTED = "propose_not_accepted"
        const val OFFER_PROCESSING_ERROR = "offer_processing_error"
        const val REQUEST_NOT_ACCEPTED = "request_not_accepted"
        const val ISSUE_PROCESSING_ERROR = "issue_processing_error"
        const val RESPONSE_FOR_UNKNOWN_REQUEST = "response_for_unknown_request"
    }
}
