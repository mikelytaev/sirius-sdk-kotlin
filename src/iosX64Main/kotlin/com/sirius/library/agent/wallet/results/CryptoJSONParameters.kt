package com.sirius.library.agent.wallet.results

import com.sirius.library.utils.JSONObject

/**
 * crypto.rs JSON parameters
 */
class CryptoJSONParameters private constructor() {
    class CreateAndStoreMyDidJSONParameter(did: String?, seed: String?, cryptoType: String?, cid: Boolean?) :
        JSONObject() {
        init {
            if (did != null) this.put("did", did)
            if (seed != null) this.put("seed", seed)
            if (cryptoType != null) this.put("crypto_type", cryptoType)
            if (cid != null) this.put("cid", cid)
        }
    }

    class CreateKeyJSONParameter(seed: String?, cryptoType: String?) : JSONObject() {
        init {
            if (seed != null) this.put("seed", seed)
            if (cryptoType != null) this.put("crypto_type", cryptoType)
        }
    }
}
