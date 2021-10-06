package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to open Pool for witch Genesis Transactions are not compatible with set Protocol version.
 */
class LedgerNotFoundException
/**
 * Initializes a new PoolIncompatibleProtocolVersionException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.LedgerNotFound.value()) {
    companion object {
        private const val serialVersionUID = 7935181938462170500L
        private const val message = "Item not found on ledger exception."
    }
}