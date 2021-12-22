package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalPool
import Indy.*
import com.sirius.library.agent.wallet.results.ErrorHandler
import com.sirius.library.utils.CompletableFutureKotlin
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.darwin.nil

actual class PoolMobile {

    actual fun getPoolHandle(name: String): LocalPool? {

        if (openedPoolRegistry.containsKey(name)) return openedPoolRegistry[name]
        try {
            val future = CompletableFutureKotlin<LocalPool?>()
             IndyPool.openPoolLedgerWithName(name = name , null) { error: NSError?, indyHandle : IndyHandle? ->
              ErrorHandler(error).handleError()
                 val pool = LocalPool(indyHandle)
                 future.complete(pool)
             }
            val pool = future.get()
            if(pool != null){
                openedPoolRegistry[name] = pool!!
            }
            return pool
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    actual companion object {
        actual var openedPoolRegistry: MutableMap<String, LocalPool> = HashMap<String, LocalPool>()
        actual fun registerPool(name: String?, genesisFilePath: String?) {
            var genesisFilePath = genesisFilePath
            /*
            try {

                genesisFilePath = java.nio.file.Paths.get(genesisFilePath).toAbsolutePath().toString()
                val createPoolLedgerConfigJSONParameter: PoolJSONParameters.CreatePoolLedgerConfigJSONParameter =
                    PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesisFilePath)
                IndyPool.run {createPoolLedgerConfigWithPoolName(name = name, createPoolLedgerConfigJSONParameter.toJson()){ error: NSError?->
                    println("error=" + error)
                }}

            } catch (e: Exception) {
                if (e.message != null) {
                    if (!e.message!!.contains("PoolLedgerConfigExists")) {
                        e.printStackTrace()
                    }
                } else {
                    e.printStackTrace()
                }
            }
            */

        }

        actual fun setProtocolVersion(version: Int) {
            try {
                IndyPool.setProtocolVersion(protocolVersion = NSNumber(version)) { error: NSError?->
                       ErrorHandler(error).handleError()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}