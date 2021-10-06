package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

class ExtraFundsException
/**
 * Initializes a new [ExtraFundsException] with the specified message.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.ExtraFundsError.value()) {
    companion object {
        private const val serialVersionUID = 6397499268992083529L
        private const val message = "Extra funds on inputs"
    }
}
