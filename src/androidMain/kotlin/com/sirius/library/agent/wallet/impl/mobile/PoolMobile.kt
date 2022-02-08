package com.sirius.library.agent.wallet.impl.mobile

import com.sirius.library.agent.wallet.LocalPool
import com.sirius.library.mobile.PoolUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils
import org.hyperledger.indy.sdk.IndyException
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.pool.PoolJSONParameters.CreatePoolLedgerConfigJSONParameter
import java.io.*
import java.util.concurrent.ExecutionException

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

        private const val DEFAULT_POOL_NAME = "_default_pool"
        const val PROTOCOL_VERSION = 2


        @Throws(IOException::class)
        private fun createGenesisTxnFile(path: String): File {
            //  String path = IndyWallet.genesisPath + "/" + filename;

            val defaultTxns = arrayOf(
                "{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node1\",\"blskey\":\"4N8aUNHSgjQVgkpm8nhNEfDf6txHznoYREg9kirmJrkivgL4oSEimFF6nsQ6M41QvhM2Z33nves5vfSn9n1UwNFJBYtWVnHYMATn76vLuL3zU88KyeAYcHfsih3He6UHcXDxcaecHVz6jhCYz1P2UZn2bDVruL5wXpehgBfBaLKm3Ba\",\"blskey_pop\":\"RahHYiCvoNCtPTrVtP7nMC5eTYrsUA8WjXbdhNc8debh1agE9bGiJxWBXYNFbnJXoXhWFMvyqhqhRoq737YQemH5ik9oL7R4NTTCz2LEZhkgLJzB3QRQqJyBNyv7acbdHrAT8nQ9UkLbaVL9NBpnWXBTw4LEMePaSHEw66RzPNdAX1\",\"client_ip\":\"138.197.161.221\",\"client_port\":9702,\"node_ip\":\"138.197.161.221\",\"node_port\":9701,\"services\":[\"VALIDATOR\"]},\"dest\":\"Gw6pDLhcBcoQesN72qfotTgFa7cbuqZpkX3Xo6pLhPhv\"},\"metadata\":{\"from\":\"Th7MpTaRZVRYnPiabds81Y\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":1,\"txnId\":\"fea82e10e894419fe2bea7d96296a6d46f50f93f9eeda954ec461b2ed2950b62\"},\"ver\":\"1\"}",
                "{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node2\",\"blskey\":\"37rAPpXVoxzKhz7d9gkUe52XuXryuLXoM6P6LbWDB7LSbG62Lsb33sfG7zqS8TK1MXwuCHj1FKNzVpsnafmqLG1vXN88rt38mNFs9TENzm4QHdBzsvCuoBnPH7rpYYDo9DZNJePaDvRvqJKByCabubJz3XXKbEeshzpz4Ma5QYpJqjk\",\"blskey_pop\":\"Qr658mWZ2YC8JXGXwMDQTzuZCWF7NK9EwxphGmcBvCh6ybUuLxbG65nsX4JvD4SPNtkJ2w9ug1yLTj6fgmuDg41TgECXjLCij3RMsV8CwewBVgVN67wsA45DFWvqvLtu4rjNnE9JbdFTc1Z4WCPA3Xan44K1HoHAq9EVeaRYs8zoF5\",\"client_ip\":\"138.197.161.221\",\"client_port\":9704,\"node_ip\":\"138.197.161.221\",\"node_port\":9703,\"services\":[\"VALIDATOR\"]},\"dest\":\"8ECVSk179mjsjKRLWiQtssMLgp6EPhWXtaYyStWPSGAb\"},\"metadata\":{\"from\":\"EbP4aYNeTHL6q385GuVpRV\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":2,\"txnId\":\"1ac8aece2a18ced660fef8694b61aac3af08ba875ce3026a160acbc3a3af35fc\"},\"ver\":\"1\"}",
                "{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node3\",\"blskey\":\"3WFpdbg7C5cnLYZwFZevJqhubkFALBfCBBok15GdrKMUhUjGsk3jV6QKj6MZgEubF7oqCafxNdkm7eswgA4sdKTRc82tLGzZBd6vNqU8dupzup6uYUf32KTHTPQbuUM8Yk4QFXjEf2Usu2TJcNkdgpyeUSX42u5LqdDDpNSWUK5deC5\",\"blskey_pop\":\"QwDeb2CkNSx6r8QC8vGQK3GRv7Yndn84TGNijX8YXHPiagXajyfTjoR87rXUu4G4QLk2cF8NNyqWiYMus1623dELWwx57rLCFqGh7N4ZRbGDRP4fnVcaKg1BcUxQ866Ven4gw8y4N56S5HzxXNBZtLYmhGHvDtk6PFkFwCvxYrNYjh\",\"client_ip\":\"138.197.161.221\",\"client_port\":9706,\"node_ip\":\"138.197.161.221\",\"node_port\":9705,\"services\":[\"VALIDATOR\"]},\"dest\":\"DKVxG2fXXTU8yT5N7hGEbXB3dfdAnYv1JczDUHpmDxya\"},\"metadata\":{\"from\":\"4cU41vWW82ArfxJxHkzXPG\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":3,\"txnId\":\"7e9f355dffa78ed24668f0e0e369fd8c224076571c51e2ea8be5f26479edebe4\"},\"ver\":\"1\"}",
                "{\"reqSignature\":{},\"txn\":{\"data\":{\"data\":{\"alias\":\"Node4\",\"blskey\":\"2zN3bHM1m4rLz54MJHYSwvqzPchYp8jkHswveCLAEJVcX6Mm1wHQD1SkPYMzUDTZvWvhuE6VNAkK3KxVeEmsanSmvjVkReDeBEMxeDaayjcZjFGPydyey1qxBHmTvAnBKoPydvuTAqx5f7YNNRAdeLmUi99gERUU7TD8KfAa6MpQ9bw\",\"blskey_pop\":\"RPLagxaR5xdimFzwmzYnz4ZhWtYQEj8iR5ZU53T2gitPCyCHQneUn2Huc4oeLd2B2HzkGnjAff4hWTJT6C7qHYB1Mv2wU5iHHGFWkhnTX9WsEAbunJCV2qcaXScKj4tTfvdDKfLiVuU2av6hbsMztirRze7LvYBkRHV3tGwyCptsrP\",\"client_ip\":\"138.197.161.221\",\"client_port\":9708,\"node_ip\":\"138.197.161.221\",\"node_port\":9707,\"services\":[\"VALIDATOR\"]},\"dest\":\"4PS3EDQ3dW1tci1Bp6543CfuuebjFrg36kLAUcskGfaA\"},\"metadata\":{\"from\":\"TWwCRQRZ2ZHMJFn9TzLp7W\"},\"type\":\"0\"},\"txnMetadata\":{\"seqNo\":4,\"txnId\":\"aa5e817d7cc626170eca175822029339a444eb0ee8f0bd20d3b0b76e566fb008\"},\"ver\":\"1\"}"
            )
            val file = File(path)
            if (!file.exists()) {
                FileUtils.forceMkdirParent(file)
                val fw = FileWriter(file)
                for (defaultTxn in defaultTxns) {
                    fw.write(defaultTxn)
                    fw.write("\n")
                }
                fw.close()
            }
            return file
        }


        @Throws(java.lang.Exception::class)
        fun convertStreamToString(`is`: InputStream?): String {
            val reader = BufferedReader(InputStreamReader(`is`))
            val sb = StringBuilder()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
            return sb.toString()
        }

        @Throws(java.lang.Exception::class)
        fun getStringFromFile(filePath: String?): String? {
            val fl = File(filePath)
            val fin = FileInputStream(fl)
            val ret = convertStreamToString(fin)
            //Make sure you close all streams.
            fin.close()
            return ret
        }

        @Throws(
            IOException::class,
            InterruptedException::class,
            ExecutionException::class,
            IndyException::class
        )
        fun createPoolLedgerConfig(name: String, path: String): String? {
            //File genesisTxnFile = new File(path);
            val genesisTxnFile = createGenesisTxnFile(path)
            val createPoolLedgerConfigJSONParameter = CreatePoolLedgerConfigJSONParameter(genesisTxnFile.absolutePath)

            println("createPoolLedgerConfigJSONParameter e="+createPoolLedgerConfigJSONParameter.toJson())
            Pool.createPoolLedgerConfig(
                name,
                createPoolLedgerConfigJSONParameter.toJson()
            ).get()
            return name
        }

        actual var openedPoolRegistry: MutableMap<String, LocalPool> = java.util.concurrent.ConcurrentHashMap<String, LocalPool>()
        actual fun registerPool(name: String?, genesisFilePath: String?) {
            println("registerPool + name="+name+" genesisFilePath="+genesisFilePath)
            try {
                createPoolLedgerConfig(name ?:"default", genesisFilePath?:"" )
              /*  genesisFilePath = java.nio.file.Paths.get(genesisFilePath).toAbsolutePath().toString()
                val createPoolLedgerConfigJSONParameter: PoolJSONParameters.CreatePoolLedgerConfigJSONParameter =
                    PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesisFilePath)
                Pool.createPoolLedgerConfig(name, createPoolLedgerConfigJSONParameter.toJson())
                    .get(60, java.util.concurrent.TimeUnit.SECONDS)*/
            } catch (e: java.lang.Exception) {
                println("registerPool e="+e.message)
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