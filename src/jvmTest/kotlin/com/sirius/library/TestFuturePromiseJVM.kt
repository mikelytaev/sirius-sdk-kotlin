package com.sirius.library

import com.sirius.library.base.CompleteFuture
import com.sirius.library.errors.sirius_exceptions.SiriusPendingOperation
import com.sirius.library.helpers.ConfTest
import com.sirius.library.messaging.Message
import com.sirius.library.models.P2PModel
import com.sirius.library.rpc.AddressedTunnel
import com.sirius.library.rpc.Future
import com.sirius.library.utils.JSONObject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.*

class TestFuturePromiseJVM {
   lateinit var confTest: ConfTest
     var p2pPair: Pair<P2PModel, P2PModel>? =null
    @BeforeTest
    fun configureTest() {
      /*  confTest = ConfTest.newInstance()
        p2pPair = confTest.createP2P()*/
    }

    @Test
    fun testSane() {
        val agent_to_sdk: AddressedTunnel = p2pPair!!.first.getTunneli()
        val sdk_to_agent: AddressedTunnel = p2pPair!!.second.getTunneli()
        val future = Future(sdk_to_agent)
        var isSiriusPendingOperation = false
        try {
            future.getValue()
        } catch (siriusPendingOperation: SiriusPendingOperation) {
            isSiriusPendingOperation = true
        }
        assertTrue(isSiriusPendingOperation)
        val expected = "Test OK"
        val promiseMsgObj: JSONObject = JSONObject()
        promiseMsgObj.put("@type", Future.MSG_TYPE)
        promiseMsgObj.put("@id", "promise-message-id")
        promiseMsgObj.put("is_tuple", false)
        promiseMsgObj.put("is_bytes", false)
        promiseMsgObj.put("value", expected)
        promiseMsgObj.put("exception", JSONObject.NULL)
        val threadObject: JSONObject = JSONObject()
        threadObject.put("thid", future.promise().id)
        promiseMsgObj.put("~thread", threadObject)
        val message = Message(promiseMsgObj.toString())
        var isWait = false
        try {
            isWait = future.waitPromise(5)
        } catch (ignored: Exception) {
        }
        assertFalse(isWait)
        agent_to_sdk.post(message)
        val isOk: Boolean = future.waitPromise(5)
        assertTrue(isOk)
        try {
            val actual: Any? = future.getValue()
            assertEquals(expected, actual.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Test
    fun testCompletableFuture() {
        var readFuture: CompletableFuture<ByteArray?> =
            CompletableFuture()
        println("readFuture=")
        Thread(Runnable {
            Thread.sleep(10000)
            readFuture.complete(ByteArray(0))
        }).start()
        val byteArray =  readFuture.get(15, TimeUnit.SECONDS)
        println("byteArray="+byteArray)

    }

    @Test
    fun testCompletableFuture1() {
        var readFuture: CompleteFuture<ByteArray?> =
            CompleteFuture()
        println("readFuture=")
        val byteArray =  readFuture.get(15, TimeUnit.SECONDS)
        Thread(Runnable {
            Thread.sleep(10000)
            readFuture.complete(ByteArray(0))
        }).start()
        println("byteArray="+byteArray)

    }

}
