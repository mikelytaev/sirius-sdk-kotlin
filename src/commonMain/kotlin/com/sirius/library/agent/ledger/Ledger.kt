package com.sirius.library.agent.ledger

import com.sirius.library.agent.wallet.abstract_wallet.AbstractAnonCreds
import com.sirius.library.agent.wallet.abstract_wallet.AbstractCache
import com.sirius.library.agent.wallet.abstract_wallet.AbstractLedger
import com.sirius.library.agent.wallet.abstract_wallet.model.AnonCredSchema
import com.sirius.library.agent.wallet.abstract_wallet.model.CacheOptions
import com.sirius.library.storage.abstract_storage.AbstractImmutableCollection
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger

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
        val schemaString: String = cache.getSchema(name, submitterDid, id, CacheOptions())  ?:""
        return Schema(schemaString)
    }

    fun loadCredDef(id: String?, submitterDid: String?): CredentialDefinition? {
        val credDef: String = cache.getCredDef(name, submitterDid, id, CacheOptions()) ?:""
        val credentialDefinition = CredentialDefinition().deserialize(credDef)
        val tag: String? = credentialDefinition.tag
        return credentialDefinition
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
        val pair = api.registerSchema(name, submitterDid, schema)
        val txnResponse: JSONObject = JSONObject(pair?.second)
        if (pair?.first == true && "REPLY" == txnResponse.getString("op")) {
            val body: String = schema?.serialize() ?:""
            val seqNo: Int =
                txnResponse.getJSONObject("result")?.getJSONObject("txnMetadata")?.getInt("seqNo") ?: 0
            val schemaInLedger = Schema(body)
            schemaInLedger.seqNo = seqNo
            ensureExistInStorage(schemaInLedger, submitterDid)
            return Pair(true, schemaInLedger)
        } else {
            val reason: String? = txnResponse.getString("reason")
            if (reason != null) {
                    val reasonStr: String = reason
                Logger.getLogger("reasonStr=$reasonStr")
            }
        }
        return Pair(false, null)
    }

    fun ensureExistInStorage(entity: Schema, submitter_did: String?) {
        //   await self._storage.select_db(self.__db)
        storage.selectDb(db)
        val tagObject = JSONObject().put("id", entity.id).put("category", "schema")
        val (_, second) = storage.fetch(tagObject.toString())
        if (second === 0) {
            val tagUpdate = JSONObject()
            tagUpdate.put("id", entity.id)
            tagUpdate.put("name", entity.name)
            tagUpdate.put("version", entity.version)
            tagUpdate.put("submitter_did", submitter_did)
            tagObject.updateWith(tagUpdate)
            storage.add(entity.serializeToJSONObject().toString(), tagObject.toString())
        }
    }

    fun ensureExistInStorage(entity: CredentialDefinition, searchTags: JSONObject?) {
       storage.selectDb(db)
        val tagObject = JSONObject()
       .put("id", entity.id)
            .put("seq_no", entity.seqNo.toString())
        .put("category", "cred_def")
        val (_, second) = storage.fetch(tagObject.toString())
        if (second === 0) {
            val tagUpdate = JSONObject()
            .put("id", entity.id)
            .put("tag", entity.tag)
            .put("schema_id", entity.schema?.id)
            .put("submitter_did", entity.submitterDid)

            tagObject.updateWith(tagUpdate)
            if (searchTags != null) {
                tagObject.updateWith(searchTags)
            }
            storage.add(entity.serialize(), tagObject.toString())
        }
    }

    fun ensureExistInStorage(entity: CredentialDefinition) {
        ensureExistInStorage(entity, null)
    }

    fun ensureSchemaExists(schema: AnonCredSchema, submitterDid: String?): Schema? {
       try {
            val schemaString: String? = cache.getSchema(name, submitterDid, schema.id, CacheOptions())
            val ledgerSchema = Schema(schemaString ?:"")
            ensureExistInStorage(ledgerSchema, submitterDid)
            return ledgerSchema
        } catch (e: Exception) {
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
        filters.id = id
        filters.name = name
        filters.version = version
        filters.submitterDid = submitterDid
        val results: Pair<List<Any>, Int> = storage.fetch(filters.tags.serialize())
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
            credDef.schema?.serializeToJSONObject(),
            credDef.tag,
            null,
            credDef.config?.serializeToJSONObject()
        )
        val buildRequest: String? = api.buildCredDefRequest(submitterDid, JSONObject(body))
        val signedRequest: String? = api.signRequest(submitterDid, JSONObject(buildRequest))
        val resp: String? = api.submitRequest(name, JSONObject(signedRequest))
        val respJson: JSONObject = JSONObject(resp)
        if (!(respJson.has("op") && respJson.getString("op") == "REPLY")) {
            return Pair(false, null)
        }
        val legderCredDef = CredentialDefinition(
            credDef.tag,
            credDef.schema,
            credDef.config,
            JSONObject(body),
            respJson.getJSONObject("result")?.getJSONObject("txnMetadata")?.getInt("seqNo")
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
