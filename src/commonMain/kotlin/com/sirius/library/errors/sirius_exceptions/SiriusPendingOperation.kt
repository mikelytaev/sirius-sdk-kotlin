package com.sirius.library.errors.sirius_exceptions

class SiriusPendingOperation : RuntimeException {
    constructor() : super() {}
    constructor(message: String?) : super(message) {}
}
