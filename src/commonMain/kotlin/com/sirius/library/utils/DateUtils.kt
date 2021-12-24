package com.sirius.library.utils

class DateUtils {



    companion object {
        const val DATEFORMAT_YYYY_MM_DDT_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss"
        const val PATTERN_ddMMyyyyHHmmss = "dd.MM.yyyy HH:mm:ss"
        const  val PATTERN_ddMMyyyyHHmm = "dd.MM.yyyy HH:mm"
        const  val PATTERN_HHmmddMM = "HH:mm dd.MM"
        const  val PATTERN_ddMMyyyy_COMMA = "dd.MM.yyyy"
        const  val PATTERN_HHmm = "HH:mm"
        const  val DATEFORMAT_HH_MM = "HH:mm"
        const  val DATEFORMAT_HH_MM_SS = "HH:mm:ss"
        const  val PATTERN_HH = "HH"
        const val PATTERN_MM = "mm"
        const val DATEFORMAT_DD_LLL_YYYY = "dd LLL yyyy"
        const val DATEFORMAT_DD_LLL = "dd LLL"

        //Wed, 30-May-2018 19:25:25 GMT
        //Wed, 30 Aug 2018 00:00:00 GMT
        const   val PATTERN_ddMMM_COOKIE = "EEE, dd-MMM-yyyy HH:mm:ss z"
        const val PATTERN_ddMMM_COOKIE2 = "EEE, dd MMM yyyy HH:mm:ss z"
        const val PATTERN_PARSEGSON = "yyyy-MM-dd HH:mm:ss"
        const  val PATTERN_ddMMM_yyyy = "dd MMM, yyyy"
        const  val PATTERN_ddMMMyyyy = "dd MMM yyyy"
        const  val PATTERN_ddMMMMyyyy = "dd MMMM yyyy"
        const  val PATTERN_ddMMMyyyyHHmm = "dd MMM yyyy HH:mm"
        const  val PATTERN_E = "E"
        const val DATEFORMAT_EEEE = "EEEE"
        const  val PATTERN_EE = "EE"
        const  val PATTERN_EEE = "EEE"
        const val PATTERN_EEEE = "EEEE"
        const  val PATTERN_EEddMMM_yyyy = "EE, dd MMM, yyyy"
        const  val PATTERN_ddMMM_yyyy_EEEE = "dd.MM.yyyy, EEEE"

        //13.01.2017 16:17
        const  val PATTERN_EEddMMM = "EE, MMM dd"
        const  val PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss"
        const  val PATTERN_DATETIME_DOT = "dd.MM.yyyy HH:mm"
        const  val PATTERN_DATE = "yyyy-MM-dd"
        const  val PATTERN_TIMELINE = "MMM yyyy"
        const val PATTERN_TIMELINE2 = "MMM  dd yyyy"
        const  val PATTERN_DAY_MONTH = "dd MMMM"
        const  val PATTERN_DAY_MONTH_IN_TIME = "dd MMMM в HH:mm"
        const  val PATTERN_ROSTER_STATUS_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
        const val PATTERN_ROSTER_STATUS_RESPONSE2 = "yyyy-MM-dd HH:mm:ss.SSSSSSZ"

        // 2019-01-15 18:42:01Z
        const  val PATTERN_INDY_MESSAGE = "yyyy-MM-dd HH:mm:ssZ"
        const   val PATTERN_INDY_MESSAGE2 = "yyyy-MM-dd'T'HH:mm:ssZ"
    }
}