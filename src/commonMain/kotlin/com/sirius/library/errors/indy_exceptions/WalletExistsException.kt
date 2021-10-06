package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to create a wallet using the same name as a wallet that already exists.
 */
class WalletExistsException
/**
 * Initializes a new WalletExistsException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletAlreadyExistsError.value()) {
    companion object {
        private const val serialVersionUID = 1829076830401150667L
        private const val message = "A wallet with the specified name already exists."
    }
}