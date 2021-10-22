package com.sirius.library.mobile.helpers

import android.util.Base64
import android.util.Log
import com.sirius.library.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation
import com.sirius.library.messaging.Message


class InvitationHelper {

    companion object {
        private var invitationHelper: InvitationHelper? = null

        @JvmStatic
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
            Log.d("mylog500", "rawValue=$rawValue")
            if (rawValue.contains("?c_i=")) {
                val ciParamStart = rawValue.indexOf("?c_i=")
                ciParam = rawValue.substring(ciParamStart + 5)
            }
            if (ciParam != null) {
                try {
                    Log.d("mylog500", "ciParam=$ciParam")
                    parsedString = String(
                        Base64.decode(
                            ciParam.toByteArray(charset("UTF-8")),
                            Base64.NO_WRAP or Base64.URL_SAFE
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d("mylog500", "decoded=$parsedString")
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