package com.sirius.library.agent.aries_rfc.feature_0037_present_proof.state_machines

import com.sirius.library.agent.aries_rfc.feature_0015_ack.Ack
import com.sirius.library.agent.aries_rfc.feature_0037_present_proof.messages.BasePresentProofMessage
import com.sirius.library.agent.aries_rfc.feature_0037_present_proof.messages.PresentProofProblemReport
import com.sirius.library.base.AbstractStateMachine
import com.sirius.library.hub.Context

abstract class BaseVerifyStateMachine(context: Context) : AbstractStateMachine(context) {
    var problemReport: PresentProofProblemReport? = null


    override fun protocols(): List<String> {
        return listOf(BasePresentProofMessage.PROTOCOL, Ack.PROTOCOL)
    }

    companion object {
        const val PROPOSE_NOT_ACCEPTED = "propose_not_accepted"
        const val RESPONSE_NOT_ACCEPTED = "response_not_accepted"
        const val RESPONSE_PROCESSING_ERROR = "response_processing_error"
        const val REQUEST_NOT_ACCEPTED = "request_not_accepted"
        const val RESPONSE_FOR_UNKNOWN_REQUEST = "response_for_unknown_request"
        const val REQUEST_PROCESSING_ERROR = "request_processing_error"
        const val VERIFY_ERROR = "verify_error"
    }
}
