package com.sirius.library

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.Codec
import com.sirius.library.agent.aries_rfc.feature_0037_present_proof.messages.RequestPresentationMessage
import com.sirius.library.agent.aries_rfc.feature_0037_present_proof.state_machines.Prover
import com.sirius.library.agent.aries_rfc.feature_0037_present_proof.state_machines.Verifier
import com.sirius.library.agent.ledger.CredentialDefinition
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.errors.indy_exceptions.DuplicateMasterSecretNameException
import com.sirius.library.helpers.ConfTest
import com.sirius.library.helpers.ServerTestSuite
import com.sirius.library.hub.CloudContext
import com.sirius.library.models.AgentParams
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.Logger
import com.sirius.library.utils.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestAriesFeature0037 {

    lateinit var confTest: ConfTest
    var log: Logger = Logger.getLogger(TestAriesFeature0037::class.simpleName)

    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
        val future = CompletableFutureKotlin<Boolean>()
        LibsodiumInitializer.initializeWithCallback {
            future.complete(true)
        }
        future.get(60)
    }

    /*@Test
    fun testSane() {
       val issuer: CloudAgent = confTest.getAgent("agent1")
        val prover: CloudAgent = confTest.getAgent("agent2")
        val verifier: CloudAgent = confTest.getAgent("agent3")
        issuer.open()
        prover.open()
        verifier.open()
        log.info("Establish pairwises")
        val i2p: Pairwise = confTest.getPairwise(issuer, prover)
        val p2i: Pairwise = confTest.getPairwise(prover, issuer)
        val v2p: Pairwise = confTest.getPairwise(verifier, prover)
        val p2v: Pairwise = confTest.getPairwise(prover, verifier)
        log.info("Register schema")
        val issuerDid: String? = i2p.me.did
        val issuerVerkey: String? = i2p.me.verkey
        val schemaName = "schema_" + UUID.randomUUID.toString()
        val (schemaId, anoncredSchema) = issuer.getWalleti()?.anoncreds
            ?.issuerCreateSchema(issuerDid, schemaName, "1.0", "attr1", "attr2", "attr3") ?: Pair(null,null)
        val ledger: Ledger? = issuer.getLedgersi().get("default")
        assertNotNull(anoncredSchema)
        val (first, schema) = ledger?.registerSchema(anoncredSchema, issuerDid)?: Pair(false,null)
        assertTrue(first)
        log.info("Register credential def")
        val (first1, credDef) = ledger?.registerCredDef(CredentialDefinition("TAG", schema), issuerDid)?: Pair(false,null)
        assertTrue(first1)
        log.info("Prepare Prover")
        try {
            prover.getWalleti()?.anoncreds?.proverCreateMasterSecret(ConfTest.proverMasterSecretName)
        } catch (ignored: DuplicateMasterSecretNameException) {
        }
        val proverSecretId: String = ConfTest.proverMasterSecretName
        val credValues: JSONObject = JSONObject()
            .put("attr1", "Value-1").put("attr2", 456).put("attr3", 4.67)
        val credId = "cred-id-" + UUID.randomUUID.toString()

        // Issue credential
        val offer: JSONObject? = issuer.getWalleti()?.anoncreds?.issuerCreateCredentialOffer(credDef?.id)
        val (credRequest, credMetadata) = prover.getWalleti()?.anoncreds?.proverCreateCredentialReq(
            p2i.me.did,
            offer,
            JSONObject(credDef?.getBodyi().toString()),
            proverSecretId
        ) ?: Pair(null,null)
        val encodedCredValues: JSONObject = JSONObject()
        for (key in credValues.keySet()) {
            val encCredVal: JSONObject = JSONObject()
            encCredVal.put("raw", credValues.get(key).toString())
            encCredVal.put("encoded", Codec.encode(credValues.get(key)))
            encodedCredValues.put(key, encCredVal)
        }
        val (cred, credRevocId, revocRegDelta) = issuer.getWalleti()?.anoncreds
            ?.issuerCreateCredential(offer, credRequest, encodedCredValues)?:Triple(JSONObject(), null,null)
        prover.getWalleti()?.anoncreds
            ?.proverStoreCredential(credId, credMetadata, cred, JSONObject(credDef?.getBodyi().toString()))
        issuer.close()
        prover.close()
        verifier.close()
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val proverParams: AgentParams = testSuite.getAgentParams("agent2")
        val verifierParams: AgentParams = testSuite.getAgentParams("agent3")
        val attrReferentId = "attr1_referent"
        val predReferentId = "predicate1_referent"
        var proofRequest: JSONObject? = null
        CloudContext.builder().setServerUri(verifierParams.serverAddress)
            .setCredentials(verifierParams.credentials.encodeToByteArray())
            .setP2p(verifierParams.connection).build().also { context ->
                proofRequest = JSONObject().put("nonce", context.getAnonCredsi().generateNonce())
                    .put("name", "Test ProofRequest").put("version", "0.1").put(
                        "requested_attributes",
                        JSONObject().put(
                            attrReferentId,
                            JSONObject().put("name", "attr1")
                                .put("restrictions", JSONObject().put("issuer_did", issuerDid))
                        )
                    ).put(
                        "requested_predicates",
                        JSONObject().put(
                            predReferentId,
                            JSONObject().put("name", "attr2").put("p_type", ">=").put("p_value", 100)
                                .put("restrictions",JSONObject().put("issuer_did", issuerDid))
                        )
                    )
            }
        //run_verifier
        val finalProofRequest: JSONObject? = proofRequest

        val runVerifier =  GlobalScope.launch (Dispatchers.Default){
            CloudContext.builder().setServerUri(verifierParams.serverAddress)
                .setCredentials(
                    verifierParams.credentials.encodeToByteArray()
                )
                .setP2p(verifierParams.connection).setTimeoutSec(60).build().also { context ->
                    val verLedger: Ledger? = context.ledgers?.get("default")
                    val machine = Verifier(context, v2p, verLedger)
                     machine.verify(
                        Verifier.VerifyParams().setProofRequest(finalProofRequest).setComment("I am Verifier")
                            .setProtocolVersion("1.0")
                    )
                }

        }

        val runProver =  GlobalScope.launch (Dispatchers.Default){
            CloudContext.builder().setServerUri(proverParams.serverAddress)
                .setCredentials(proverParams.credentials.encodeToByteArray())
                .setP2p(proverParams.connection).setTimeoutSec(60).build().also { context ->
                    var event: Event? = null
                    event = context.subscribe()?.one?.get(30)

                    assertTrue(event?.message() is RequestPresentationMessage)
                    val requestPresentationMessage: RequestPresentationMessage =
                        event?.message() as RequestPresentationMessage
                    val ttl = 60
                    val proverLedger: Ledger? = context.ledgers?.get("default")
                    val machine = Prover(context, p2v, proverSecretId, proverLedger?.name)
                    machine.prove(requestPresentationMessage)
                }
        }

        runBlocking {
            runProver.join()
            runVerifier.join()
        }


       // assertTrue(runProver.get(60)
      //  assertTrue(runVerifier.get(60)
    }*/
}
