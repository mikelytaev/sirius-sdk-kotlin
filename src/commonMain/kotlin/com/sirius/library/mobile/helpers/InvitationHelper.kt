package com.sirius.library.mobile.helpers


import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.messaging.Message
import com.sirius.library.utils.Base64
import com.sirius.library.utils.StringUtils


class InvitationHelper {

    companion object {
        private var invitationHelper: InvitationHelper? = null

        fun getInstance(): InvitationHelper {
            if (invitationHelper == null) {
                invitationHelper = InvitationHelper()
            }
            return invitationHelper!!
        }
    }


    /**
     * Parse invitation url to valuable invitation JSON message
     */
    fun parseInvitationLink(rawValue: String?): String? {
        var parsedString = ""
        if (rawValue != null) {
            var ciParam: String? = null
           println("mylog500 rawValue=$rawValue")
            if (rawValue.contains("?c_i=")) {
                val ciParamStart = rawValue.indexOf("?c_i=")
                ciParam = rawValue.substring(ciParamStart + 5)
            }
            if (ciParam != null) {
                try {
                    println("mylog500 ciParam=$ciParam")
                    val bytes =  Base64.getUrlDecoder().decode(StringUtils.stringToBytes(ciParam, StringUtils.CODEC.UTF_8))
                    parsedString = StringUtils.bytesToString(bytes, StringUtils.CODEC.UTF_8)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                println("mylog500 decoded=$parsedString")
            }
        }
        if (validateInvitationUrl(parsedString)) {
            return parsedString
        }
        return null

    }


    /**
     * Validate that message is instance of Invitation
     */
    fun validateInvitationUrl(url: String): Boolean {
        try{
            val message = Message.restoreMessageInstance(url)
            if (message.first) {
                if (message.second is Invitation) {
                    return true
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        return false
    }


}