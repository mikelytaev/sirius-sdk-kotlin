package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when the SDK experienced an IO error.
 */
class IOException
/**
 * Initializes a new IOException.
 */
    (error: IndyError) : IndyException(message + error.buildMessage(), ErrorCode.CommonIOError.value()) {
    companion object {
        private const val serialVersionUID = -1581785238453075780L
        private const val message = "An IO error occurred."
    }
}