package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when requesting a value from a wallet that does not contain the specified key.
 */
class WalletItemNotFoundException
/**
 * Initializes a new WalletItemNotFoundException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletItemNotFound.value()) {
    companion object {
        private const val serialVersionUID = 667964860056778208L
        private const val message = "No value with the specified key exists in the wallet from which it was requested."
    }
}