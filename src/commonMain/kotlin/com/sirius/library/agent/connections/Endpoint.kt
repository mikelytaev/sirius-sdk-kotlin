package com.sirius.library.agent.connections

/**
 * Active Agent endpoints
 * https://github.com/hyperledger/aries-rfcs/tree/master/concepts/0094-cross-domain-messaging
 */
class Endpoint {
    var address: String
    var routingKeys: List<String>
    var isDefault = false

    constructor(address: String, routingKeys: List<String>, isDefault: Boolean) {
        this.address = address
        this.routingKeys = routingKeys
        this.isDefault = isDefault
    }

    constructor(address: String, routingKeys: List<String>) {
        this.address = address
        this.routingKeys = routingKeys
    }

    constructor(address: String) {
        this.address = address
        routingKeys = ArrayList<String>()
    }
}

