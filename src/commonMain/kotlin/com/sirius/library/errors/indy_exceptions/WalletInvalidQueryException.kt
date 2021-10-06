package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when provided wallet query is invalid.
 */
class WalletInvalidQueryException
/**
 * Initializes a new WalletInvalidQueryException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletQueryError.value()) {
    companion object {
        private const val serialVersionUID = 667964860056778208L
        private const val message = "Wallet query is invalid."
    }
}
