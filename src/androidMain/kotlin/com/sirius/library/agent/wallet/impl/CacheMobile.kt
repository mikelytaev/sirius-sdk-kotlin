/*
package com.sirius.library.agent.wallet.impl

class CacheMobile(wallet: Wallet) : AbstractCache() {
    var wallet: Wallet
    var timeoutSec = 60
    var poolMobile: PoolMobile
    fun getSchema(poolName: String?, submitter_did: String?, id: String?, options: CacheOptions): String? {
        try {
            val pool: Pool = poolMobile.getPoolHandle(poolName)
            val optionsStr: String = options.serialize()
            return Cache.getSchema(pool, wallet, submitter_did, id, optionsStr)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getCredDef(poolName: String?, submitter_did: String?, id: String?, options: CacheOptions): String? {
        try {
            val pool: Pool = poolMobile.getPoolHandle(poolName)
            val optionsStr: String = options.serialize()
            return Cache.getCredDef(pool, wallet, submitter_did, id, optionsStr)
                .get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun purgeSchemaCache(options: PurgeOptions) {
        try {
            Cache.purgeSchemaCache(wallet, options.serialize()).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun purgeCredDefCache(options: PurgeOptions) {
        try {
            Cache.purgeCredDefCache(wallet, options.serialize()).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    init {
        this.wallet = wallet
        poolMobile = PoolMobile()
    }
}
*/
