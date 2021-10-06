package com.sirius.library.agent.aries_rfc.feature_0037_present_proof.messages

import com.sirius.library.agent.aries_rfc.AriesProblemReport
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject

class PresentProofProblemReport(message: String) : AriesProblemReport(message) {
    companion object {
        fun builder(): Builder<*> {
            return PresentProofProblemReportBuilder()
        }

        init {
            Message.registerMessageClass(
                PresentProofProblemReport::class,
                BasePresentProofMessage.PROTOCOL,
                "problem_report"
            )
        }
    }

    abstract class Builder<B : Builder<B>> :
        AriesProblemReport.Builder<B>() {
         override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): PresentProofProblemReport {
            return PresentProofProblemReport(generateJSON().toString())
        }
    }

    private class PresentProofProblemReportBuilder :
        Builder<PresentProofProblemReportBuilder>() {
        protected override fun self(): PresentProofProblemReportBuilder {
            return this
        }
    }
}