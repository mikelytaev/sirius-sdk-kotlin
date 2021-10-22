package com.sirius.library.utils

 expect class UUID() {
    companion object {
        val randomUUID: UUID
    }

     override fun toString(): String
}