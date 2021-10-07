package com.sirius.library

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal


class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }






}
