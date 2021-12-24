package com.sirius.library.utils

expect class Date {

    var time : Long
    constructor()
    constructor(time: Long)

    fun formatTo(template :  String) : String

    companion object {
        fun paresDate(date :  String, format : String): Date
    }
}