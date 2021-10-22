package com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines

import com.sirius.library.agent.aries_rfc.feature_0015_ack.Ack
import com.sirius.library.agent.aries_rfc.feature_0048_trust_ping.Ping
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnProblemReport
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnProtocolMessage
import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.base.AbstractStateMachine
import com.sirius.library.hub.Context

abstract class BaseConnectionStateMachine(context: Context<*>, val me: Pairwise.Me, val myEndpoint: Endpoint) : AbstractStateMachine(context) {
    var problemReport: ConnProblemReport? = null
    override fun protocols(): List<String> {
        return listOf(ConnProtocolMessage.PROTOCOL, Ack.PROTOCOL, Ping.PROTOCOL)
    }



    companion object {
        const val REQUEST_NOT_ACCEPTED = "request_not_accepted"
        const val REQUEST_PROCESSING_ERROR = "request_processing_error"
        const val RESPONSE_NOT_ACCEPTED = "response_not_accepted"
        const val RESPONSE_PROCESSING_ERROR = "response_processing_error"
    }
}