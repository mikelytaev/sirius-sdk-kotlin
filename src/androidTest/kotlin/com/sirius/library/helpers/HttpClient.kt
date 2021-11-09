package com.sirius.library.helpers

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


actual class HttpClient actual constructor() {
    actual fun get(url: String): Pair<Boolean, String?> {
       return try {
           val client = OkHttpClient()
           val request: Request = Request.Builder()
               .url(url)
               .build()
           val response: Response = client.newCall(request).execute()
           val message = response.body?.string()
           return  Pair(true, message)
        } catch (e: java.net.MalformedURLException) {
            e.printStackTrace()
            Pair(false, e.message)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            Pair(false, e.message)
        }

    }
}