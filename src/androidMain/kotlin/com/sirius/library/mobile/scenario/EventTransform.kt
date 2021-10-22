package com.sirius.library.mobile.scenario

import com.sirius.library.agent.listener.Event
import com.sirius.library.messaging.Message


class EventTransform {

    companion object {

        fun eventToPair(event: Event): Pair<String?, Message?> {
            var theirDid = event.pairwise?.their?.did
            val message = event.message()
            return Pair(theirDid, message)
        }

    }
}