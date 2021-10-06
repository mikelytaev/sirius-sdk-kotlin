package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when a pool ledger has been terminated.
 */
class PoolLedgerTerminatedException
/**
 * Initializes a new PoolLedgerTerminatedException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PoolLedgerTerminated.value()) {
    companion object {
        private const val serialVersionUID = 768482152424714514L
        private const val message = "The pool ledger was terminated."
    }
}