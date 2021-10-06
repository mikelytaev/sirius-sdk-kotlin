package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when unknown (e.g. unregistered) payment method was called
 */
class UnknownPaymentMethodException
/**
 * Initializes a new [UnknownPaymentMethodException]
 */
    (error: IndyError) :
    IndyException(MESSAGE + error.buildMessage(), ErrorCode.UnknownPaymentMethod.value()) {
    companion object {
        private const val serialVersionUID = -8226688236266389417L
        private const val MESSAGE = "An unknown payment method was called"
    }
}
