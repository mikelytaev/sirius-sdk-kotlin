package com.sirius.library.utils

actual class UUID actual constructor() {

    var uuid : java.util.UUID? = null


    constructor(  uuid : java.util.UUID) : this() {
        this.uuid =uuid
    }



    actual companion object {
        actual val randomUUID: UUID
            get() = UUID(java.util.UUID.randomUUID())
    }

    actual override fun toString(): String {
        return this.uuid.toString()
    }
}