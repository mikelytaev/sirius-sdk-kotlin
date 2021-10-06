package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to open a pool using a configuration that does not exist.
 */
class PoolConfigNotCreatedException
/**
 * Initializes a new PoolConfigNotCreatedException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PoolLedgerNotCreatedError.value()) {
    companion object {
        private const val serialVersionUID = 6945180938262170499L
        private const val message =
            "The requested pool cannot be opened because it does not have an existing configuration."
    }
}