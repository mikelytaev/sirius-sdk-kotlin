package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to use a poll that has already been closed.
 */
class InvalidPoolException
/**
 * Initializes a new PoolClosedException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PoolLedgerInvalidPoolHandle.value()) {
    companion object {
        private const val serialVersionUID = 7124250084655044699L
        private const val message = "The pool is closed or invalid and cannot be used."
    }
}