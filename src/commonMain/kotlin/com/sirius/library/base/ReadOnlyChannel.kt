package com.sirius.library.base

/**
 * Communication abstraction for reading data stream
 */
interface ReadOnlyChannel {
    /**
     * Read message packet
     * @param timeout Operation timeout is sec
     * @return chunk of data stream
     */
    fun read(): java.util.concurrent.CompletableFuture<ByteArray?>?
}