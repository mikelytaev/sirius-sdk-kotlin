package com.sirius.library.agent.aries_rfc

import com.sirius.library.agent.coprotocols.AbstractCloudCoProtocolTransport.Companion.THREAD_DECORATOR
import com.sirius.library.utils.JSONObject

open class AriesProblemReport(message: String) : AriesProtocolMessage(message) {
    val problemCode: String?
        get() = getMessageObjec()?.optString("problem-code")
    val explain: String?
        get() = getMessageObjec()?.optString("explain")

    abstract class Builder<B : Builder<B>> : AriesProtocolMessage.Builder<B>() {
        var problemCode: String? = null
        var explain: String? = null
        var threadId: String? = null
        fun setProblemCode(problemCode: String?): B {
            this.problemCode = problemCode
            return self()
        }

        fun setExplain(explain: String?): B {
            this.explain = explain
            return self()
        }

        fun setThreadId(threadId: String?): B {
            this.threadId = threadId
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            val id: String? = jsonObject.optString("id")
            if (problemCode != null) {
                jsonObject.put("problem-code", problemCode)
            }
            if (explain != null) {
                jsonObject.put("explain", explain)
            }
            if (threadId != null) {
                var thread: JSONObject? = jsonObject.optJSONObject(THREAD_DECORATOR)
                if (thread == null) thread = JSONObject()
                thread.put("thid", thread)
                jsonObject.put(THREAD_DECORATOR, thread)
            }
            return jsonObject
        }
    }
}
