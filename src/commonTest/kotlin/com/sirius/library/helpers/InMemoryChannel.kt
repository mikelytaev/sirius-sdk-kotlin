package com.sirius.library.helpers

import com.sirius.library.base.ReadOnlyChannel
import com.sirius.library.base.WriteOnlyChannel

class InMemoryChannel : ReadOnlyChannel, WriteOnlyChannel {
    var cf: java.util.concurrent.CompletableFuture<ByteArray> = java.util.concurrent.CompletableFuture<ByteArray>()
    fun read(): java.util.concurrent.CompletableFuture<ByteArray> {
        return cf
    }

    fun write(data: ByteArray?): Boolean {
        if (cf.isDone()) cf = java.util.concurrent.CompletableFuture<ByteArray>()
        cf.complete(data)
        return true
    }
}