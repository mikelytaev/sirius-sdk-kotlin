package com.sirius.library.utils

import platform.Foundation.NSUUID

actual class UUID actual constructor() {

    var uuid : NSUUID? = null


    constructor(  uuid : NSUUID) : this() {
        this.uuid =uuid
    }


    actual companion object {
        actual val randomUUID: UUID
            get() = UUID(NSUUID())
    }

    actual override fun toString(): String {
        return uuid?.UUIDString() ?: ""
    }

}