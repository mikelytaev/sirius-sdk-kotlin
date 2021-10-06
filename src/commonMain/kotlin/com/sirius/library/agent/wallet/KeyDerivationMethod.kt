package com.sirius.library.agent.wallet

enum class KeyDerivationMethod(private val value: String) {
    DEFAULT("ARGON2I_MOD"), FAST("ARGON2I_INT"), RAW("RAW");

    companion object {
        private val map: MutableMap<String, KeyDerivationMethod> = HashMap<String, KeyDerivationMethod>()
        fun valueOfVal(value: String): KeyDerivationMethod? {
            return map[value]
        }

        init {
            for (errorCode in values()) {
                map[errorCode.value.toString()] = errorCode
            }
        }
    }

    override fun toString(): String {
        return value
    }
}