package com.sirius.library.agent.wallet.abstract_wallet.model

enum class NYMRole(//none
    var value: Int?,  var names: String
) {
    COMMON_USER(null, "null"),  //0
    TRUSTEE(0, "TRUSTEE"),  //0
    STEWARD(2, "STEWARD"),  //2
    TRUST_ANCHOR(101, "TRUST_ANCHOR"),  //101
    NETWORK_MONITOR(201, "NETWORK_MONITOR"),  //201
    RESET(null, "");

    override fun toString(): String {
        return names
    }
}