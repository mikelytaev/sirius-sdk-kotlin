package com.sirius.library.agent.connections

import com.sirius.library.agent.RemoteParams

interface RemoteCall<T> {
    fun remoteCall(type: String, params: RemoteParams.RemoteParamsBuilder? = null): T?
}
