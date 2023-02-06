package com.mlytics.mlysdk.kernel.core.service.filter

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.core.service.filter.seeker.AbstractFileDownloader
import com.mlytics.mlysdk.kernel.service.filer.model.Request
import com.mlytics.mlysdk.util.Cache
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MInternalError
import com.mlytics.mlysdk.util.MessageCode
import com.mlytics.mlysdk.util.SpecTask
import com.mlytics.mlysdk.util.TaskManager
import com.mlytics.mlysdk.util.TaskState

class ResourceCache(var max: Int = KernelSettings.download.maxCacheItems) {
    var _cache: Cache<Resource> = Cache<Resource>(max)

    fun has(id: String): Boolean {
        return this._cache.has(id)
    }

    fun set(id: String, resource: Resource, ttl: Long) {
        this._cache.set(id, resource, ttl)
    }

    fun get(id: String): Resource? {
        return this._cache.get(id)
    }

}

abstract class AbstractFileRequester {

    companion object {
        const val ABORTED_REQUEST_EXPIRY_TIME: Long = 10 * 60 * 1000
    }

    var _requestPool: MutableMap<String, Request> = mutableMapOf()
    var _taskManager: TaskManager<Any> = TaskManager()
    suspend fun activate() {
        this._activate()
    }

    abstract suspend fun _activate()

    suspend fun deactivate() {
        this._deactivate()
    }

    abstract suspend fun _deactivate()

    abstract suspend fun _buildTasks()

    suspend fun fetch(resource: Resource): Request? {
        var request: Request?
        if (this._hasRequested(resource)) {
            request = this._fetchRequest(resource)
        } else {
            request = this._buildRequest(resource)
        }
        if (request?.isAborted == true) {
            request = this._buildRequest(resource)
        }

        return request
    }

    suspend fun abort(resource: Resource) {
        val request = this._fetchRequest(resource)
        if (request != null) {
            this._abortRequest(request)
            this._clearRequest(resource)
        }

    }

    suspend fun clear(resource: Resource) {
        this._clearRequest(resource)
    }

    suspend fun _buildRequest(resource: Resource): Request? {
        var task = this._buildObtainResourceTask(resource)
        if (task != null) else {
            Logger.error("_buildRequest nil task")
            return null
        }

        val request = Request(task, resource)
        this._storeRequest(resource, request)
        return request
    }

    abstract suspend fun _buildObtainResourceTask(resource: Resource): SpecTask?

    fun _hasRequested(resource: Resource): Boolean {
        return this._requestPool[resource.id] != null
    }

    fun _fetchRequest(resource: Resource): Request? {
        return this._requestPool[resource.id]
    }

    fun _storeRequest(resource: Resource, request: Request) {
        this._requestPool[resource.id!!] = request
    }

    fun _clearRequest(resource: Resource) {
        this._requestPool.remove(resource.id)
    }

    suspend fun _abortRequest(request: Request) {
        request.abort(FileRequesterError.REQUEST_ABORTED)
    }

    suspend fun _buildClearRequestTask() {
        this._taskManager.createCyclicTask(TaskState(
            name = FileRequesterTaskName.CLEAR_REQUEST,
            sleepFirst = true,
            sleepSeconds = 20.0,
            sleepJitter = 0.0,
            maxErrorRetry = -1,
            maxTotalRetry = -1
        ) {
            this._execClearRequestTaskCallee()
        })
    }

    suspend fun _execClearRequestTaskCallee() {
        val requests = this._requestPool.values

        for (request in requests) {
            if (request.isAborted && request.resource.mtime.hasElapsedTimeS(
                    AbstractFileRequester.ABORTED_REQUEST_EXPIRY_TIME.times(1000).toLong()
                )
            ) {
                this._clearRequest(request.resource)
            }
        }

    }

}

object FileRequesterTaskName {
    const val CLEAR_REQUEST = "file requester: clear request"
    const val OBTAIN_RESOURCE = "file requester: obtain resource"
}

object FileRequesterError {
    val REQUEST_ABORTED = MInternalError(MessageCode.ESC000)
}

open class AbstractObtainResourceDuty(options: CommonTaskOptions<Resource>) :
    CommonDuty<Resource>(options) {

    lateinit var _downloader: AbstractFileDownloader

    suspend fun callee() {

        this._downloader.fetch()
    }

    suspend fun onFail() {
        this._downloader.abort()
    }

    suspend fun onCancel() {
        this._downloader.abort()
    }

}

typealias ObtainResourceDutyOptions = CommonTaskOptions<Resource>

class CommonTaskOptions<T> {
    var content: T? = null
}

open class CommonDuty<T>(var options: CommonTaskOptions<T>)
