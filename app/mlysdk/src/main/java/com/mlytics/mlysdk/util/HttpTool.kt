package com.mlytics.mlysdk.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HttpTool {

    fun build(headers: Map<String, String?>? = null): Headers {
        var builder = Headers.Builder()
        headers?.forEach {
            if (it.value != null) {
                builder.add(it.key, it.value.toString())
            }
        }
        return builder.build()
    }

    fun queries(url: String, queries: Map<String, Any?>? = null): String {
        if (queries == null) {
            return url
        }
        return StringBuilder().append(url).apply {
            queries.forEach {
                if (it.value != null) {
                    append(if (length == url.length) "?" else "&")
                    append(it.key)
                    append("=")
                    val value = URLEncoder.encode(it.value.toString(), "utf8")
                    append(value)
                }
            }
        }.toString()
    }

    fun form(queries: Map<String, Any?>? = null): FormBody {
        var build = FormBody.Builder()
        queries?.forEach {
            if (it.value != null) {
                build.add(it.key, it.value.toString())
            }
        }
        return build.build()
    }

    fun build(
        url: String, queries: Map<String, Any?>?, headers: Map<String, String?>? = null
    ): Request {
        val url = HttpTool.queries(url, queries)
        Logger.debug("http ${url}")
        return Request.Builder().apply {
            url(url)
            if (headers != null && headers.isNotEmpty()) {
                headers(build(headers))
            }
        }.build()
    }

}

class PrepareCall(
    var url: String,
    var queries: Map<String, Any?>? = null,
    var headers: Map<String, String?>? = null,
    var timeout: Long = 30000
) : Cancelable {

    companion object {
        var client = OkHttpClient.Builder().build()
    }

    var call: Call?
    var request: Request
    var response: Response? = null
    var error: Exception? = null
    var isCanceled: Boolean = false
        get() {
            return call?.isCanceled() ?: false
        }

    init {
        this.request = HttpTool.build(url, queries, headers)
        this.call = client.newCall(request)
    }

    override fun cancel() {
        call?.cancel()
    }

    override var isAborted: Boolean
        get() {
            return isCanceled
        }
        set(value) {}

    suspend fun await(): PrepareCall {

//        val timeoutJob = CoroutineScope(Dispatchers.Default).launch {
//            delay(timeout)
//            call?.cancel()
//        }

        suspendCoroutine { continuation ->
            call?.enqueue(object : Callback {
                override fun onFailure(c: Call, e: IOException) {
                    call = null
                    error = e
                    response = null
//                    timeoutJob.cancel()
                    continuation.resume(Unit)
                }

                override fun onResponse(c: Call, resp: Response) {
                    call = null
                    error = null
                    response = resp
//                    timeoutJob.cancel()
                    continuation.resume(Unit)
                }
            })
        }

        return this
    }

    inline fun <reified T> toObject(): T? {
        if (this is T) {
            return this
        }
        if (Unit is T) {
            return Unit
        }
        val type = object : TypeToken<T>() {}.type
        val string: String = this.response?.body?.string() ?: return null
        Logger.debug("http ${this.request.url} ${string}")
        return Gson().fromJson<T>(string, type)
    }

}

