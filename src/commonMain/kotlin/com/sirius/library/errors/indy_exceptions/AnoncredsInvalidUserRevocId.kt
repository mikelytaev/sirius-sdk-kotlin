package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when a invalid user revocation index is used.
 */
class AnoncredsInvalidUserRevocId
/**
 * Initializes a new AnoncredsInvalidUserRevocId.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.AnoncredsInvalidUserRevocId.value()) {
    companion object {
        private const val serialVersionUID = 4969718227042210813L
        private const val message = "The user revocation registry index specified is invalid."
    }
}