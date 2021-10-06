package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when a transaction cannot be sent to to insufficient privileges.
 */
class LedgerSecurityException
/**
 * Initializes a new LedgerSecurityException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.LedgerSecurityError.value()) {
    companion object {
        private const val serialVersionUID = 1695822815015877550L
        private const val message =
            "The transaction cannot be sent as the privileges for the current pool connection don't allow it."
    }
}