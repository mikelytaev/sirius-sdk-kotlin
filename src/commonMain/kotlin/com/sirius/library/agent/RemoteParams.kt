package com.sirius.library.agent

class RemoteParams private constructor(params: Map<String, Any?>) {
    var params: Map<String, Any?> = HashMap<String, Any?>()

    class RemoteParamsBuilder private constructor() {
        private val params: MutableMap<String, Any?> = HashMap<String, Any?>()
        fun add(name: String, `object`: Any?): RemoteParamsBuilder {
         //   `object`?.let {
                params[name] = `object`
          //  }
            return this
        }

        fun build(): RemoteParams {
            return RemoteParams(params)
        }

        companion object {
            fun create(): RemoteParamsBuilder {
                return RemoteParamsBuilder()
            }
        }
    }

    init {
        this.params = params
    }
}
