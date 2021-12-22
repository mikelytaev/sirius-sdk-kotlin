package com.sirius.library.mobile.scenario


class EventAction protected constructor(private val action: String) {
    companion object {
        val accept = EventAction("accept")
        val cancel = EventAction("cancel")
    }
}

interface EventActionListener{
    fun onActionStart(action: EventAction, id: String, comment: String?)
    fun onActionEnd(
        action: EventAction,
        id: String,
        comment: String?,
        success: Boolean,
        error: String?
    )
}
interface EventActionAbstract {
    suspend fun actionStart(action: EventAction, id: String, comment: String?, actionListener : EventActionListener? = null)
}