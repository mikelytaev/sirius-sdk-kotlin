package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to open a wallet that has already been opened.
 */
class WalletAlreadyOpenedException
/**
 * Initializes a new WalletAlreadyOpenedException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletAlreadyOpenedError.value()) {
    companion object {
        private const val serialVersionUID = 3294831240096535507L
        private const val message = "The wallet is already open."
    }
}