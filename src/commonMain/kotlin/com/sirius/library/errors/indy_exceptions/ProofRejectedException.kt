package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException


/**
 * Exception thrown when a proof has been rejected.
 */
class ProofRejectedException
/**
 * Initializes a new ProofRejectionException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.AnoncredsProofRejected.value()) {
    companion object {
        private const val serialVersionUID = -5100028213117687183L
        private const val message = "The proof has been rejected."
    }
}