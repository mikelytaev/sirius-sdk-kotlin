package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalPool
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.pool.PoolJSONParameters

actual class PoolMobile {
    actual fun getPoolHandle(name: String): LocalPool? {
        if (openedPoolRegistry.containsKey(name)) return openedPoolRegistry[name]
        try {
            val pool: Pool = Pool.openPoolLedger(name, null).get()
            openedPoolRegistry[name] = pool
            return pool
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    actual companion object {
        actual var openedPoolRegistry: MutableMap<String, LocalPool> = java.util.concurrent.ConcurrentHashMap<String, LocalPool>()
        actual fun registerPool(name: String?, genesisFilePath: String?) {
            var genesisFilePath = genesisFilePath
            try {
                genesisFilePath = java.nio.file.Paths.get(genesisFilePath).toAbsolutePath().toString()
                val createPoolLedgerConfigJSONParameter: PoolJSONParameters.CreatePoolLedgerConfigJSONParameter =
                    PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesisFilePath)
                Pool.createPoolLedgerConfig(name, createPoolLedgerConfigJSONParameter.toJson())
                    .get(60, java.util.concurrent.TimeUnit.SECONDS)
            } catch (e: java.lang.Exception) {
                if (e.message != null) {
                    if (!e.message!!.contains("PoolLedgerConfigExists")) {
                        e.printStackTrace()
                    }
                } else {
                    e.printStackTrace()
                }
            }
        }

        actual fun setProtocolVersion(version: Int) {
            try {
                Pool.setProtocolVersion(version).get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}