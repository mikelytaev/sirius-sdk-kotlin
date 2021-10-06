package com.sirius.library.base

import com.sirius.library.utils.JSONObject


interface JsonSerializable<T> {
    fun serialize(): String?
    fun deserialize(string: String): T
    fun serializeToJSONObject(): JSONObject
}
