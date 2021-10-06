package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when a revocation registry is full.
 */
class RevocationRegistryFullException
/**
 * Initializes a new RevocationRegistryFullException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.AnoncredsRevocationRegistryFullError.value()) {
    companion object {
        private const val serialVersionUID = 8294079007838985455L
        private const val message =
            "The specified revocation registry is full.  Another revocation registry must be created."
    }
}