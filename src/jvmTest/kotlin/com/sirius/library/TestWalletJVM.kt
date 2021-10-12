package com.sirius.library

import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.wallet.AbstractWallet
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.agent.wallet.abstract_wallet.model.CacheOptions
import com.sirius.library.agent.wallet.abstract_wallet.model.NYMRole
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.helpers.ConfTest
import com.sirius.library.utils.*
import kotlin.test.*

class TestWalletJVM {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
    }

    @Test
    fun testCryptoPackMessage() {
        val agent1: CloudAgent = confTest.agent1()
        val agent2: CloudAgent = confTest.agent2()
        agent1.open()
        agent2.open()
        val walletSender: AbstractWallet? = agent1.getWalleti()
        val walletRecipient: AbstractWallet? = agent2.getWalleti()
        val verkeySender: String? = walletSender?.crypto?.createKey()
        val verkeyRecipient: String?= walletRecipient?.crypto?.createKey()
        assertNotNull(verkeySender)
        assertNotNull(verkeyRecipient)
        val verKeyList: MutableList<String> = ArrayList<String>()
        verKeyList.add(verkeyRecipient)
        val message: JSONObject = JSONObject()
        message.put("content", "Hello!")
        //1: anon crypt mode
        val messageWired: ByteArray? = walletSender.crypto.packMessage(message, verKeyList)
        val unpackedMessage: String? = walletRecipient.crypto.unpackMessage(messageWired)
        val jsonObject: JSONObject = JSONObject(unpackedMessage)
        val messObjUnpacked: JSONObject? = jsonObject.getJSONObject("message")
        assertEquals(message.toString(), messObjUnpacked.toString())
        //2: auth crypt mode
        val messageWired2: ByteArray? = walletSender.crypto.packMessage(message, verKeyList, verkeySender)
        val unpackedMessage2: String? = walletRecipient.crypto.unpackMessage(messageWired2)
        val jsonObject2:JSONObject = JSONObject(unpackedMessage2)
        val messObjUnpacked2: JSONObject? = jsonObject2.getJSONObject("message")
        assertEquals(message.toString(), messObjUnpacked2.toString())
        assertNotEquals(messageWired2, messageWired)
        agent1.close()
        agent2.close()
    }

    @Test
    fun testCryptoSign() {
        val agent1: CloudAgent = confTest.agent1()
        val agent2: CloudAgent = confTest.agent2()
        agent1.open()
        agent2.open()
        val walletSigner: AbstractWallet? = agent1.getWalleti()
        val walletVerifier: AbstractWallet? = agent2.getWalleti()
        val keySigner: String? = walletSigner?.crypto?.createKey()
        val codec = StringCodec()
        val message: JSONObject = JSONObject()
        message.put("content", "Hello!")
        val messageBytes: ByteArray = codec.fromASCIIStringToByteArray(message.toString())
        val signature: ByteArray? = walletSigner?.crypto?.cryptoSign(keySigner, messageBytes)
        val isOk: Boolean = walletVerifier?.crypto?.cryptoVerify(keySigner, messageBytes, signature) ?: false
        assertTrue(isOk)
        val keySigner2: String? = walletSigner?.crypto?.createKey()
        val brokenSignature: ByteArray? = walletSigner?.crypto?.cryptoSign(keySigner, messageBytes)
        val isOk2: Boolean = walletVerifier?.crypto?.cryptoVerify(keySigner2, messageBytes, brokenSignature) ?: false
        assertFalse(isOk2)
        agent1.close()
        agent2.close()
    }

    @Test
    fun testDidMaintenance() {
        val agent1: CloudAgent = confTest.agent1()
        agent1.open()

        //1: Create Key
        val randomKey: String? = agent1.getWalleti()?.did?.createKey()
        assertNotNull(randomKey)

        // 2: Set metadata
        val metadataObject: JSONObject = JSONObject()
        metadataObject.put("key1", "value1")
        metadataObject.put("key2", "value2")
        agent1.getWalleti()?.did?.setKeyMetadata(randomKey, metadataObject.toString())
        val actualMetadata: String? = agent1.getWalleti()?.did?.getKeyMetadata(randomKey)
        assertEquals(metadataObject.toString(), actualMetadata)

        // 3:  Create DID + Verkey
        val (first)  = agent1.getWalleti()?.did?.createAndStoreMyDid() ?: Pair("","")
        val fully: String = agent1.getWalleti()?.did?.qualifyDid(first, "peer") ?:"1"
        assertTrue(fully.contains(first))


        // 4:  Replace verkey
        val verkeyNew: String? = agent1.getWalleti()?.did?.replaceKeysStart(fully)
        assertNotNull(verkeyNew)
        val metadataList: List<Any?> = agent1.getWalleti()?.did?.listMyDidsWithMeta() ?: listOf<Any?>()
        println("metadataList=$metadataList")
        assertNotNull(metadataList)
        var anyTempVerkey = false
        for (i in metadataList.indices) {
            val m = metadataList[i]
            if (m is JSONObject) {
                val tempVerKey: String? = (m as JSONObject).optString("tempVerkey")
                if (verkeyNew == tempVerKey) {
                    anyTempVerkey = true
                }
            }
            println("m=$m")
        }
        assertTrue(anyTempVerkey)
        agent1.getWalleti()?.did?.replaceKeysApply(fully)
        val metadataList2: List<Any?> = agent1.getWalleti()?.did?.listMyDidsWithMeta() ?: listOf()

        //  assert any([m['verkey'] == verkey_new for m in metadata_list])
        assertNotNull(metadataList2)
        var anyTempVerkey2 = false
        for (i in metadataList2.indices) {
            val m = metadataList2[i]
            if (m is JSONObject) {
                val verKey: String? = (m as JSONObject).optString("verkey")
                if (verkeyNew == verKey) {
                    anyTempVerkey2 = true
                }
            }
        }
        assertTrue(anyTempVerkey2)
        val actualVerkey: String? = agent1.getWalleti()?.did?.keyForLocalDid(fully)
        assertEquals(verkeyNew, actualVerkey)
        agent1.close()
    }

    @Test
    fun testTheirDidMaintenance() {
        val agent1: CloudAgent = confTest.agent1()
        val agent2: CloudAgent = confTest.agent2()
        agent1.open()
        agent2.open()
        val walletMe: AbstractWallet? = agent1.getWalleti()
        val walletTheir: AbstractWallet? = agent2.getWalleti()
        val (first, second) = walletMe?.did?.createAndStoreMyDid() ?: Pair("","")
        val (first1, second1) = walletTheir?.did?.createAndStoreMyDid() ?: Pair("","")
        walletMe?.did?.storeTheirDid(first1, second1)
        val metadataObject: JSONObject = JSONObject()
        metadataObject.put("key1", "value1")
        metadataObject.put("key2", "value2")
        walletMe?.did?.setDidMetadata(first1, metadataObject.toString())
        val metadataExpected: String = metadataObject.toString()
        val metadataActual: String? = walletMe?.did?.getDidMetadata(first1)
        assertEquals(metadataExpected, metadataActual)
        val verkey: String? = walletMe?.did?.keyForLocalDid(first1)
        assertEquals(second1, verkey)
        val verkeyTheirNew: String? = walletTheir?.did?.replaceKeysStart(first1)
        walletTheir?.did?.replaceKeysApply(first1)
        walletMe?.did?.storeTheirDid(first1, verkeyTheirNew)
        val verkeyNew: String? = walletMe?.did?.keyForLocalDid(first1)
        assertEquals(verkeyTheirNew, verkeyNew)
        agent1.close()
        agent2.close()
    }

    @Test
    fun testRecordValue() {
        val agent1: CloudAgent = confTest.agent1()
        agent1.open()
        val value = "my-value-" + UUID.randomUUID.toString()
        val myId = "my-id-" + UUID.randomUUID.toString()
        agent1.getWalleti()?.nonSecrets?.addWalletRecord("type", myId, value)
        val opts = RetrieveRecordOptions()
        opts.checkAll()
        val valueInfo: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        assertNotNull(valueInfo)
        val valueInfoObject: JSONObject = JSONObject(valueInfo)
        assertEquals(myId, valueInfoObject.getString("id"))
        assertEquals(
            JSONObject().toString(),
            valueInfoObject.optJSONObject("tags").toString()
        )
        assertEquals(value, valueInfoObject.getString("value"))
        assertEquals("type", valueInfoObject.getString("type"))
        val valueNew = "my-new-value-" + UUID.randomUUID.toString()
        agent1.getWalleti()?.nonSecrets?.updateWalletRecordValue("type", myId, valueNew)
        val valueInfoNew: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfoObjectNew: JSONObject = JSONObject(valueInfoNew)
        assertEquals(valueNew, valueInfoObjectNew.getString("value"))
        agent1.getWalleti()?.nonSecrets?.deleteWalletRecord("type", myId)
        agent1.close()
    }

    @Test
    fun testRecordValueWithTags() {
        val agent1: CloudAgent = confTest.agent1()
        agent1.open()
        val value = "my-value-" + UUID.randomUUID.toString()
        val myId = "my-id-" + UUID.randomUUID.toString()
        val tags: JSONObject = JSONObject()
        tags.put("tag1", "val1")
        tags.put("~tag2", "val2")
        agent1.getWalleti()?.nonSecrets?.addWalletRecord("type", myId, value, tags.toString())
        val opts = RetrieveRecordOptions()
        opts.checkAll()
        val valueInfo: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfoObject: JSONObject = JSONObject(valueInfo)
        assertEquals(myId, valueInfoObject.getString("id"))
        assertEquals(tags.toString(), valueInfoObject.optJSONObject("tags").toString())
        assertEquals(value, valueInfoObject.getString("value"))
        assertEquals("type", valueInfoObject.getString("type"))
        val updTags: JSONObject = JSONObject()
        updTags.put("ext-tag", "val3")
        agent1.getWalleti()?.nonSecrets?.updateWalletRecordTags("type", myId, updTags.toString())
        val valueInfoNew: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfoNewObject: JSONObject = JSONObject(valueInfoNew)
        assertEquals(updTags.toString(), valueInfoNewObject.optJSONObject("tags").toString())
        agent1.getWalleti()?.nonSecrets?.addWalletRecordTags("type", myId, tags.toString())
        val valueInfoNew2: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfoNew2Object: JSONObject = JSONObject(valueInfoNew2)
        updTags.put("tag1", "val1")
        updTags.put("~tag2", "val2")
        assertEquals(updTags.toString(), valueInfoNew2Object.optJSONObject("tags").toString())
        val tagsList: MutableList<String> = ArrayList<String>()
        tagsList.add("ext-tag")
        agent1.getWalleti()?.nonSecrets?.deleteWalletRecord("type", myId, tagsList)
        val valueInfoNew3: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfoNew3Object: JSONObject = JSONObject(valueInfoNew3)
        assertEquals(tags.toString(), valueInfoNew3Object.optJSONObject("tags").toString())
        agent1.close()
    }

    @Test
    fun testRecordValueWithTagsThenUpdate() {
        val agent1: CloudAgent = confTest.agent1()
        agent1.open()
        val value = "my-value-" + UUID.randomUUID.toString()
        val myId = "my-id-" + UUID.randomUUID.toString()
        agent1.getWalleti()?.nonSecrets?.addWalletRecord("type", myId, value)
        val opts = RetrieveRecordOptions()
        opts.checkAll()
        val valueInfo: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfoObject: JSONObject = JSONObject(valueInfo)
        assertEquals(myId, valueInfoObject.getString("id"))
        assertEquals(
            JSONObject().toString(),
            valueInfoObject.optJSONObject("tags").toString()
        )
        assertEquals(value, valueInfoObject.getString("value"))
        assertEquals("type", valueInfoObject.getString("type"))
        val tags1: JSONObject = JSONObject()
        tags1.put("tag1", "val1")
        tags1.put("~tag2", "val2")
        agent1.getWalleti()?.nonSecrets?.updateWalletRecordTags("type", myId, tags1.toString())
        val valueInfo1: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfo1Object: JSONObject = JSONObject(valueInfo1)
        assertEquals(tags1.toString(), valueInfo1Object.optJSONObject("tags").toString())
        val tags2: JSONObject = JSONObject()
        tags1.put("tag3", "val3")
        agent1.getWalleti()?.nonSecrets?.updateWalletRecordTags("type", myId, tags2.toString())
        val valueInfo2: String? = agent1.getWalleti()?.nonSecrets?.getWalletRecord("type", myId, opts)
        val valueInfo2Object: JSONObject = JSONObject(valueInfo2)
        assertEquals(tags2.toString(), valueInfo2Object.optJSONObject("tags").toString())
        agent1.close()
    }

    @Test
    fun testRecordSearch() {
        val agent1: CloudAgent = confTest.agent1()
        agent1.open()
        val id1 = "id-1-" + UUID.randomUUID.toString()
        val id2 = "id-2-" + UUID.randomUUID.toString()
        val value1 = "value-1-" + UUID.randomUUID.toString()
        val value2 = "value-2-" + UUID.randomUUID.toString()
        val markerA = "A-" + UUID.randomUUID.toString()
        val markerB = "B-" + UUID.randomUUID.toString()
        val opts = RetrieveRecordOptions()
        opts.checkAll()
        val tags1: JSONObject = JSONObject()
        tags1.put("tag1", value1)
        tags1.put("~tag2", "5")
        tags1.put("marker", markerA)
        val tags2: JSONObject = JSONObject()
        tags2.put("tag3", "val3")
        tags2.put("~tag4", value2)
        tags2.put("marker", markerB)
        agent1.getWalleti()?.nonSecrets?.addWalletRecord("type", id1, "value1", tags1.toString())
        agent1.getWalleti()?.nonSecrets?.addWalletRecord("type", id2, "value2", tags2.toString())
        val query: JSONObject = JSONObject()
        query.put("tag1", value1)
        val (searchList, second) = agent1.getWalleti()?.nonSecrets?.walletSearch("type", query.toString(), opts) ?: Pair(
            listOf(),0)
        println("searchList=$searchList")
        println("recordsTotal.second=$second")
        assertEquals(1, second )
        for (i in 0 until searchList.size) {
            assertTrue(searchList.get(i).contains(value1))
        }
        val queryNew: JSONObject = JSONObject()
        val queryArr: JSONArray = JSONArray()
        val querytag1: JSONObject = JSONObject()
        querytag1.put("tag1", value1)
        val querytag2: JSONObject = JSONObject()
        querytag2.put("~tag4", value2)
        queryArr.put(querytag1)
        queryArr.put(querytag2)
        queryNew.put("\$or", queryArr)
        val (searchList2, second1) = agent1.getWalleti()?.nonSecrets?.walletSearch("type", queryNew.toString(), opts) ?: Pair(
            listOf(),0)
        assertEquals(searchList2.size, 1)
        assertEquals(second1, 2)
        val (first, second2) = agent1.getWalleti()?.nonSecrets?.walletSearch("type", queryNew.toString(), opts, 1000) ?: Pair(
            listOf(), 0)
        assertEquals(first.size, 2)
        assertEquals(second2, 2)
        val queryNew1: JSONObject = JSONObject()
        val queryArg: JSONObject = JSONObject()
        val queryArr1: JSONArray = JSONArray()
        queryArr1.put(markerA)
        queryArr1.put(markerB)
        queryArg.put("\$in", queryArr1)
        queryNew1.put("marker", queryArg)
        val (_, second3) = agent1.getWalleti()?.nonSecrets?.walletSearch("type", queryNew1.toString(), opts, 1000) ?:Pair(
            listOf(),0)
        assertEquals(second3 , 2)
        agent1.close()
    }

    @Test
    fun testRegisterSchemaInNetwork() {
        val agent2: CloudAgent = confTest.agent2()
        agent2.open()
        val seed = "000000000000000000000000Trustee1"
        val (first) = agent2.getWalleti()?.did?.createAndStoreMyDid(null, seed) ?: Pair("","")
        val schema_name = "schema_" + UUID.randomUUID.toString()
        val attibutes: MutableList<String> = ArrayList<String>()
        attibutes.add("attr1")
        attibutes.add("attr2")
        attibutes.add("attr3")
        val schemaIdSchema: Pair<String?, AnonCredSchema?> = agent2.getWalleti()?.anoncreds?.issuerCreateSchema(
            first, schema_name, "1.0", attibutes
        )  ?: Pair(null,null)
        println("schemaIdSchema=$schemaIdSchema")
        val response: Pair<Boolean?, String?> = agent2.getWalleti()?.ledger?.registerSchema(
            confTest.defaultNetwork(),
            first, schemaIdSchema.second
        )?: Pair(null,null)
        println("response=$response")
        assertTrue(response.first==true)
        agent2.close()
    }

    @Test
    fun testRegisterCredDefInNetwork() {
        val agent2: CloudAgent = confTest.agent2()
        val defaultNetwork: String = confTest.defaultNetwork()
        agent2.open()
        val seed = "000000000000000000000000Trustee1"
        val (first) = agent2.getWalleti()?.did?.createAndStoreMyDid(null, seed) ?: Pair("","")
        val schemaName = "schema_" + UUID.randomUUID.toString()
        val (first1, second) = agent2.getWalleti()?.anoncreds?.issuerCreateSchema(
            first, schemaName, "1.0", "attr1", "attr2", "attr3"
        ) ?: Pair(null,null)
        val (first2) = agent2.getWalleti()?.ledger?.registerSchema(
            defaultNetwork,
            first, second
        )?: Pair(null,null)
        assertTrue(first2==true)
        val opt = CacheOptions()
        val schemaForLedger: String? = agent2.getWalleti()?.cache?.getSchema(
            defaultNetwork,
            first, first1, opt
        )
        val (_, second1) = agent2.getWalleti()?.anoncreds?.issuerCreateAndStoreCredentialDef(
            first, JSONObject(schemaForLedger), "TAG"
        )?: Pair(null,null)
        val (first3) = agent2.getWalleti()?.ledger?.registerCredDef(
            defaultNetwork,
            first, JSONObject(second1)
        )?: Pair(null,null)
        assertTrue(first3==true)
        agent2.close()
    }

    @Test
    fun testNymOperationsInNetwork() {
        val agent1: CloudAgent = confTest.agent1()
        val agent2: CloudAgent = confTest.agent2()
        agent1.open()
        agent2.open()
        try {
            val steward: AbstractWallet? = agent1.getWalleti()
            val actor: AbstractWallet? = agent2.getWalleti()
            val seed = "000000000000000000000000Steward1"
            val (first) = steward?.did?.createAndStoreMyDid(null, seed) ?: Pair("","")
            val (first1, second) = actor?.did?.createAndStoreMyDid()?: Pair("","")
            val (first2, second1) = actor?.did?.createAndStoreMyDid()?: Pair("","")

            //   # Trust Anchor
            val (first3) = steward?.ledger?.writeNum(
                confTest.defaultNetwork(),
                first,
                first1, second, "Test-Trustee", NYMRole.TRUST_ANCHOR
            )?: Pair(false,"")
            assertTrue(first3)
            val (first4, second2) = steward?.ledger?.readNym(
                confTest.defaultNetwork(),
                first, first1
            )?: Pair(false,"")
           assertTrue(first4==true)
            val (first5, second3) = steward?.ledger?.readNym(
                confTest.defaultNetwork(), null,
                first1
            )?: Pair(false,"")
            assertTrue(first5==true)
            assertEquals(second2, second3)
            val okNymJson: JSONObject = JSONObject(second2)
            val role: Int? = okNymJson.getInt("role")
            assertEquals(role, NYMRole.TRUST_ANCHOR.value)

            //Common User
            val (first6) = steward?.ledger?.writeNum(
                confTest.defaultNetwork(),
                first, first2,
                second1, "CommonUser", NYMRole.COMMON_USER
            )?: Pair(false,"")
            assertTrue(first6)
            val (first7, second4) = steward?.ledger?.readNym(
                confTest.defaultNetwork(), null,
                first2
            )?: Pair(false,"")
           assertTrue(first7==true)
            val okNym3Json: JSONObject = JSONObject(second4)
            val role3: Any?= okNym3Json.get("role")
            assertEquals(role3, JSONObject.NULL)
            val (first8) = actor?.ledger?.writeNum(
                confTest.defaultNetwork(),
                first2,
                first2, second1, "ResetUser", NYMRole.RESET
            )?: Pair(false,"")
            assertTrue(first8)
            val (first9) = steward?.ledger?.readNym(
                confTest.defaultNetwork(), null,
                first2
            )?: Pair(false,"")
            assertTrue(first9==true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            agent1.close()
            agent2.close()
        }
    }

    @Test
    fun testAttributeOperationsInNetwork() {
        val agent1: CloudAgent = confTest.agent1()
        val agent2: CloudAgent = confTest.agent2()
        agent1.open()
        agent2.open()
        val steward: AbstractWallet? = agent1.getWalleti()
        val actor: AbstractWallet? = agent2.getWalleti()
        val seed = "000000000000000000000000Steward1"
        val (first) = steward?.did?.createAndStoreMyDid(null, seed) ?: Pair("","")
        val (first1, second) = actor?.did?.createAndStoreMyDid()?: Pair("","")
        val (first2) = steward?.ledger?.writeNum(
            confTest.defaultNetwork(),
            first,
            first1, second, "CommonUser", NYMRole.COMMON_USER
        )?: Pair(false,"")
        assertTrue(first2)
        val okResponse2: Pair<Boolean?, String?> = actor?.ledger?.writeAttribute(
            confTest.defaultNetwork(),
            first1,
            first1, "attribute", "value"
        )?: Pair(false,"")
        assertTrue(okResponse2.first==true)
        println(okResponse2)
        val okResponse4: Pair<Boolean?, String?> = steward?.ledger?.readAttribute(
            confTest.defaultNetwork(),
            first,
            first1, "attribute"
        )?: Pair(false,"")
        println(okResponse4)
        assertTrue(okResponse4.first==true)
        assertEquals(okResponse4.second, "value")
        agent1.close()
        agent2.close()
    }
}

