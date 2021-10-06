package com.sirius.library.errors.sirius_exceptions

import com.sirius.library.errors.BaseSiriusException

class SiriusRPCError : BaseSiriusException {
    constructor(message: String?) : super(message) {}
    constructor() : super() {}
}