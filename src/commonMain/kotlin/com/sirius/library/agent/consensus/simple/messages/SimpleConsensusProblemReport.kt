package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.aries_rfc.AriesProblemReport
import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONObject
import kotlin.reflect.KClass

class SimpleConsensusProblemReport(message: String) : AriesProblemReport(message) {
    companion object {
        fun builder(): Builder<*> {
            return SimpleConsensusProblemReportBuilder()
        }

    }

    abstract class Builder<B : Builder<B>> :
        AriesProblemReport.Builder<B>() {
        override fun generateJSON(): JSONObject {
            return super.generateJSON()
        }

        fun build(): SimpleConsensusProblemReport {
            return SimpleConsensusProblemReport(generateJSON().toString())
        }
    }

    private class SimpleConsensusProblemReportBuilder :
        Builder<SimpleConsensusProblemReportBuilder>() {
        protected override fun self(): SimpleConsensusProblemReportBuilder {
            return this
        }

        override fun getClass(): KClass<out Message> {
            return SimpleConsensusProblemReport::class
        }
    }
}