package com.sirius.library.base

/**
 * Communication abstraction for writing data stream
 */
interface WriteOnlyChannel {
    /**
     * Write message packet
     * @param data message packet
     * @return  True if success ele False
     */
    fun write(data: ByteArray?): Boolean
}