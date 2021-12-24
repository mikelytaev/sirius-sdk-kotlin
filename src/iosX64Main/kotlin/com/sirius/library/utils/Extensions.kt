package com.sirius.library.utils

import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import platform.Foundation.NSData
import platform.Foundation.create

fun ByteArray.toNSData() : NSData{
    val messData = memScoped{
        NSData.create(bytes = allocArrayOf(this@toNSData), length = size.toULong())
    }
    return messData
}
