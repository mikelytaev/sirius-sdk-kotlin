package com.sirius.library.errors.sirius_exceptions

import com.sirius.library.errors.BaseSiriusException

class SiriusPromiseContextException : BaseSiriusException {
    constructor(className: String, printable: String, message: String?) : super(message) {
        this.className = className
        this.printable = printable
    }

    constructor(className: String, printable: String) : super("$className;$printable") {
        this.className = className
        this.printable = printable
    }

    var className: String
    var printable: String
}