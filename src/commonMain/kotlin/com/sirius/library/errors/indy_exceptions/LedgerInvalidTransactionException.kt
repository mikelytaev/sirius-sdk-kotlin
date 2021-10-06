package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempt to parse invalid transaction response.
 */
class LedgerInvalidTransactionException
/**
 * Initializes a new LedgerInvalidTransactionException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.LedgerInvalidTransaction.value()) {
    companion object {
        private const val serialVersionUID = -6503578332467229584L
        private const val message = "No consensus was reached during the ledger operation."
    }
}