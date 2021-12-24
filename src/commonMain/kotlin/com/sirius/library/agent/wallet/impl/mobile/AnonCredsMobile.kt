
package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractAnonCreds
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject


expect class AnonCredsMobile(wallet: LocalWallet) : AbstractAnonCreds {
    var timeoutSec : Long
}

