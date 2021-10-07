/*
package com.sirius.library.helpers

import com.sirius.library.base.ReadOnlyChannel
import com.sirius.library.base.WriteOnlyChannel
import com.sirius.library.utils.CompletableFuture

class InMemoryChannel : ReadOnlyChannel, WriteOnlyChannel {
    var cf: CompletableFuture<ByteArray> = CompletableFuture<ByteArray>()
    fun read(): CompletableFuture<ByteArray> {
        return cf
    }

    fun write(data: ByteArray?): Boolean {
        if (cf.isDone()) cf = CompletableFuture<ByteArray>()
        cf.complete(data)
        return true
    }
}*/
