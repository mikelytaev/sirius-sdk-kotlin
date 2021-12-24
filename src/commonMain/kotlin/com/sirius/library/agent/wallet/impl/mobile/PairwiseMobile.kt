package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractPairwise
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject


expect class PairwiseMobile(wallet: LocalWallet, nonSecretsMobile: NonSecretsMobile) : AbstractPairwise {

    var timeoutSec : Long
    val STORAGE_TYPE : String
    val CONST_VALUE : String
    val DEFAULT_FETCH_LIMIT : Int
    var nonSecretsMobile: NonSecretsMobile

}

