package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when the SDK reports that it is in an invalid state.
 */
class InvalidStateException
/**
 * Initializes a new InvalidStateException.
 */
    (errorDetails: IndyError) :
    IndyException(message + errorDetails.buildMessage(), ErrorCode.CommonInvalidState.value()) {
    companion object {
        private const val serialVersionUID = -1741244553102207886L
        private const val message = "The SDK library experienced an unexpected internal error."
    }
}