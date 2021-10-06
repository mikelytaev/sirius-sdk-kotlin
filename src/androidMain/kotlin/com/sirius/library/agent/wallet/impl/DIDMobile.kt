/*
package com.sirius.library.agent.wallet.impl

class DIDMobile(wallet: Wallet) : AbstractDID() {
    var wallet: Wallet
    var timeoutSec = 60
    var poolMobile: PoolMobile
    fun createAndStoreMyDid(did: String?, seed: String?, cid: Boolean?): Pair<String, String>? {
        try {
            val res: DidResults.CreateAndStoreMyDidResult = Did.createAndStoreMyDid(
                wallet,
                CreateAndStoreMyDidJSONParameter(did, seed, null, cid).toJson()
            ).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            return Pair(res.getDid(), res.getVerkey())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun storeTheirDid(did: String?, verkey: String?) {
        val identityJson: JSONObject = JSONObject().put("did", did)
        if (verkey != null) identityJson.put("verkey", verkey)
        try {
            Did.storeTheirDid(wallet, identityJson.toString()).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setDidMetadata(did: String?, metadata: String?) {
        try {
            Did.setDidMetadata(wallet, did, metadata).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun listMyDidsWithMeta(): List<Any>? {
        try {
            val listDidsWithMetaJson: String =
                Did.getListMyDidsWithMeta(wallet).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            val listDidsWithMeta: JSONArray = JSONArray(listDidsWithMetaJson)
            return listDidsWithMeta.toList()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getDidMetadata(did: String?): String? {
        try {
            return Did.getDidMetadata(wallet, did).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun keyForLocalDid(did: String?): String? {
        try {
            return Did.keyForLocalDid(wallet, did).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun keyForDid(poolName: String?, did: String?): String? {
        try {
            val pool: Pool = poolMobile.getPoolHandle(poolName)
            return Did.keyForDid(pool, wallet, did).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun createKey(seed: String?): String? {
        try {
            return Crypto.createKey(wallet, CreateKeyJSONParameter(seed, null).toJson())
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun replaceKeysStart(did: String?, seed: String?): String? {
        try {
            return Did.replaceKeysStart(wallet, did, CreateKeyJSONParameter(seed, null).toJson())
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun replaceKeysApply(did: String?) {
        try {
            Did.replaceKeysApply(wallet, did).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setKeyMetadata(verkey: String?, metadata: String?) {
        try {
            Crypto.setKeyMetadata(wallet, verkey, metadata).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        */
/*
        metadata_str = json.dumps(metadata)
        await indy.did.set_key_metadata(self.__handle, verkey, metadata_str)*//*

    }

    fun getKeyMetadata(verkey: String?): String? {
        try {
            return Crypto.getKeyMetadata(wallet, verkey).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
        */
/*
        metadata_str = await indy.did.get_key_metadata(self.__handle, verkey)
        metadata = json.loads(metadata_str)*//*

    }

    fun setEndpointForDid(did: String?, address: String?, transportKey: String?) {
        try {
            Did.setEndpointForDid(wallet, did, address, transportKey)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getEndpointForDid(pooName: String?, did: String?): Pair<String, String>? {
        try {
            val pool: Pool = poolMobile.getPoolHandle(pooName)
            val res: DidResults.EndpointForDidResult =
                Did.getEndpointForDid(wallet, pool, did).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
            return Pair(res.getAddress(), res.getTransportKey())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getMyDidMeta(did: String?): Any? {
        try {
            return Did.getDidWithMeta(wallet, did).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun abbreviateVerKey(did: String?, fullVerkey: String?): String? {
        try {
            return Did.AbbreviateVerkey(did, fullVerkey).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun qualifyDid(did: String?, method: String?): String? {
        try {
            return Did.qualifyDid(wallet, did, method).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    init {
        this.wallet = wallet
        poolMobile = PoolMobile()
    }
}
*/
