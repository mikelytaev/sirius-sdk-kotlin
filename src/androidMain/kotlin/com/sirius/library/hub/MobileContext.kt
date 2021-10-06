/*
package com.sirius.library.hub

import kotlin.jvm.JvmOverloads

class MobileContext(config: MobileHub.Config?) : Context(MobileHub(config)) {
    var mediatorPw: Pairwise? = null
    var timeToLiveSec = 60
    override var currentHub: MobileHub
        get() = super.getCurrentHub() as MobileHub
        set(currentHub) {
            super.currentHub = currentHub
        }

    class MobileContextBuilder {
        var config: MobileHub.Config = Config()
        fun setWalletConfig(walletConfig: JSONObject): MobileContextBuilder {
            config.walletConfig = walletConfig
            return this
        }

        fun setIndyEndpoint(indyEndpoint: String): MobileContextBuilder {
            config.indyEndpoint = indyEndpoint
            return this
        }

        fun setServerUri(serverUri: String): MobileContextBuilder {
            config.serverUri = serverUri
            return this
        }

        fun setWalletCredentials(walletCredentials: JSONObject): MobileContextBuilder {
            config.walletCredentials = walletCredentials
            return this
        }

        fun setSender(sender: BaseSender): MobileContextBuilder {
            config.sender = sender
            return this
        }

        fun setMediatorInvitation(invitation: Invitation): MobileContextBuilder {
            config.mediatorInvitation = invitation
            return this
        }

        fun build(): MobileContext {
            return MobileContext(config)
        }
    }

    @JvmOverloads
    fun connectToMediator(label: String?, connections: List<MobileContextConnection>? = null) {
        val invitation: Invitation = (currentHub.getConfig() as MobileHub.Config).mediatorInvitation
        val mediatorDid = getMediatorDid(invitation.recipientKeys().get(0))
        if (mediatorDid == null) {
            val (first, second) = getDid().createAndStoreMyDid()
            val me = Me(first, second)
            val endpoint = Endpoint("ws://")
            val invitee = Invitee(this, me, endpoint)
            var connectionArray: JSONArray? = null
            if (connections != null) {
                for (connection in connections) {
                    connectionArray = JSONArray()
                    val fcmServiceObject: JSONObject = JSONObject()
                        .put("id", "did:peer:" + me.getDid().toString() + ";indy").put("type", connection.getType())
                        .put("priority", connection.getPriority()).put(
                            "recipientKeys",
                            JSONArray(connection.getRecipientKeys())
                        ).put("serviceEndpoint", connection.getServiceEndpoint())
                    connectionArray.put(fcmServiceObject)
                }
            }
            val pw: Pairwise = invitee.createConnection(invitation, label, null, connectionArray)
            if (pw != null) {
                getPairwiseList().ensureExists(pw)
                mediatorPw = pw
            }
            askForMediation()
        } else {
            mediatorPw = getPairwiseList().loadForDid(mediatorDid)
            getEndpoints().add(getMyMediatorEndpoint(invitation.recipientKeys().get(0)))
        }
        if (mediatorPw != null) {
            val services: JSONArray = mediatorPw.getTheir().getDidDoc().optJSONArray("service")
            var mediatorService: JSONObject = JSONObject()
            for (o in services) {
                val service: JSONObject = o as JSONObject
                if (service.optString("type") == "MediatorService") {
                    mediatorService = service
                    break
                }
            }
            val myWsEndpoint: String = mediatorService.optString("serviceEndpoint")
            (currentHub.getAgentConnectionLazy() as MobileAgent).connect(myWsEndpoint)
        }
    }

    private fun getMediatorDid(mediatorRecipientKey: String): String? {
        val recordStr: String = nonSecrets.getWalletRecord(
            MEDIATOR_ENDPOINTS,
            mediatorRecipientKey,
            RetrieveRecordOptions(false, true, false)
        )
        if (recordStr != null && !recordStr.isEmpty()) {
            val r: JSONObject = JSONObject(recordStr)
            val v: JSONObject = JSONObject(r.opt("value").toString())
            if (v.has("their_did")) {
                return v.optString("their_did")
            }
        }
        return null
    }

    private fun getMyMediatorEndpoint(mediatorRecipientKey: String): Endpoint? {
        val recordStr: String = nonSecrets.getWalletRecord(
            MEDIATOR_ENDPOINTS,
            mediatorRecipientKey,
            RetrieveRecordOptions(false, true, false)
        )
        if (recordStr != null && !recordStr.isEmpty()) {
            val r: JSONObject = JSONObject(recordStr)
            val v: JSONObject = JSONObject(r.opt("value").toString())
            if (v.has("endpoint_address")) {
                return Endpoint(v.optString("endpoint_address"))
            }
        }
        return null
    }

    private fun saveMediatorInfo(mediatorRecipientKey: String, theirDid: String, endpoint: Endpoint) {
        nonSecrets.addWalletRecord(
            MEDIATOR_ENDPOINTS,
            mediatorRecipientKey,
            JSONObject().put("their_did", theirDid).put("endpoint_address", endpoint.getAddress()).toString()
        )
    }

    fun askForMediation(): Boolean {
        try {
            CoProtocolP2PAnon(
                this, mediatorPw.getMe().getVerkey(), mediatorPw.getTheir(), ArrayList<E>(), timeToLiveSec
            ).use { cp ->
                val request: MediateRequest = MediateRequest.builder().build()
                val (first, second) = cp.sendAndWait(request)
                if (first) {
                    if (second is MediateGrant) {
                        val grant: MediateGrant = second as MediateGrant
                        val endpoint = Endpoint(grant.getEndpointAddress(), grant.getRoutingKeys())
                        getEndpoints().add(endpoint)
                        val invitation: Invitation = (currentHub.getConfig() as MobileHub.Config).mediatorInvitation
                        saveMediatorInfo(invitation.recipientKeys().get(0), mediatorPw.getTheir().getDid(), endpoint)
                        return true
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun addMediatorKeys(keys: List<String?>?): Boolean {
        try {
            CoProtocolP2PAnon(
                this, mediatorPw.getMe().getVerkey(), mediatorPw.getTheir(), ArrayList<E>(), timeToLiveSec
            ).use { cp ->
                val keylistUpdate: KeylistUpdate = KeylistUpdate.builder().addKeys(keys).build()
                val (first, second) = cp.sendAndWait(keylistUpdate)
                if (first) {
                    if (second is KeylistUpdateResponse) {
                        return true
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun addMediatorKey(key: String?): Boolean {
        return addMediatorKeys(java.util.Arrays.asList<String>(key))
    }

    companion object {
        const val MEDIATOR_ENDPOINTS = "MEDIATOR_ENDPOINTS"
        const val PROTOCOL_VERSION = 2
        fun addPool(name: String?, txnPath: String?) {
            try {
                Pool.setProtocolVersion(PROTOCOL_VERSION).get()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            PoolMobile.registerPool(name, txnPath)
        }

        fun builder(): MobileContextBuilder {
            return MobileContextBuilder()
        }
    }
}
*/
