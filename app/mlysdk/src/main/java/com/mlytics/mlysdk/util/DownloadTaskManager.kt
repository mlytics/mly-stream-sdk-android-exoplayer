package com.mlytics.mlysdk.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Response
import java.net.URL

//import Foundation
class DownloadTaskManager : Object {
    private var downloads: MutableMap<String, DownloadTask> = mutableMapOf()
    var timeout: Double

    constructor (timeout: Double = 60.0) {
        this.timeout = timeout
    }

    suspend fun activate() {

    }

    suspend fun deactivate() {
        this.purgeTasks()
    }

    private fun build(
        url: URL,
        command: DownloadTaskCommand = DownloadTaskCommand.download,
        headers: MutableMap<String, String> = mutableMapOf()
    ): DownloadTask? {
        val url = standard(url)
        if (url != null) else {
            Logger.error("DownloadTaskManager.build nil url")
            return null
        }

        var download = this.downloads[url.toString()]
        if (download != null) {
            when (command) {
                DownloadTaskCommand.abort -> download.abort()
                DownloadTaskCommand.exit -> download.exit()
                else -> download.resume()
            }
            return download
        }

        if (command != DownloadTaskCommand.download) {
            Logger.error("command must be .download, but ${command}")
        }

        download = DownloadTask(url, this.timeout, headers)
        this.downloads[url.toString()] = download
        download.start()
        return download
    }

    fun abort(url: URL): DownloadTask? {
        return this.build(url, DownloadTaskCommand.abort)
    }

    fun exit(url: URL) {
        this.build(url, DownloadTaskCommand.exit)
        this.downloads.remove(url.toString())
    }

    fun create(
        url: URL, headers: MutableMap<String, String> = mutableMapOf()
    ): DownloadTask? {
        return this.build(url, DownloadTaskCommand.download, headers)
    }

    fun purgeTasks() {

        for (t in this.downloads.values) {
            t.exit()
        }

    }

    fun standard(url: URL): URL? {
        var s = url.run {
            val p = if (port == null || port == -1) "" else port.toString()
            "${protocol}://${host}:${p}${path}?${query}"
        }
        var u = URL(s)
        return u
    }

}

class DownloadTask : Object, SpecTask {
    override var isCompleted: Boolean? = false
        get() {
            return task?.response != null
        }

    override var isAborted: Boolean = false
        get() {
            return task?.isCanceled ?: false
        }

    var url: URL
    var timeout: Double
    var task: PrepareCall? = null
    var response: Response? = null
    override var data: ByteArray? = null
    override var type: String? = null
    var headers: MutableMap<String, String>? = null
    var taskState: SpecTaskState = SpecTaskState.initial

    override var state: Int? = null
        get() {
            return this.response?.code
        }

    var error: Exception? = null
    var condition: Condition = Condition()

    constructor (
        url: URL, timeout: Double = 60.0, headers: MutableMap<String, String> = mutableMapOf()
    ) {
        this.url = url
        this.timeout = timeout
        this.headers = headers
    }

    override suspend fun done() {
        if (this.taskState == SpecTaskState.completed) {
            return
        }
        this.condition.done()
        Logger.debug("download done ${url} ${data?.size ?: -1}")
    }

    override suspend fun throwIfError() {
        val error = this.error
        if (error != null) {
            Logger.error("download error ${url}", error)
            throw error
        }

    }

    override fun cancel() {
        task?.cancel()
    }

    fun start() {
        Logger.debug("download start ${url}")
        var call = PrepareCall(url.toString())
        this.task = call
        runBlocking {
            launch(Dispatchers.IO) {
                call.await()
                complete()
            }
        }
    }

    fun complete() {
        val error = task?.error
        if (error != null) {
            Logger.error("download error", error)
        }

        this.data = task?.response?.body?.bytes()
        this.type = this.getResponseHeader(HTTPHeader.CONTENT_TYPE)
        this.error = error
        this.condition.pass(error)
    }

    override fun abort() {
        task?.cancel()
    }

    override fun exit() {
        task?.cancel()
    }

    fun resume(headers: Map<String, String>? = null) {
        TODO("resume")
    }

    fun getResponseHeader(header: HTTPHeader): String? {
        val resp = response
        if (resp != null) {
            return resp.header(header.rawValue, null)
        }
        return null
    }

}

enum class DownloadTaskCommand {
    download, abort, exit, suspended
}

enum class SpecTaskState {
    initial, running, canceling, suspended, completed
}

interface SpecTask : Cancelable {
    var state: Int?
    var data: ByteArray?
    var type: String?
    var isCompleted: Boolean?

    suspend fun done()
    fun abort()
    fun exit()
    suspend fun throwIfError()
}
