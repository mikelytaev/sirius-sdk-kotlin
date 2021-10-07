package com.sirius.library

import junit.framework.Assert.assertTrue
import org.junit.Test


class AndroidGreetingTest {

    @Test
    fun testExample() {
        assertTrue("Check Android is mentioned", Greeting().greeting().contains("Android"))
    }
}