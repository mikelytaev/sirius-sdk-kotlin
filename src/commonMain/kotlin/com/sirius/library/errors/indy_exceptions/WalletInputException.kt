package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException


/**
 * Exception thrown when input provided to wallet operations is considered not valid.
 */
class WalletInputException
/**
 * Initializes a new WalletInputException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletInputError.value()) {
    companion object {
        private const val serialVersionUID = 1829076830401150667L
        private const val message = "Input provided to wallet operations is considered not valid."
    }
}