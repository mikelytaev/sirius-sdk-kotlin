package examples

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.LibsodiumUtil
import com.sirius.library.agent.aries_rfc.feature_0095_basic_message.Message
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter
import com.sirius.library.agent.connections.Endpoint
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.agent.wallet.abstract_wallet.model.RetrieveRecordOptions
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.CloudHub
import com.sirius.library.hub.Context
import com.sirius.library.messaging.MessageFabric
import com.sirius.library.utils.Date
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject
import kotlin.jvm.JvmStatic

object MainTEst {
    lateinit var context: CloudContext


    @JvmStatic
    fun main(args: Array<String>) {
        MessageFabric.registerAllMessagesClass()
        LibsodiumInitializer.initializeWithCallback {


            val config: CloudHub.Config = CloudHub.Config()
            config.serverUri = "https://demo.socialsirius.com"
            config.credentials =
                "ez8ucxfrTiV1hPX99MHt/JZL1h63sUO9saQCgn2BsaC2EndwDSYpOo6eFpn8xP8ZDoj5B5KN4aaLiyzTqkrbDxrbAe/+2uObPTl6xZdXMBs=".encodeToByteArray()
            config.p2p = P2PConnection(
                "B1n1Hwj1USs7z6FAttHCJcqhg7ARe7xtcyfHJCdXoMnC",
                "y7fwmKxfatm6SLN6sqy6LFFjKufgzSsmqA2D4WZz55Y8W7JFeA3LvmicC36E8rdHoAiFhZgSf4fuKmimk9QyBec",
                "5NUzoX1YNm5VXsgzudvVikN7VQpRf5rhaTnPxyu12eZC"
            )

            context = CloudContext(config)
            val endpoints: List<Endpoint> = context?.endpoints ?: listOf()
          /*  var myEndpoint: Endpoint? = null
            for (e in endpoints) {
                if (e.routingKeys.isEmpty()) {
                    myEndpoint = e
                    break
                }
            }
*/
            val connectionKey: String? = context?.crypto?.createKey()
           // val connectionKey: String? ="ConnectionKey"
   /*         println("connectionKey: $connectionKey")
            val invitation: Invitation =
                Invitation.builder().setLabel("0160 Sample J").setRecipientKeys(listOfNotNull(connectionKey))
                    .setEndpoint(myEndpoint?.address).build()

            // шаг 3 - согласно Aries-0160 генерируем URL
            val qrContent: String? = invitation.invitationUrl()

            // шаг 4 - создаем QR
            val qrUrl: String? = context?.generateQrCode(qrContent)
            println("Открой QR код и просканируй в Sirius App: $qrUrl")*/

        }
    }

}
