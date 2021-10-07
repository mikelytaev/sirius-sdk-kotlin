package com.sirius.library.agent.aries_rfc.feature_0037_present_proof.messages

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.utils.JSONObject

open class BasePresentProofMessage(msg: String) : AriesProtocolMessage(msg) {
    val comment: String?
        get() = this.getMessageObjec()?.getString("comment")

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
        const val PROTOCOL = "present-proof"
        const val DEF_LOCALE = "en"
        const val CREDENTIAL_TRANSLATION_TYPE =
            "https://github.com/Sirius-social/agent/tree/master/messages/credential-translation"
        const val CREDENTIAL_TRANSLATION_ID = "credential-translation"
    }
}
