package com.sirius.library.base

import com.sirius.library.utils.CompletableFuture

/**
 * Communication abstraction for reading data stream
 */
interface ReadOnlyChannel {
    /**
     * Read message packet
     * @param timeout Operation timeout is sec
     * @return chunk of data stream
     */
    fun read(): CompletableFuture<ByteArray?>?
}