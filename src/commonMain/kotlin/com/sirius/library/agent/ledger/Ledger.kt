package com.sirius.library.agent.ledger

import com.sirius.library.agent.wallet.abstract_wallet.AbstractAnonCreds
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCache
import com.sirius.library.agent.wallet.abstract_wallet.AbstractLedger
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.agent.wallet.abstract_wallet.model.CacheOptions
import com.sirius.library.storage.abstract_storage.AbstractImmutableCollection
import com.sirius.library.utils.JSONObject

class Ledger(
    var name: String, api: AbstractLedger, issuer: AbstractAnonCreds,
    cache: AbstractCache, storage: AbstractImmutableCollection
) {
    var api: AbstractLedger
    var issuer: AbstractAnonCreds
    var cache: AbstractCache
    var storage: AbstractImmutableCollection
    var db: String
    fun loadSchema(id: String?, submitterDid: String?): Schema {
        val schemaString: String = cache.getSchema(name, submitterDid, id, CacheOptions())
        return Schema(schemaString)
    }

    fun loadCredDef(id: String?, submitterDid: String?): CredentialDefinition? {
        val credDef: String = cache.getCredDef(name, submitterDid, id, CacheOptions())
        val credentialDefinition = CredentialDefinition().deserialize(credDef)
        val tag: String = credentialDefinition.tag
        return null
    }

    /*   async def load_cred_def(self, id_: str, submitter_did: str) -> CredentialDefinition:
       cred_def_body = await self._cache.get_cred_def(
       pool_name=self.name,
       submitter_did=submitter_did,
       id_=id_,
       options=CacheOptions()
           )
       tag = cred_def_body.get('tag')
       schema_seq_no = int(cred_def_body['schemaId'])
       cred_def_seq_no = int(cred_def_body['id'].split(':')[3]) + 1
       txn_request = await self._api.build_get_txn_request(
       submitter_did=submitter_did,
       ledger_type=None,
       seq_no=schema_seq_no
           )
       resp = await self._api.sign_and_submit_request(
       pool_name=self.name,
       submitter_did=submitter_did,
       request=txn_request
           )
                   if resp['op'] == 'REPLY':
       txn_data = resp['result']['data']
       schema_body = {
           'name': txn_data['txn']['data']['data']['name'],
                   'version': txn_data['txn']['data']['data']['version'],
                   'attrNames': txn_data['txn']['data']['data']['attr_names'],
                   'id': txn_data['txnMetadata']['txnId'],
                   'seqNo': txn_data['txnMetadata']['seqNo']
       }
       schema_body['ver'] = schema_body['id'].split(':')[-1]
       schema = Schema(**schema_body)
       cred_def = CredentialDefinition(
               tag=tag, schema=schema, body=cred_def_body, seq_no=cred_def_seq_no
       )
               return cred_def
           else:
       raise SiriusInvalidPayloadStructure()

   */
    fun registerSchema(schema: AnonCredSchema?, submitterDid: String?): Pair<Boolean, Schema?> {
        val (first, second) = api.registerSchema(name, submitterDid, schema)
        val txnResponse: JsonObject = GsonUtils.toJsonObject(second)
        if (first && "REPLY" == txnResponse.get("op").getAsString()) {
            val body: String = schema.serialize()
            val seqNo: Int =
                txnResponse.getAsJsonObject("result").getAsJsonObject("txnMetadata").get("seqNo").getAsInt()
            val schemaInLedger = Schema(body)
            schemaInLedger.setSeqNo(seqNo)
            ensureExistInStorage(schemaInLedger, submitterDid)
            return Pair(true, schemaInLedger)
        } else {
            val reason: JsonElement = txnResponse.get("reason")
            if (reason != null) {
                if (!reason.isJsonNull()) {
                    val reasonStr: String = reason.getAsString()
                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.WARNING, reasonStr)
                }
            }
        }
        return Pair(false, null)
    }

    fun ensureExistInStorage(entity: Schema, submitter_did: String?) {
        //   await self._storage.select_db(self.__db)
        storage.selectDb(db)
        val tagObject = JsonObject()
        tagObject.addProperty("id", entity.getId())
        tagObject.addProperty("category", "schema")
        val (_, second) = storage.fetch(tagObject.toString())
        if (second === 0) {
            val tagUpdate = JsonObject()
            tagUpdate.addProperty("id", entity.getId())
            tagUpdate.addProperty("name", entity.getName())
            tagUpdate.addProperty("version", entity.getVersion())
            tagUpdate.addProperty("submitter_did", submitter_did)
            GsonUtils.updateJsonObject(tagObject, tagUpdate)
            storage.add(entity.serializeToJsonObject().toString(), tagObject.toString())
        }
    }

    fun ensureExistInStorage(entity: CredentialDefinition, searchTags: JsonObject?) {
        storage.selectDb(db)
        val tagObject = JsonObject()
        tagObject.addProperty("id", entity.getId())
        tagObject.addProperty("seq_no", java.lang.String.valueOf(entity.getSeqNo()))
        tagObject.addProperty("category", "cred_def")
        val (_, second) = storage.fetch(tagObject.toString())
        if (second === 0) {
            val tagUpdate = JsonObject()
            tagUpdate.addProperty("id", entity.getId())
            tagUpdate.addProperty("tag", entity.getTag())
            tagUpdate.addProperty("schema_id", entity.getSchema().getId())
            tagUpdate.addProperty("submitter_did", entity.getSubmitterDid())
            GsonUtils.updateJsonObject(tagObject, tagUpdate)
            if (searchTags != null) {
                GsonUtils.updateJsonObject(tagObject, searchTags)
            }
            storage.add(entity.serialize(), tagObject.toString())
        }
    }

    fun ensureExistInStorage(entity: CredentialDefinition?) {
        ensureExistInStorage(entity, null)
    }

    fun ensureSchemaExists(schema: AnonCredSchema, submitterDid: String?): Schema? {
        try {
            val schemaString: String = cache.getSchema(name, submitterDid, schema.getId(), CacheOptions())
            val ledgerSchema = Schema(schemaString)
            ensureExistInStorage(ledgerSchema, submitterDid)
            return ledgerSchema
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            val (first, second) = registerSchema(schema, submitterDid)
            if (first) {
                return second
            }
        }
        return null
    }

    fun fetchSchemas(id: String?, name: String?, version: String?, submitterDid: String?): List<Schema> {
        val filters = SchemaFilters()
        filters.setId(id)
        filters.setName(name)
        filters.setVersion(version)
        filters.setSubmitterDid(submitterDid)
        val results: Pair<List<Any>, Int> = storage.fetch(filters.getTags().serialize())
        val schemaList: MutableList<Schema> = ArrayList<Schema>()
        if (results != null) {
            val objects = results.first
            for (i in objects.indices) {
                val ob = objects[i]
                if (ob is String) {
                    val schema = Schema(ob)
                    schemaList.add(schema)
                }
            }
        }
        return schemaList
    }

    fun fetchSchemas(id: String?, name: String?, version: String?): List<Schema> {
        return fetchSchemas(id, name, version, null)
    }

    fun fetchSchemas(id: String?, name: String?): List<Schema> {
        return fetchSchemas(id, name, null, null)
    }

    fun fetchSchemas(id: String?): List<Schema> {
        return fetchSchemas(id, null, null, null)
    }

    fun fetchSchemas(): List<Schema> {
        return fetchSchemas(null, null, null, null)
    }

    fun registerCredDef(
        credDef: CredentialDefinition,
        submitterDid: String?,
        tags: JSONObject?
    ): Pair<Boolean, CredentialDefinition?> {
        val (_, body) = issuer.issuerCreateAndStoreCredentialDef(
            submitterDid,
            credDef.getSchema().serializeToJSONObject(),
            credDef.getTag(),
            null,
            credDef.getConfig().serializeToJSONObject()
        )
        val buildRequest: String = api.buildCredDefRequest(submitterDid, JSONObject(body))
        val signedRequest: String = api.signRequest(submitterDid, JSONObject(buildRequest))
        val resp: String = api.submitRequest(name, JSONObject(signedRequest))
        val respJson: JSONObject = JSONObject(resp)
        if (!(respJson.has("op") && respJson.getString("op") == "REPLY")) {
            return Pair(false, null)
        }
        val legderCredDef = CredentialDefinition(
            credDef.getTag(),
            credDef.getSchema(),
            credDef.getConfig(),
            Gson().fromJson(body, JsonObject::class.java),
            respJson.getJSONObject("result").getJSONObject("txnMetadata").getInt("seqNo")
        )
        ensureExistInStorage(legderCredDef, tags)
        return Pair(true, legderCredDef)
    }

    fun registerCredDef(credDef: CredentialDefinition, submitterDid: String?): Pair<Boolean, CredentialDefinition?> {
        return registerCredDef(credDef, submitterDid, JSONObject())
    }

    init {
        this.api = api
        this.issuer = issuer
        this.cache = cache
        this.storage = storage
        db = "ledger_storage_$name"
    }
}
