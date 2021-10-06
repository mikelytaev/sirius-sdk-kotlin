package com.sirius.library.utils

class Date {

    constructor()  {
        this.time = 0
    }



    constructor(time: Long)  {
        this.time = time
    }

     var time : Long = 0


    fun formatTo(template :  String) : String{
        return ""
    }

    companion object {
        fun paresDate(date :  String, format : String): Date{
            return Date()
        }
    }
}