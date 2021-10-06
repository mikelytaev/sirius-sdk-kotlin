package com.sirius.library.errors.indy_exceptions

import com.sirius.library.errors.IndyException

/**
 * Exception thrown when the SDK reports than an invalid parameter was passed to it.
 */
class InvalidParameterException(errorDetails: IndyError, sdkErrorCode: Int) :
    IndyException(buildMessage(errorDetails, sdkErrorCode), sdkErrorCode) {
    /**
     * Gets the index of the parameter that was incorrect.
     *
     * @return The index of the parameter that was incorrect.
     */
    val parameterIndex: Int

    companion object {
        private const val serialVersionUID = -1802344846222826490L

        /**
         * Gets the index of the parameter the SDK reported as incorrect.
         * @param sdkErrorCode
         * @return
         */
        private fun getParamIndex(sdkErrorCode: Int): Int {
            if(sdkErrorCode in 100..111){
                return sdkErrorCode - 99
            }
           return 0;
        }

        /**
         * Constructs the error message for the exception from the SDK error code.
         *
         * @param sdkErrorCode The SDK error code.
         * @return A message indicating which parameter was incorrect.
         */
        private fun buildMessage(errorDetails: IndyError, sdkErrorCode: Int): String {
            val code = getParamIndex(sdkErrorCode)
            return "The value passed to parameter $code is not valid."+ errorDetails.buildMessage()
        }
    }

    /**
     * Initializes a new InvalidParameterException with the SDK error code.
     *
     * @param sdkErrorCode The SDK error code.
     */
    init {
        parameterIndex = getParamIndex(sdkErrorCode)
    }
}
