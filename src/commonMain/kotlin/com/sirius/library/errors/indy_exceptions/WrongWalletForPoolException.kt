package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.ErrorCode
import com.sirius.library.errors.IndyException

/**
 * Exception thrown when attempting to use a wallet with a pool other than the pool the wallet was created for.
 */
class WrongWalletForPoolException
/**
 * Initializes a new WrongWalletForPoolException.
 */
    (error: IndyError) :
    IndyException(message + error.buildMessage(), ErrorCode.WalletIncompatiblePoolError.value()) {
    companion object {
        private const val serialVersionUID = -8931044806844925321L
        private const val message = "The wallet specified is not compatible with the open pool."
    }
}