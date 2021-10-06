package com.sirius.library.agent.consensus.simple.messages

import com.sirius.library.agent.aries_rfc.AriesProtocolMessage
import com.sirius.library.utils.JSONArray
import com.sirius.library.utils.JSONObject

open class SimpleConsensusMessage(msg: String) : AriesProtocolMessage(msg) {
    val participants: List<String>
        get() {
            val res: MutableList<String> = ArrayList<String>()
            if (getMessageObj().has("participants")) {
                val jArr: JSONArray = getMessageObj().optJSONArray("participants") ?: JSONArray()
                for (o in jArr) {
                    if (o is String) res.add(o as String)
                }
            }
            return res
        }

    abstract class Builder<B : Builder<B>> protected constructor() :
        AriesProtocolMessage.Builder<B>() {
        var participants: List<String>? = null
        fun setParticipants(participants: List<String>?): B {
            this.participants = participants
            return self()
        }

        override fun generateJSON(): JSONObject {
            val jsonObject: JSONObject = super.generateJSON()
            if (participants != null) {
                jsonObject.put("participants", participants)
            } else {
                jsonObject.put("participants", JSONArray())
            }
            return jsonObject
        }
    }

    companion object {
        const val PROTOCOL = "simple-consensus"
    }
}
