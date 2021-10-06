package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to use a credential that has been revoked.
 */
class CredentialRevokedException
/**
 * Initializes a new CredentialRevokedException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.AnoncredsCredentialRevoked.value()) {
    companion object {
        private const val serialVersionUID = 8269746965241515882L
        private const val message = "The credential has been revoked."
    }
}