package com.sirius.library.utils

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.secretbox.SecretBox
import com.ionspin.kotlin.crypto.util.LibsodiumRandom
import com.ionspin.kotlin.crypto.util.LibsodiumUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object SDK {

    fun initiateSDK() {
        CoroutineScope(Dispatchers.Main).launch {
            initializeCrypto()
        }
    }


    suspend fun initializeCrypto() {
        LibsodiumInitializer.initialize()
    }
}