package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages

import com.sirius.library.utils.JSONObject

class ProposedAttrib : JSONObject {
    constructor() : super() {}
    constructor(o: JSONObject) : super(o.toString()) {}
    constructor(name: String?, value: String?) {
        put("name", name)
        put("value", value)
    }

    constructor(name: String?, value: String?, mimeType: String?) {
        put("name", name)
        put("value", value)
        put("mime-type", mimeType)
    }

    val name: String?
        get() = optString("name")
    val value: String?
        get() = optString("value")
    val mimeType: String?
        get() = optString("mime-type")
}
