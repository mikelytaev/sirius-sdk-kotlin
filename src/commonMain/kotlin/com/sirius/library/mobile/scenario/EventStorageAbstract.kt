package com.sirius.library.mobile.scenario

import com.sirius.library.messaging.Message


interface EventStorageAbstract {
     fun eventStore(id : String, event: Pair<String?, Message?>?, accepted : Boolean)
     fun eventRemove(id : String)
     fun getEvent(id : String) : Pair<String?, Message>?
}