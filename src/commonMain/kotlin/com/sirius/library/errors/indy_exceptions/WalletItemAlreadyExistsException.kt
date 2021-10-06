package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when add record operation is used with record name that already exists.
 */
class WalletItemAlreadyExistsException
/**
 * Initializes a new WalletItemNotFoundException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletItemAlreadyExists.value()) {
    companion object {
        private const val serialVersionUID = 667964860056778208L
        private const val message = "Item already exists."
    }
}
