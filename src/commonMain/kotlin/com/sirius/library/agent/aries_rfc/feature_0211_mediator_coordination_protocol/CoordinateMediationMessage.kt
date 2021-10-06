package com.sirius.library.agent.aries_rfc.feature_0211_mediator_coordination_protocol

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.utils.JSONObject

open class CoordinateMediationMessage(message: String) : AriesProtocolMessage(message) {
    abstract class Builder<B : Builder<B>> protected constructor() :
        AriesProtocolMessage.Builder<B>() {
         override fun generateJSON(): JSONObject {
            setVersion("1.0")
            return super.generateJSON()
        }
    }

    companion object {
        const val PROTOCOL = "coordinate-mediation"
    }
}