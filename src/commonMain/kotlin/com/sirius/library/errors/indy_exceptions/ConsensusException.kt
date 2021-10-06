package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when consensus was not reached during a ledger operation.
 */
class ConsensusException
/**
 * Initializes a new ConsensusException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.LedgerNoConsensusError.value()) {
    companion object {
        private const val serialVersionUID = -6503578332467229584L
        private const val message = "No consensus was reached during the ledger operation."
    }
}