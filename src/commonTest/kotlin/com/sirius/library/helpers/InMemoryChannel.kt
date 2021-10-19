
package com.sirius.library.helpers

import com.sirius.library.base.ReadOnlyChannel
import com.sirius.library.base.WriteOnlyChannel
import com.sirius.library.utils.CompletableFutureKotlin

class InMemoryChannel : ReadOnlyChannel, WriteOnlyChannel {
    var cf: CompletableFutureKotlin<ByteArray?> = CompletableFutureKotlin<ByteArray?>()
    override fun read(): CompletableFutureKotlin<ByteArray?> {
        return cf
    }

    override fun write(data: ByteArray?): Boolean {
        if (cf.isDone()==true) cf = CompletableFutureKotlin<ByteArray?>()
        cf.complete(data)
        return true
    }
}
