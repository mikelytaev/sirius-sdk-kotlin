package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

class PaymentSourceDoesNotExistException
/**
 * Initializes a new [PaymentSourceDoesNotExistException] with the specified message.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PaymentSourceDoesNotExistError.value()) {
    companion object {
        private const val serialVersionUID = -5009466707967765943L
        private const val message = "No such source found"
    }
}