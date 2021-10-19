package com.sirius.library.helpers

expect class HttpClient() {

    fun get (url : String) : Pair<Boolean,String?>
}