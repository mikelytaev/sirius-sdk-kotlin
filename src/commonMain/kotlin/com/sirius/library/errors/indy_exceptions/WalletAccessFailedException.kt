package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Attempt to open encrypted wallet with invalid credentials
 */
class WalletAccessFailedException
/**
 * Initializes a new WalletAccessFailedException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletAccessFailed.value()) {
    companion object {
        private const val serialVersionUID = 3294831240096535507L
        private const val message = "The wallet security error."
    }
}