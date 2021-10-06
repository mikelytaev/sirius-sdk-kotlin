package com.sirius.library.utils

object GsonUtils {
    val defaultGson: Gson
        get() = GsonBuilder().setExclusionStrategies(ExposeExcludeStrategy()).create()

    fun toJsonObject(jsonString: String?): JsonObject {
        return JsonParser.parseString(jsonString).getAsJsonObject()
    }

    fun toJsonArray(jsonString: String?): JsonArray {
        return JsonParser.parseString(jsonString).getAsJsonArray()
    }

    fun updateJsonObject(originalJson: JsonObject, updateObject: JsonObject): JsonObject {
        return updateJsonObject(originalJson, updateObject, false)
    }

    fun updateJsonObject(originalJson: JsonObject, updateObject: JsonObject, withCopy: Boolean): JsonObject {
        var copyOrigin: JsonObject = originalJson
        if (withCopy) {
            copyOrigin = originalJson.deepCopy()
        }
        val entrySet: Set<Map.Entry<String?, JsonElement?>> = updateObject.entrySet()
        for ((key, value): Map.Entry<String?, JsonElement?> in entrySet) {
            copyOrigin.add(key, value)
        }
        return copyOrigin
    }

    class ExposeExcludeStrategy : ExclusionStrategy {
        fun shouldSkipField(field: FieldAttributes): Boolean {
            val annotation: Expose = field.getAnnotation(Expose::class.java) ?: return false
            return true
        }

        fun shouldSkipClass(clazz: java.lang.Class<*>?): Boolean {
            return false
        }
    }
}
