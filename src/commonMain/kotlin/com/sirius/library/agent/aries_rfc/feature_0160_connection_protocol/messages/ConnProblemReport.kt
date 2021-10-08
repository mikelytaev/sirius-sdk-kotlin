package com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages

import com.sirius.library.agent.aries_rfc.AriesProblemReport
import com.sirius.library.agent.aries_rfc.feature_0113_question_answer.messages.QuestionMessage
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

class ConnProblemReport(message: String) : AriesProblemReport(message) {
    companion object {
        fun builder(): Builder<*> {
            return ConnProblemReportBuilder()
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

        override fun getClass(): KClass<out Message> {
            return ConnProblemReport::class
        }
    }
}
