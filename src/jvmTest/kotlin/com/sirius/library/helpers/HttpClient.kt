package com.sirius.library.helpers

import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

actual class HttpClient actual constructor() {
    actual fun get(url: String): Pair<Boolean, String?> {
            return try {
             val httpclient: CloseableHttpClient = HttpClients.createDefault()
             val httpGet = HttpGet(url)
             val response1: CloseableHttpResponse = httpclient.execute(httpGet)
             try {
                 val entity1: HttpEntity = response1.getEntity()
                 val `in`: java.io.BufferedReader = java.io.BufferedReader(
                     java.io.InputStreamReader(
                         entity1.getContent()
                     )
                 )
                 var inputLine: String?
                 val response: java.lang.StringBuffer = java.lang.StringBuffer()
                 while (`in`.readLine().also { inputLine = it } != null) {
                     response.append(inputLine)
                 }
                 `in`.close()
                 EntityUtils.consume(entity1)
                 // print result
                 Pair(true, response.toString())
             } finally {
                 response1.close()
             }
         } catch (e: java.net.MalformedURLException) {
             e.printStackTrace()
             Pair(false, e.message)
         } catch (e: java.io.IOException) {
             e.printStackTrace()
             Pair(false, e.message)
         }
    }
}