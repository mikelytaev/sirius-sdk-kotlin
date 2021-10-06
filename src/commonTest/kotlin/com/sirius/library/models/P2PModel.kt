package com.sirius.library.models

import com.sirius.library.encryption.P2PConnection
import com.sirius.library.rpc.AddressedTunnel

class P2PModel(p2p: P2PConnection, tunnel: AddressedTunnel) {
    var p2p: P2PConnection
    var tunnel: AddressedTunnel
    fun getP2p(): P2PConnection {
        return p2p
    }

    fun getTunnel(): AddressedTunnel {
        return tunnel
    }

    init {
        this.p2p = p2p
        this.tunnel = tunnel
    }
}
