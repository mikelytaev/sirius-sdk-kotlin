package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when decoding of wallet data during input/output failed.
 */
class WalletDecodingException
/**
 * Initializes a new WalletDecodingException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletDecodingError.value()) {
    companion object {
        private const val serialVersionUID = 1829076830401150667L
        private const val message = "Decoding of wallet data during input/output failed."
    }
}