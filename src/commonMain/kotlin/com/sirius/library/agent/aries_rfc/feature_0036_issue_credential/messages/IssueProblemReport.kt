package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages

import com.sirius.library.agent.aries_rfc.AriesProblemReport
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

class IssueProblemReport(message: String) : AriesProblemReport(message) {
    companion object {
        fun builder(): Builder<*> {
            return IssueProblemReportBuilder()
        }


    }

    abstract class Builder<B : Builder<B>> : AriesProblemReport.Builder<B>() {
         override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): IssueProblemReport {
            return IssueProblemReport(generateJSON().toString())
        }
    }

    private class IssueProblemReportBuilder : Builder<IssueProblemReportBuilder>() {
        protected override fun self(): IssueProblemReportBuilder {
            return this
        }

        override fun getClass(): KClass<out Message> {
            return IssueProblemReport::class
        }
    }
}
