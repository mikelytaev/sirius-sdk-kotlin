package com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages

import com.sirius.library.messaging.Message
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

class ProposeCredentialMessage(message: String) : BaseIssueCredentialMessage(message) {
    companion object {
        fun builder(): Builder<*> {
            return ProposeCredentialMessageBuilder()
        }

        init {
            Message.registerMessageClass(ProposeCredentialMessage::class, PROTOCOL, "propose-credential")
        }
    }

    val schemaIssuerDid: String?
        get() = getMessageObj()?.optString("schema_issuer_did")
    val schemaId: String?
        get() = getMessageObj()?.optString("schema_id")
    val schemaName: String?
        get() = getMessageObj()?.optString("schema_name")
    val schemaVersion: String?
        get() = getMessageObj()?.optString("schema_version")
    val credDefId: String?
        get() = getMessageObj()?.optString("cred_def_id")
    val issuerDid: String?
        get() = getMessageObj()?.optString("issuer_did")
    val credentialProposal: List<Any>
        get() {
            val res: MutableList<ProposedAttrib> = ArrayList<ProposedAttrib>()
            val credentialProposal: JSONObject? = getMessageObj()?.optJSONObject("credential_proposal")
            if (credentialProposal != null) {
                if (credentialProposal.optString("@type") == CREDENTIAL_PREVIEW_TYPE) {
                    val attribs: JSONArray? = credentialProposal?.optJSONArray("attributes")
                    if (attribs != null) {
                        for (o in attribs) {
                            res.add(ProposedAttrib(o as JSONObject))
                        }
                    }
                }
            }
            return res
        }

    abstract class Builder<B : Builder<B>?> :
        BaseIssueCredentialMessage.Builder<B>() {
        var credentialProposal: List<ProposedAttrib>? = null
        var schemaIssuerDid: String? = null
        var schemaId: String? = null
        var schemaName: String? = null
        var schemaVersion: String? = null
        var credDefId: String? = null
        var issuerDid: String? = null
        fun setCredentialProposal(credentialProposal: List<ProposedAttrib>?): B? {
            this.credentialProposal = credentialProposal
            return self()
        }

        fun setSchemaIssuerDid(schemaIssuerDid: String?): B? {
            this.schemaIssuerDid = schemaIssuerDid
            return self()
        }

        fun setSchemaId(schemaId: String?): B? {
            this.schemaId = schemaId
            return self()
        }

        fun setSchemaName(schemaName: String?): B? {
            this.schemaName = schemaName
            return self()
        }

        fun setSchemaVersion(schemaVersion: String?): B? {
            this.schemaVersion = schemaVersion
            return self()
        }

        fun setCredDefId(credDefId: String?): B? {
            this.credDefId = credDefId
            return self()
        }

        fun setIssuerDid(issuerDid: String?): B? {
            this.issuerDid = issuerDid
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            if (credentialProposal != null) {
                val credProposal: JSONObject? = JSONObject().put("@type", CREDENTIAL_PREVIEW_TYPE)
                val attributes = JSONArray()
                for (attrib in credentialProposal!!) attributes.put(attrib)
                credProposal?.put("attributes", attributes)
                jsonObject.put("credential_proposal", credProposal)
            }
            if (schemaIssuerDid != null) {
                jsonObject.put("schema_issuer_did", schemaIssuerDid)
            }
            if (schemaId != null) {
                jsonObject.put("schema_id", schemaId)
            }
            if (schemaName != null) {
                jsonObject.put("schema_name", schemaName)
            }
            if (schemaVersion != null) {
                jsonObject.put("schema_version", schemaVersion)
            }
            if (credDefId != null) {
                jsonObject.put("cred_def_id", credDefId)
            }
            if (issuerDid != null) {
                jsonObject.put("issuer_did", issuerDid)
            }
            return jsonObject
        }

        fun build(): ProposeCredentialMessage {
            return ProposeCredentialMessage(generateJSON().toString())
        }
    }

    private class ProposeCredentialMessageBuilder :
        Builder<ProposeCredentialMessageBuilder?>() {
        override fun self(): ProposeCredentialMessageBuilder {
            return this
        }
    }
}
