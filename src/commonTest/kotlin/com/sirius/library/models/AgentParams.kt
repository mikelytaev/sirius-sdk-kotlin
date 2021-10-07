package com.sirius.library.models

import com.sirius.library.agent.model.Entity
import com.sirius.library.encryption.P2PConnection

class AgentParams(
    var serverAddress: String,
    var credentials: String,
    connection: P2PConnection,
    entitiesList: List<Entity>
) {
    var connection: P2PConnection
    var entitiesList: List<Entity>
    fun getConnectioni(): P2PConnection {
        return connection
    }

    fun getEntitiesListi(): List<Entity> {
        return entitiesList
    }

    init {
        this.connection = connection
        this.entitiesList = entitiesList
    }
}
