package com.sirius.library.errors

import kotlin.jvm.JvmOverloads

class StateMachineTerminatedWithError @JvmOverloads constructor(
    var problemCode: String,
    var explain: String,
    var isNotify: Boolean = true
) :
    BaseSiriusException()