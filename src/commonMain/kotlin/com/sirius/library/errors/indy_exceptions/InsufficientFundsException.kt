package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

class InsufficientFundsException
/**
 * Initializes a new [InsufficientFundsException] with the specified message.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.InsufficientFundsError.value()) {
    companion object {
        private const val serialVersionUID = 6397499268992083528L
        private const val message = "Insufficient funds on inputs"
    }
}