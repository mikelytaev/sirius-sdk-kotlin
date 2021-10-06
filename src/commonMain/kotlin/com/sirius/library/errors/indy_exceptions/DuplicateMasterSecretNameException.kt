package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to create a master secret name that already exists.
 */
class DuplicateMasterSecretNameException
/**
 * Initializes a new DuplicateMasterSecretNameException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.AnoncredsMasterSecretDuplicateNameError.value()) {
    companion object {
        private const val serialVersionUID = 7180454759216991453L
        private const val message = "Another master-secret with the specified name already exists."
    }
}