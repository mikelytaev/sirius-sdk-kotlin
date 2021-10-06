package com.sirius.library

import assertTrue
import Test

class AndroidGreetingTest {

    @Test
    fun testExample() {
        assertTrue("Check Android is mentioned", Greeting().greeting().contains("Android"))
    }
}