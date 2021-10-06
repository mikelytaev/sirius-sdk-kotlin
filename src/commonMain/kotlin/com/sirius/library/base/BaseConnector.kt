package com.sirius.library.base

/**
 * Transport Layer.
 *
 * Connectors operate as transport provider for high-level abstractions
 */
abstract class BaseConnector : ReadOnlyChannel, WriteOnlyChannel {
    /**
     * Open communication
     */
    abstract fun open()

    /**
     * Close communication
     */
    abstract fun close()
    abstract val isOpen: Boolean
}