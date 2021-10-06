package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when a value passed to the SDK was not structured so that the SDK could correctly process it.
 */
class InvalidStructureException
/**
 * Initializes a new InvalidStructureException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.CommonInvalidStructure.value()) {
    companion object {
        private const val serialVersionUID = -2157029980107821313L
        private const val message = "A value being processed is not valid."
    }
}