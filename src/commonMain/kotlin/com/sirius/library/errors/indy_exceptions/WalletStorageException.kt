package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown occurred during wallet operation.
 */
class WalletStorageException
/**
 * Initializes a new WalletStorageException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletStorageError.value()) {
    companion object {
        private const val serialVersionUID = 1829076830401150667L
        private const val message = "Storage error occurred during wallet operation."
    }
}