package com.sirius.library.mobile

import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq

class SearchHelper {

    companion object {
        private var searchHelper: SearchHelper? = null

        fun getInstance(): SearchHelper {
            if (searchHelper == null) {
                searchHelper = SearchHelper()
            }
            return searchHelper!!
        }
        fun cleanInstance(){
            searchHelper = null
        }
    }




    fun searchForProofRequest(proofRequest:  String, extraQuery : String? = null){
       val credentialsSearchForProofReq =
            CredentialsSearchForProofReq.open(
                SiriusSDK.getInstance().walletHelper.myWallet,
                proofRequest, extraQuery
            ).get()
    }

}