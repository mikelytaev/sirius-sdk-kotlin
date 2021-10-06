package com.sirius.library.errors

open class BaseSiriusException : Exception {
    /**
     * Initializes a new SiriusException with the specified message.
     *
     * @param message The message for the exception.
     */
    protected constructor(message: String?) : super(message) {}

    /**
     * Initializes a new SiriusException.
     */
    protected constructor() {}
}