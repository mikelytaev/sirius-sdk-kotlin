package com.sirius.library.mobile.helpers


import android.util.Log
import com.sirius.library.agent.listener.Event
import com.sirius.library.agent.listener.Listener

import com.sirius.library.mobile.SiriusSDK
import java.util.concurrent.TimeUnit

/**
 * This is the helper class to show how the SDK workflow is done. Parse message from different channels (Websocket, FCM etc..)
 * and loop through scenario
 */
class ChanelHelper {


    companion object {
        private var chanelHelper: ChanelHelper? = null

        @JvmStatic
        fun getInstance(): ChanelHelper {
            if (chanelHelper == null) {
                chanelHelper = ChanelHelper()
            }
            return chanelHelper!!
        }
    }

    fun initListener() {
        if (listener == null) {
            Thread(Runnable {
                try {
                    listener = SiriusSDK.getInstance().context.subscribe()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
        }
    }

    var listener: Listener? = null


    fun parseMessage(message: String) {
        initListener()
        Thread(Runnable {
            try {
                Log.d("mylog200", "listener=" + listener)
                val cf  = listener!!.one
                SiriusSDK.getInstance().context.currentHub.getAgenti()?.receiveMsg(
                    message.toByteArray(
                        charset("UTF-8")
                    )
                )
                val event = cf?.get(60L, TimeUnit.SECONDS)
                val message = event?.message()
                //val type = message.type
                parseMessageByScenario(event)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }).start()

    }

    private fun parseMessageByScenario(event: Event?) {
        ScenarioHelper.getInstance().scenarioMap.forEach { scenario->
            event?.let {
                scenario.value.startScenario(event)
            }
        }
        Log.d("mylog2090", "event.message type" + event?.message())
    }

}