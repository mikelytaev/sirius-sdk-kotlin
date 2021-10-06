package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to use a wallet that has been closed.
 */
class InvalidWalletException
/**
 * Initializes a new WalletClosedException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletInvalidHandle.value()) {
    companion object {
        private const val serialVersionUID = -606730416804502147L
        private const val message = "The wallet is closed or invalid and cannot be used."
    }
}
