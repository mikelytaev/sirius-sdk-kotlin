package com.sirius.library.errors.sirius_exceptions

import com.sirius.library.errors.BaseSiriusException

class SiriusInvalidType : BaseSiriusException {
    constructor(message: String?) : super(message) {}
    constructor() {}
}