package com.sirius.library.agent.wallet.impl.mobile

import Indy.IndyAnoncreds
import Indy.IndyHandle
import com.sirius.library.agent.wallet.LocalWallet
import com.sirius.library.agent.wallet.abstract_wallet.AbstractAnonCreds
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.agent.wallet.results.AnoncredsResults
import com.sirius.library.agent.wallet.results.ErrorHandler
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.Foundation.NSError
import platform.Foundation.NSNumber


actual class AnonCredsMobile actual constructor(val wallet: LocalWallet) : AbstractAnonCreds() {

    actual var timeoutSec: Long = 60
    override fun issuerCreateSchema(
        issuerDid: String?,
        name: String?,
        version: String?,
        attrs: List<String?>?
    ): Pair<String?, AnonCredSchema?> {
        try {
            val future = CompletableFutureKotlin<Pair<String?,String?>>()
            IndyAnoncreds.issuerCreateSchemaWithName(name, version, JSONArray(attrs).toString(), issuerDid)
            { error: NSError?, data: String?, data1: String? ->
                ErrorHandler(error).handleError()
                future.complete(Pair(data,data1))
            }
            val result = future.get()
         //   val res = Json.decodeFromString<AnoncredsResults.IssuerCreateSchemaResult>(result)
            val anonCredSchema = AnonCredSchema(result?.second ?:"{}")
            return Pair(result?.first, anonCredSchema)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Pair(null, null)
    }

    override fun issuerCreateAndStoreCredentialDef(
        issuerDid: String?,
        schema: Any?,
        tag: String?,
        signatureType: String?,
        config: Any?
    ): Pair<String?, String?> {

        try {
            val future = CompletableFutureKotlin<Pair<String?,String?>>()
            IndyAnoncreds.issuerCreateAndStoreCredentialDefForSchema(schema.toString(), issuerDid, tag,
                signatureType,config.toString(), wallet.walletHandle)
            { error: NSError?, data: String?, data1: String? ->
                ErrorHandler(error).handleError()
                future.complete(Pair(data,data1))
            }
           return future.get() ?: Pair(null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Pair(null, null)

    }


    override fun issuerRotateCredentialDefStart(credDefId: String?, config: String?): String? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.issuerRotateCredentialDefStartForId(credDefId, config,wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun issuerRotateCredentialDefApply(credDefId: String?) {

        try {
            val future = CompletableFutureKotlin<Boolean>()
            IndyAnoncreds.issuerRotateCredentialDefApplyForId(credDefId,wallet.walletHandle)
            { error: NSError?->
                ErrorHandler(error).handleError()
                future.complete(true)
            }
             future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun issuerCreateAndStoreRevocReg(
        issuerDid: String?,
        revocDefType: String?,
        tag: String?,
        credDefId: String?,
        config: String?,
        tailsWriterHandle: Int
    ): Triple<String?, String?, String?> {
        return Triple(null, null, null)
    }

    override fun issuerCreateCredentialOffer(credDefId: String?): JSONObject? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.issuerCreateCredentialOfferForCredDefId(credDefId, wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            val string =  future.get()
            return JSONObject(string)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

    }


    override fun issuerCreateCredential(
        credOffer: JSONObject?,
        credReq: JSONObject?,
        credValues: JSONObject?,
        revRegId: String?,
        blobStorageReaderHandle: Int?
    ): Triple<JSONObject, String?, JSONObject?> {

        try {
            val future = CompletableFutureKotlin<Triple<JSONObject,String?,JSONObject?>>()
            IndyAnoncreds.issuerCreateCredentialForCredentialRequest(credReq.toString(),credOffer.toString(),
                credValues.toString(),revRegId,NSNumber(blobStorageReaderHandle?:0) ,wallet.walletHandle)
            { error: NSError?, data: String?,data1: String?,data2: String?->
                ErrorHandler(error).handleError()
              val triple =   Triple(JSONObject(data),data1,JSONObject(data2))
                future.complete(triple)
            }
            return  future.get() ?: Triple(JSONObject(), null, null)

        } catch (e: Exception) {
            e.printStackTrace()
        }


        return Triple(JSONObject(), null, null)
    }

    override fun issuerRevokeCredential(
        blobStorageReaderHandle: Int?,
        revRegId: String?,
        credRevocId: String?
    ): String? {
        return null
    }

    override fun issuerMergeRevocationRegistryDeltas(revRegDelta: String?, otherRevRegDelta: String?): String? {
        return null
    }

    override fun proverCreateMasterSecret(masterSecretName: String?): String? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.proverCreateMasterSecret(masterSecretName, wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }


    override fun proverCreateCredentialReq(
        proverDid: String?,
        credOffer: JSONObject?,
        credDef: JSONObject?,
        masterSecretId: String?
    ): Pair<JSONObject?, JSONObject?>? {

        try {
            val future = CompletableFutureKotlin<Pair<JSONObject?, JSONObject?>>()
            IndyAnoncreds.proverCreateCredentialReqForCredentialOffer(credOffer.toString(),credDef.toString(),
                proverDid,masterSecretId,wallet.walletHandle)
            { error: NSError?, data: String?,data1: String?->
                ErrorHandler(error).handleError()
                val triple =   Pair(JSONObject(data),JSONObject(data1))
                future.complete(triple)
            }
            return  future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun proverSetCredentialAttrTagPolicy(credDefId: String?, taAttrs: String?, retroactive: Boolean) {}
    override fun proverGetCredentialAttrTagPolicy(credDefId: String?): String? {
        return null
    }


    override fun proverStoreCredential(
        credId: String?,
        credReqMetadata: JSONObject?,
        cred: JSONObject?,
        credDef: JSONObject?,
        revReqDef: String?
    ): String? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.proverStoreCredential(cred.toString(),credId, credReqMetadata.toString(),
                credDef.toString(),revReqDef,wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun proverGetCredential(credDefId: String?): String? {
        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.proverGetCredentialWithId(credDefId,wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun proverDeleteCredential(credId: String?) {

        TODO("NOT Implemented in IOS INdyWallet")


        /*
        try {
            Anoncreds.proverDeleteCredential(wallet, credId).get(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

         */
    }

    override fun proverGetCredentials(filters: String?): List<String>? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.proverGetCredentialsForFilter(filters,wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            val creds =  future.get()

            val jArr: JSONArray = JSONArray(creds)
            val res: MutableList<String> = ArrayList<String>()
            for (o in jArr) {
                res.add(o.toString())
            }
            return res

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun proverSearchCredential(query: String?): List<String>? {
        return null
    }

    override fun proverGetCredentialsForProofReq(proofRequest: String?): String? {
        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.proverGetCredentialsForProofReq(proofRequest,wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }


    override fun proverSearchCredentialsForProofReq(
        proofRequest: JSONObject?,
        extraQuery: String?,
        limitReferents: Int
    ): JSONObject? {

            val future = CompletableFutureKotlin<IndyHandle?>()
            IndyAnoncreds.proverSearchCredentialsForProofRequest( proofRequest.toString(),extraQuery,wallet.walletHandle)
            { error: NSError?, data: IndyHandle?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
           val serachHandle =  future.get() ?: 0

            return try {
                var requestedAttributes: JSONObject? = proofRequest?.optJSONObject("requested_attributes")
                requestedAttributes = if (requestedAttributes != null) requestedAttributes else JSONObject()
                var requestedPredicates: JSONObject? = proofRequest?.optJSONObject("requested_predicates")
                requestedPredicates = if (requestedPredicates != null) requestedPredicates else JSONObject()
                val result: JSONObject =
                    JSONObject().put("self_attested_attributes", JSONObject())
                        .put("requested_attributes", JSONObject())
                        .put("requested_predicates", JSONObject())
                for (attrReferent in requestedAttributes.keySet()) {
                    val future2 = CompletableFutureKotlin<String?>()
                    IndyAnoncreds.proverFetchCredentialsForProofReqItemReferent(attrReferent,serachHandle,
                        NSNumber(limitReferents)
                    ){ error: NSError?, data: String?->
                        ErrorHandler(error).handleError()
                        future2.complete(data)
                    }

                    val credForAttrStr = future2.get()

                    val credForAttr: JSONArray = JSONArray(credForAttrStr)
                    var collection: JSONArray? =
                        result.optJSONObject("requested_attributes")?.optJSONArray(attrReferent)
                    collection = if (collection != null) collection else JSONArray()
                    for (o in credForAttr) collection.put(o)
                    result.optJSONObject("requested_attributes")?.put(attrReferent, collection)
                }
                for (predicateReferent in requestedPredicates.keySet()) {

                    val future3 = CompletableFutureKotlin<String?>()
                    IndyAnoncreds.proverFetchCredentialsForProofReqItemReferent(predicateReferent,serachHandle,
                        NSNumber(limitReferents)
                    ){ error: NSError?, data: String?->
                        ErrorHandler(error).handleError()
                        future3.complete(data)
                    }
                    val credForPredStr = future3.get()
                    val credForPred: JSONArray = JSONArray(credForPredStr)
                    var collection: JSONArray? =
                        result?.optJSONObject("requested_predicates")?.optJSONArray(predicateReferent)
                    collection = if (collection != null) collection else JSONArray()
                    for (o in credForPred) collection.put(o)
                    result.optJSONObject("requested_predicates")?.put(predicateReferent, collection)
                }
                result
            } finally {
                val future4 = CompletableFutureKotlin<Boolean>()
                IndyAnoncreds.proverCloseCredentialsSearchWithHandle(serachHandle ){
                    ErrorHandler(it).handleError()
                    future4.complete(true)
                }
                future4.get()
            }
        return null
    }

    override fun proverCreateProof(
        proofReq: JSONObject?,
        requestedCredentials: JSONObject?,
        masterSecretName: String?,
        schemas: JSONObject?,
        credentialDefs: JSONObject?,
        revStates: JSONObject?
    ): JSONObject? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.proverCreateProofForRequest(proofReq.toString(),requestedCredentials.toString(),
                masterSecretName,schemas.toString(),credentialDefs.toString(),revStates.toString(),wallet.walletHandle)
            { error: NSError?, data: String?->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            val res =  future.get()
            return JSONObject(res)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun verifierVerifyProof(
        proofRequest: JSONObject?,
        proof: JSONObject?,
        schemas: JSONObject?,
        credentialDefs: JSONObject?,
        revRegDefs: JSONObject?,
        revRegs: JSONObject?
    ): Boolean {
        return false
    }

    override fun createRevocation(
        blobStorageReaderHandle: Int,
        revRegDef: String?,
        revRegDelta: String?,
        timestamp: Int,
        credRevId: String?
    ): String? {
        return null
    }

    override fun updateRevocationState(
        blobStorageReaderHandle: Int,
        revState: String?,
        revRegDef: String?,
        revRegDelta: String?,
        timestamp: Int,
        credRevId: String?
    ): String? {
        return null
    }

    override fun generateNonce(): String? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.generateNonce { error, data ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun toUnqualified(entity: String?): String? {

        try {
            val future = CompletableFutureKotlin<String?>()
            IndyAnoncreds.toUnqualified(entity) { error, data ->
                ErrorHandler(error).handleError()
                future.complete(data)
            }
            return future.get()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


}