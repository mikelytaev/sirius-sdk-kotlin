package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException


/**
 * Exception thrown when attempting to open Pool for witch Genesis Transactions are not compatible with set Protocol version.
 */
class PoolIncompatibleProtocolVersionException
/**
 * Initializes a new PoolIncompatibleProtocolVersionException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PoolLedgerNotCreatedError.value()) {
    companion object {
        private const val serialVersionUID = 6945180938262170499L
        private const val message = "Pool Genesis Transactions are not compatible with Protocol version."
    }
}