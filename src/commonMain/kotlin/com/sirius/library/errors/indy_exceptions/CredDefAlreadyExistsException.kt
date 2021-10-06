package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when an anoncreds accumulator is full.
 */
class CredDefAlreadyExistsException
/**
 * Initializes a new CredDefAlreadyExistsException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.AnoncredsCredDefAlreadyExistsError.value()) {
    companion object {
        private const val serialVersionUID = -6792822612990030627L
        private const val message = "The anoncreds accumulator is full."
    }
}