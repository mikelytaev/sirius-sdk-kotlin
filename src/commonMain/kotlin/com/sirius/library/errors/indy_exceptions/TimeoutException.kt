package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when timeout happens for ledger operation.
 */
class TimeoutException
/**
 * Initializes a new TimeoutException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PoolLedgerTimeout.value()) {
    companion object {
        private const val serialVersionUID = -2318833884012610163L
        private const val message = "Timeout happens for ledger operation."
    }
}