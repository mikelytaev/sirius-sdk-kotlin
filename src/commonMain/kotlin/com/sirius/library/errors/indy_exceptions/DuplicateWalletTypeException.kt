package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to register a custom wallet type that has already been registered.
 */
class DuplicateWalletTypeException
/**
 * Initializes a new DuplicateWalletTypeException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletTypeAlreadyRegisteredError.value()) {
    companion object {
        private const val serialVersionUID = -5414881660233778407L
        private const val message = "A wallet type with the specified name has already been registered."
    }
}