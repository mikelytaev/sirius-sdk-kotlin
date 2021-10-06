package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonObject

open class BaseIssueCredentialMessage(message: String) : AriesProtocolMessage(message) {
    val comment: String?
        get() = this.getMessageObj()?.optString("comment")

    abstract class Builder<B : Builder<B>> protected constructor() :
        AriesProtocolMessage.Builder<B>() {
        protected var locale = DEF_LOCALE
        var comment: String? = null
        fun setLocale(locate: String): B {
            locale = locate
            return self()
        }

        fun setComment(comment: String?): B {
            this.comment = comment
            return self()
        }

         override fun generateJSON(): JSONObject {
            setVersion("1.1")
            val jsonObject: JSONObject = super.generateJSON()
            val l10n = JSONObject()
            l10n.put("locale", locale)
            jsonObject.put("~l10n", l10n)
            if (comment != null) {
                jsonObject.put("comment", comment)
            }
            return jsonObject
        }
    }

    companion object {
        const val PROTOCOL = "issue-credential"
        const val DEF_LOCALE = "en"
        const val CREDENTIAL_PREVIEW_TYPE =
            "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/credential-preview"
        const val CREDENTIAL_TRANSLATION_TYPE =
            "https://github.com/Sirius-social/agent/tree/master/messages/credential-translation"
        const val ISSUER_SCHEMA_TYPE = "https://github.com/Sirius-social/agent/tree/master/messages/issuer-schema"
        const val CREDENTIAL_TRANSLATION_ID = "credential-translation"
        const val ISSUER_SCHEMA_ID = "issuer-schema"
    }
}
