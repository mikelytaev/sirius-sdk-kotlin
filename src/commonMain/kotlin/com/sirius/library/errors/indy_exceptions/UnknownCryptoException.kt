package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to use a crypto format unrecognized by the SDK.
 */
class UnknownCryptoException
/**
 * Initializes a new UnknownCryptoException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.UnknownCryptoTypeError.value()) {
    companion object {
        private const val serialVersionUID = 4955846571270561834L
        private const val message = "An unknown crypto format has been used for a DID entity key."
    }
}