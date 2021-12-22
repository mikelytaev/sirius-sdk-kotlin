package com.sirius.library.utils

actual class Date {


    actual var time: Long = 0

    actual constructor() {
        TODO("Not yet implemented")
    }

    actual constructor(time: Long) {
        TODO("Not yet implemented")
    }

    actual fun formatTo(template: String): String {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun paresDate(date: String, format: String): Date {
            TODO("Not yet implemented")
        }
    }

}