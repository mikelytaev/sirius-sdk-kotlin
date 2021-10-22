package com.sirius.library.mobile.models

class WalletRecordSearch {
    var totalCount :Int? =null
    var records : List<WalletRecord>? = listOf()

    class WalletRecord{
        var type:String? =null
        var id : String?=null
        var value :String? = null
        var tags : String? = null
    }

}

