package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception is thrown when information is incompatible e.g. 2 different payment methods in inputs and outputs
 */
class IncompatiblePaymentException
/**
 * Initializes a new [IncompatiblePaymentException] with the specified message.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.IncompatiblePaymentError.value()) {
    companion object {
        private const val serialVersionUID = 5531031012103688872L
        private const val message = "Information passed to libindy is incompatible"
    }
}