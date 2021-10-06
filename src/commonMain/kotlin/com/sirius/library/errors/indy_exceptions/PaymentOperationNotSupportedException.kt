package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

class PaymentOperationNotSupportedException
/**
 * Initializes a new [PaymentOperationNotSupportedException] with the specified message.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.PaymentOperationNotSupportedError.value()) {
    companion object {
        private const val serialVersionUID = -5009466707967765943L
        private const val message = "Operation is not supported for payment method"
    }
}
