package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when opening a wallet while specifying a wallet type that has not been registered.
 */
class UnknownWalletTypeException
/**
 * Initializes a new UnknownWalletTypeException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletUnknownTypeError.value()) {
    companion object {
        private const val serialVersionUID = -6275711661964891560L
        private const val message = "The wallet type specified has not been registered."
    }
}