package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to create a pool configuration with the same name as one that already exists.
 */
class PoolLedgerConfigExistsException
/**
 * Initializes a new PoolLedgerConfigExistsException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PoolLedgerConfigAlreadyExistsError.value()) {
    companion object {
        private const val serialVersionUID = 2032790158242533689L
        private const val message = "A pool ledger configuration already exists with the specified name."
    }
}