package com.sirius.library.errors

import com.sirius.library.errors.indy_exceptions.*
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Thrown when an Indy specific error has occurred.
 */
open class IndyException : Exception {
    /**
     * Gets the SDK error code for the exception.
     *
     * @return The SDK error code used to construct the exception.
     */
    var sdkErrorCode: Int
        private set

    /**
     * Gets the SDK error message for the exception.
     */
    var sdkMessage: String?
        private set

    /**
     * Gets the SDK error backtrace for the exception.
     *
     * @return The SDK backtrace.
     */
    var sdkBacktrace // Collecting of backtrace can be enabled by:
            : String?
        private set

    //   1) setting environment variable `RUST_BACKTRACE=1`
    //   2) calling `setRuntimeConfig` API function with `collect_backtrace: true`
    val internalMessage: String
        get() = ""

    /**
     * Initializes a new IndyException with the specified message.
     *
     * @param message      The message for the exception.
     * @param sdkErrorCode The SDK error code to construct the exception from.
     */
    protected   constructor(message: String?, sdkErrorCode: Int) : super(message) {
        val errorDetails = IndyError()
        this.sdkErrorCode = sdkErrorCode
        sdkMessage = errorDetails.message
        sdkBacktrace = errorDetails.backtrace
    }

    /**
     * Initializes a new IndyException with the specified message.
     *
     * @param errorDetails The details for the exception.
     * @param sdkErrorCode The SDK error code to construct the exception from.
     */
    protected constructor(errorDetails: IndyError, sdkErrorCode: Int) : super(errorDetails.buildMessage()) {
        this.sdkErrorCode = sdkErrorCode
        sdkMessage = errorDetails.message
        sdkBacktrace = errorDetails.backtrace
    }

    open class IndyError {
        var message: String? = null
        var backtrace: String? = null

         constructor(errorDetails: JsonObject) {
            try {
                message = errorDetails.get("message")?.jsonPrimitive?.content
                backtrace = errorDetails.get("backtrace")?.jsonPrimitive?.content
            } catch (ignored: Exception) {
                // Nothing to do
            }
        }

         constructor() {
            //PointerByReference errorDetailsJson = new PointerByReference();
            //	LibIndy.api.indy_get_current_error(errorDetailsJson);
            //JSONObject errorDetails = new JSONObject(errorDetailsJson.getValue().getString(0));
            try {
                val errorDetails = JsonObject(HashMap())
                message = errorDetails.get("message")?.jsonPrimitive?.content
                backtrace = errorDetails.get("backtrace")?.jsonPrimitive?.content
            } catch (ignored: Exception) {
                // Nothing to do
            }
        }

        fun buildMessage(): String {
            return " message {$message}backtrace {$backtrace}"
        }
    }

    companion object {
        private const val serialVersionUID = 2650355290834266477L

        /**
         * Initializes a new IndyException using the specified SDK error code.
         *
         * @param sdkErrorCode The SDK error code to construct the exception from.
         * @return IndyException correspondent to SDK error code
         */
        fun fromSdkError(sdkErrorCode: Int, errorObject: JSONObject): IndyException {
            val errorCode = ErrorCode.valueOf(sdkErrorCode)
            val errorDetails = IndyError(errorObject.jsonObject)
            return when (errorCode) {
                ErrorCode.CommonInvalidParam1, ErrorCode.CommonInvalidParam2, ErrorCode.CommonInvalidParam3, ErrorCode.CommonInvalidParam4, ErrorCode.CommonInvalidParam5, ErrorCode.CommonInvalidParam6, ErrorCode.CommonInvalidParam7, ErrorCode.CommonInvalidParam8, ErrorCode.CommonInvalidParam9, ErrorCode.CommonInvalidParam10, ErrorCode.CommonInvalidParam11,ErrorCode.CommonInvalidParam12, ErrorCode.CommonInvalidParam13, ErrorCode.CommonInvalidParam14 -> InvalidParameterException(
                    errorDetails,
                    sdkErrorCode
                )
                ErrorCode.CommonInvalidState -> InvalidStateException(errorDetails)
                ErrorCode.CommonInvalidStructure -> InvalidStructureException(errorDetails)
                ErrorCode.CommonIOError -> IOException(errorDetails)
                ErrorCode.WalletInvalidHandle -> InvalidWalletException(errorDetails)
                ErrorCode.WalletUnknownTypeError -> UnknownWalletTypeException(errorDetails)
                ErrorCode.WalletTypeAlreadyRegisteredError -> DuplicateWalletTypeException(errorDetails)
                ErrorCode.WalletAlreadyExistsError -> WalletExistsException(errorDetails)
                ErrorCode.WalletNotFoundError -> WalletNotFoundException(errorDetails)
                ErrorCode.WalletInputError -> WalletInputException(errorDetails)
                ErrorCode.WalletDecodingError -> WalletDecodingException(errorDetails)
                ErrorCode.WalletStorageError -> WalletStorageException(errorDetails)
                ErrorCode.WalletEncryptionError -> WalletEncryptionException(errorDetails)
                ErrorCode.WalletItemNotFound -> WalletItemNotFoundException(errorDetails)
                ErrorCode.WalletItemAlreadyExists -> WalletItemAlreadyExistsException(errorDetails)
                ErrorCode.WalletQueryError -> WalletInvalidQueryException(errorDetails)
                ErrorCode.WalletIncompatiblePoolError -> WrongWalletForPoolException(errorDetails)
                ErrorCode.WalletAlreadyOpenedError -> WalletAlreadyOpenedException(errorDetails)
                ErrorCode.WalletAccessFailed -> WalletAccessFailedException(errorDetails)
                ErrorCode.PoolLedgerNotCreatedError -> PoolConfigNotCreatedException(errorDetails)
                ErrorCode.PoolLedgerInvalidPoolHandle -> InvalidPoolException(errorDetails)
                ErrorCode.PoolLedgerTerminated -> PoolLedgerTerminatedException(errorDetails)
                ErrorCode.LedgerNoConsensusError -> ConsensusException(errorDetails)
                ErrorCode.LedgerInvalidTransaction -> LedgerInvalidTransactionException(errorDetails)
                ErrorCode.LedgerSecurityError -> LedgerSecurityException(errorDetails)
                ErrorCode.PoolLedgerConfigAlreadyExistsError -> PoolLedgerConfigExistsException(errorDetails)
                ErrorCode.PoolLedgerTimeout -> TimeoutException(errorDetails)
                ErrorCode. PoolIncompatibleProtocolVersion -> PoolIncompatibleProtocolVersionException(errorDetails)
                ErrorCode.LedgerNotFound -> LedgerNotFoundException(errorDetails)
                ErrorCode.AnoncredsRevocationRegistryFullError -> RevocationRegistryFullException(errorDetails)
                ErrorCode.AnoncredsInvalidUserRevocId -> AnoncredsInvalidUserRevocId(errorDetails)
                ErrorCode. AnoncredsMasterSecretDuplicateNameError -> DuplicateMasterSecretNameException(errorDetails)
                ErrorCode.AnoncredsProofRejected -> ProofRejectedException(errorDetails)
                ErrorCode. AnoncredsCredentialRevoked -> CredentialRevokedException(errorDetails)
                ErrorCode.AnoncredsCredDefAlreadyExistsError -> CredDefAlreadyExistsException(errorDetails)
                ErrorCode. UnknownCryptoTypeError -> UnknownCryptoException(errorDetails)
                ErrorCode. DidAlreadyExistsError -> DidAlreadyExistsException(errorDetails)
                ErrorCode. UnknownPaymentMethod -> UnknownPaymentMethodException(errorDetails)
                ErrorCode. IncompatiblePaymentError -> IncompatiblePaymentException(errorDetails)
                ErrorCode. InsufficientFundsError -> InsufficientFundsException(errorDetails)
                ErrorCode. ExtraFundsError -> ExtraFundsException(errorDetails)
                ErrorCode.PaymentSourceDoesNotExistError -> PaymentSourceDoesNotExistException(errorDetails)
                ErrorCode. PaymentOperationNotSupportedError -> PaymentOperationNotSupportedException(errorDetails)
                ErrorCode. TransactionNotAllowedError -> TransactionNotAllowedException(errorDetails)
                else -> {
                    val message: String = "An unmapped error with the code ${sdkErrorCode} was returned by the SDK. }"

                    IndyException(message, sdkErrorCode)
                }
            }
        }
    }
}


