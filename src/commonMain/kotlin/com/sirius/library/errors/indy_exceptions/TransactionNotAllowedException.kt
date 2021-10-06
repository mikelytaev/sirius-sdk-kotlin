package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

class TransactionNotAllowedException
/**
 * Initializes a new [TransactionNotAllowedException] with the specified message.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.TransactionNotAllowedError.value()) {
    companion object {
        private const val serialVersionUID = 6397499268992083529L
        private const val message = "The transaction is not allowed to a requester"
    }
}
