package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages

import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonObject

class AttribTranslation(attribName: String?, translation: String?) {
    var dict: JSONObject = JSONObject()
    fun getDicti(): JSONObject {
        return dict
    }

    init {
        dict.put("attrib_name", attribName)
        dict.put("translation", translation)
    }
}
