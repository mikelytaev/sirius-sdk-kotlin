package com.sirius.library.mobile.models


import com.sirius.library.agent.pairwise.Pairwise


class PairwiseRecord {

    fun convertMetadata() {
        //pairwise = Gson().fromJson(metadata, Pairwise::class.java)
    }

    var my_did : String? = null
    var their_did : String? = null
    var metadata : String? = null
    var pairwise : Pairwise? = null
}