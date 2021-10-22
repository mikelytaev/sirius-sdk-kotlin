/*
package examples.covid

import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.hub.CloudHub

abstract class BaseParticipant(
    config: CloudHub.Config,
    pairwises: List<Pairwise>?,
    covidMicroledgerName: String?,
    me: Pairwise.Me?
) {
    var loop = false
    var config: CloudHub.Config
    var pairwises: List<Pairwise>?
    var thread: java.lang.Thread? = null
    var covidMicroledgerName: String?
    var me: Pairwise.Me?
    var covidMicroledgerParticipants: MutableList<String>
    fun start() {
        loop = true
        if (thread == null) {
            thread = java.lang.Thread(java.lang.Runnable { routine() })
            thread.start()
        }
    }

    fun stop() {
        loop = false
        thread.interrupt()
        thread = null
    }

    protected abstract fun routine()

    init {
        this.config = config
        this.pairwises = pairwises
        this.covidMicroledgerName = covidMicroledgerName
        this.me = me
        covidMicroledgerParticipants = ArrayList<String>()
        if (me != null) {
            me!!.did?.let {
                covidMicroledgerParticipants.add(it)
            }

        }
        if (pairwises != null) {
            for (pw in pairwises) {
                pw.their?.did?.let {
                    covidMicroledgerParticipants.add(it)
                }

            }
        }
    }
}

*/
