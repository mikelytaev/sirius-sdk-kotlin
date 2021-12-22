package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractDID
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import org.hyperledger.indy.sdk.crypto.Crypto
import org.hyperledger.indy.sdk.crypto.CryptoJSONParameters
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.did.DidResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet

actual class DIDMobile actual constructor(val wallet: LocalWallet) : AbstractDID() {

    actual var timeoutSec = 60L
    actual var poolMobile: PoolMobile = PoolMobile()
    override fun createAndStoreMyDid(did: String?, seed: String?, cid: Boolean?): Pair<String, String> {
        try {
            val res: DidResults.CreateAndStoreMyDidResult = Did.createAndStoreMyDid(
                wallet,
                DidJSONParameters.CreateAndStoreMyDidJSONParameter(did, seed, null, cid).toJson()
            ).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
            return Pair(res.getDid(), res.getVerkey())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Pair<String, String>("","")
    }

    override fun storeTheirDid(did: String?, verkey: String?) {
        val identityJson: JSONObject = JSONObject().put("did", did)
        if (verkey != null) identityJson.put("verkey", verkey)
        try {
            Did.storeTheirDid(wallet, identityJson.toString()).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun setDidMetadata(did: String?, metadata: String?) {
        try {
            Did.setDidMetadata(wallet, did, metadata).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun listMyDidsWithMeta(): List<Any?>? {
        try {
            val listDidsWithMetaJson: String =
                Did.getListMyDidsWithMeta(wallet).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
            val listDidsWithMeta: JSONArray = JSONArray(listDidsWithMetaJson)
            return listDidsWithMeta.toList()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getDidMetadata(did: String?): String? {
        try {
            return Did.getDidMetadata(wallet, did).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun keyForLocalDid(did: String?): String? {
        try {
            return Did.keyForLocalDid(wallet, did).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun keyForDid(poolName: String?, did: String?): String? {
        try {
            val pool: Pool?= poolMobile.getPoolHandle(poolName?:"")
            return Did.keyForDid(pool, wallet, did).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun createKey(seed: String?): String? {
        try {
            return Crypto.createKey(wallet, CryptoJSONParameters.CreateKeyJSONParameter(seed, null).toJson())
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun replaceKeysStart(did: String?, seed: String?): String? {
        try {
            return Did.replaceKeysStart(wallet, did, CryptoJSONParameters.CreateKeyJSONParameter(seed, null).toJson())
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun replaceKeysApply(did: String?) {
        try {
            Did.replaceKeysApply(wallet, did).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun setKeyMetadata(verkey: String?, metadata: String?) {
        try {
            Crypto.setKeyMetadata(wallet, verkey, metadata).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


    }

    override fun getKeyMetadata(verkey: String?): String? {
        try {
            return Crypto.getKeyMetadata(wallet, verkey).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun setEndpointForDid(did: String?, address: String?, transportKey: String?) {
        try {
            Did.setEndpointForDid(wallet, did, address, transportKey)
                .get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun getEndpointForDid(pooName: String?, did: String?): Pair<String, String>? {
        try {
            val pool: Pool? = poolMobile.getPoolHandle(pooName?:"")
            val res: DidResults.EndpointForDidResult =
                Did.getEndpointForDid(wallet, pool, did).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
            return Pair(res.getAddress(), res.getTransportKey())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getMyDidMeta(did: String?): Any? {
        try {
            return Did.getDidWithMeta(wallet, did).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun abbreviateVerKey(did: String?, fullVerkey: String?): String? {
        try {
            return Did.AbbreviateVerkey(did, fullVerkey).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun qualifyDid(did: String?, method: String?): String? {
        try {
            return Did.qualifyDid(wallet, did, method).get(timeoutSec.toLong(), java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

}