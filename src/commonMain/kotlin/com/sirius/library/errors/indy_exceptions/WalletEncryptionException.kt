package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown occurred during encryption-related operations.
 */
class WalletEncryptionException
/**
 * Initializes a new WalletEncryptionException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletEncryptionError.value()) {
    companion object {
        private const val serialVersionUID = 1829076830401150667L
        private const val message = "Error during encryption-related operations."
    }
}