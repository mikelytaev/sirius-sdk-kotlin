package com.sirius.library.agent.microledgers

import com.sirius.library.utils.JSONObject

class LedgerMeta : JSONObject {
    constructor(obj: JSONObject) : super(obj.toString()) {}
    constructor(name: String?, uid: String?, created: String?) : super() {
        put("name", name)
        put("uid", uid)
        put("created", created)
    }

    val name: String?
        get() = optString("name")
    val uid: String?
        get() = optString("uid")
    val created: String?
        get() = optString("created")
}