package com.sirius.library.utils

expect object JSONEscape {
    fun unescapeJsonObject(unescaped : String) : String
}