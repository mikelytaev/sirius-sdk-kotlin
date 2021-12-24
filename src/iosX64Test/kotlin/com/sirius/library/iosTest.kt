package com.sirius.library

import com.sirius.library.utils.SDK
import platform.SceneKit.SCNSceneSourceAssetModifiedDateKey
import platform.darwin.dispatch_async
import platform.darwin.dispatch_queue_global_t
import kotlin.test.Test
import kotlin.test.assertTrue
class IosGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greeting().contains("iOS"), "Check iOS is mentioned")
    }

    @Test
    fun testExample2() {
      // SDK.initializeCrypto()
    }

    @Test
    fun testExample3() {

        dispatch_async(dispatch_queue_global_t()){

        }
        // SDK.initializeCrypto()
    }
}