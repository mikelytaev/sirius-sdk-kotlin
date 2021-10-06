package com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages

import com.sirius.library.agent.aries_rfc.AriesProblemReport
import com.sirius.library.utils.JSONObject

class ConnProblemReport(message: String) : AriesProblemReport(message) {
    companion object {
        fun builder(): Builder<*> {
            return ConnProblemReportBuilder()
        }

        init {
            registerMessageClass(ConnProblemReport::class, ConnProtocolMessage.PROTOCOL, "problem_report")
        }
    }

    abstract class Builder<B : Builder<B>> : AriesProblemReport.Builder<B>() {
         override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): ConnProblemReport {
            return ConnProblemReport(generateJSON().toString())
        }
    }

    private class ConnProblemReportBuilder : Builder<ConnProblemReportBuilder>() {
        override fun self(): ConnProblemReportBuilder {
            return this
        }
    }
}
