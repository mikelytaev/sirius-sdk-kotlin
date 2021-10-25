package com.sirius.library.utils

actual class Date {
    actual var time: Long = 0


    actual fun formatTo(template: String): String {
        return ""
    }

    actual companion object {
        actual fun paresDate(date: String, format: String): Date {
            return Date()
        }
    }

    actual constructor(time: Long) {
        this.time = time
    }

    actual constructor() {
        time = java.util.Date().time
    }
}