package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractNonSecrets
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject


expect class NonSecretsMobile(wallet: LocalWallet) : AbstractNonSecrets {

    var timeoutSec  : Long

}

