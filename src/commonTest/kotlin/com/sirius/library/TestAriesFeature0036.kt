package com.sirius.library

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.sirius.library.agent.CloudAgent
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.OfferCredentialMessage
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.messages.ProposedAttrib
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.state_machines.Holder
import com.sirius.library.agent.aries_rfc.feature_0036_issue_credential.state_machines.Issuer
import com.sirius.library.agent.ledger.CredentialDefinition
import com.sirius.library.agent.ledger.Ledger
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.errors.indy_exceptions.DuplicateMasterSecretNameException
import com.sirius.library.errors.indy_exceptions.WalletItemNotFoundException
import com.sirius.library.helpers.ConfTest
import com.sirius.library.helpers.ServerTestSuite
import com.sirius.library.hub.CloudContext
import com.sirius.library.messaging.Message
import com.sirius.library.models.AgentParams
import com.sirius.library.utils.CompletableFutureKotlin
import com.sirius.library.utils.JSONObject
import com.sirius.library.utils.UUID
import kotlinx.coroutines.*
import kotlin.test.*

class TestAriesFeature0036 {
    lateinit var confTest: ConfTest
    @BeforeTest
    fun configureTest() {
        confTest = ConfTest.newInstance()
        val future = CompletableFutureKotlin<Boolean>()
        LibsodiumInitializer.initializeWithCallback {
            future.complete(true)
        }
        future.get(60)
    }

    @Test
    fun testSane() {
        val issuer: CloudAgent = confTest.getAgent("agent1")
        val holder: CloudAgent = confTest.getAgent("agent2")
        issuer.open()
        holder.open()
        val i2h: Pairwise = confTest.getPairwise(issuer, holder)
        val h2i: Pairwise = confTest.getPairwise(holder, issuer)
        val issuerDid: String? = i2h.me.did
        val issuerVerkey: String? = i2h.me.verkey
        val schemaName = "schema_" + UUID.randomUUID.toString()
        val (schemaId, anoncredSchema) = issuer.getWalleti()?.anoncreds?.issuerCreateSchema(
            issuerDid,
            schemaName, "1.0", "attr1", "attr2", "attr3", "attr4"
        ) ?:Pair(null,null)
        val ledger: Ledger? = issuer.getLedgersi().get("default")
        val (first, schema) = ledger?.registerSchema(anoncredSchema, issuerDid) ?:Pair(false, null)
        assertTrue(first)
        val (first1, credDef) = ledger?.registerCredDef(CredentialDefinition("TAG", schema), issuerDid) ?:Pair(false, null)
        assertTrue(first1)
        try {
            holder.getWalleti()?.anoncreds?.proverCreateMasterSecret(ConfTest.proverMasterSecretName)
        } catch (ignored: DuplicateMasterSecretNameException) {
        }
        issuer.close()
        holder.close()
        val testSuite: ServerTestSuite = confTest.suiteSingleton
        val issuerParams: AgentParams = testSuite.getAgentParams("agent1")
        val holderParams: AgentParams = testSuite.getAgentParams("agent2")
        val holderSecretId: String = ConfTest.proverMasterSecretName
        val credId = "cred-id-" + UUID.randomUUID.toString()
        val values: JSONObject = JSONObject()
            .put("attr1", "Value-1").put("attr2", 567).put("attr3", 5.7).put("attr4", "base64")
        val preview: List<ProposedAttrib> = listOf(
            ProposedAttrib("attr1", "Value-1", "text/plain"),
            ProposedAttrib("attr4", "base64", "image/png")
        )

        val issuerFuture = GlobalScope.async (Dispatchers.Default){
            try {
                CloudContext.builder().setServerUri(issuerParams.serverAddress).setCredentials(
                    issuerParams.credentials.encodeToByteArray()
                ).setP2p(issuerParams.connection).build().also { context ->
                    val issuerMachine = Issuer(context, i2h, 60)
                    delay(10)

                   issuerMachine.issue(
                        Issuer.IssueParams().setValues(values).setSchema(schema).setCredDef(credDef)
                            .setComment("Hello Iam issuer")
                            .setLocale("en").setPreview(preview).setCredId(credId)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
        val holderFuture =  GlobalScope.launch (Dispatchers.Default){
           try {
               CloudContext.builder().setServerUri(holderParams.serverAddress).setCredentials(
                   holderParams.credentials.encodeToByteArray()
               ).setP2p(holderParams.connection).build().also { context ->
                   var event: Event? = null
                   event = context.subscribe()?.one?.get(30)
                  // } catch (e: Exception) {
                    //   e.printStackTrace()
                      // return@supplyAsync Pair(false, "")
                 //  }
                   val offer: Message? = event?.message()
                   assertTrue(offer is OfferCredentialMessage)
                   val holderMachine = Holder(context, h2i, holderSecretId, "en")
                   val okCredId: Pair<Boolean, String> =
                       holderMachine.accept(offer as OfferCredentialMessage, "Hello, Iam holder")
                   if (okCredId.first) {
                       val cred: String? = context.getAnonCredsi().proverGetCredential(okCredId.second)
                       println(cred)
                       val mimeTypes: JSONObject = Holder.getMimeTypes(context, okCredId.second)
                       assertEquals(2, mimeTypes.length())
                       assertEquals("text/plain", mimeTypes.optString("attr1"))
                       assertEquals("image/png", mimeTypes.optString("attr4"))
                   }
                  // return@supplyAsync okCredId
               }
           } catch (e: WalletItemNotFoundException) {
               e.printStackTrace()
               fail()
           }

       }

     runBlocking {
        // withTimeout(30){
             issuerFuture.join()
     //    }
     //  withTimeout(30){
           holderFuture.join()
    //   }

     }
      //  val issueRes: Boolean = issuerFuture.get(30)
  //      val holderRes: Boolean = holderFuture.get(30).first
      //  assertTrue(issueRes)
     //   assertTrue(holderRes)
    }
}
