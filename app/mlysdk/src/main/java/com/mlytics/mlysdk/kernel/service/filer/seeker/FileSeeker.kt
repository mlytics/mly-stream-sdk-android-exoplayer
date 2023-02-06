package com.mlytics.mlysdk.kernel.service.filer.seeker

import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.core.service.filter.AbstractFileRequester
import com.mlytics.mlysdk.kernel.core.service.filter.AbstractObtainResourceDuty
import com.mlytics.mlysdk.kernel.core.service.filter.ObtainResourceDutyOptions
import com.mlytics.mlysdk.kernel.core.service.filter.ResourceCache
import com.mlytics.mlysdk.kernel.core.service.filter.model.ResourceTTLSuggester
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.SpecTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object FileSeeker {

    private lateinit var _requester: AbstractFileRequester
    var resourceCache: ResourceCache = ResourceCache()

    suspend fun fetch(resource: Resource): Resource? {
        return this._fetch(resource, true)
    }

    suspend fun prefetch(resource: Resource): Resource? {
        return this._fetch(resource, false)
    }

    suspend fun _fetch(resource: Resource, isBlocking: Boolean): Resource? {
        val res = _fetchCache(resource)
        if (res != null) {
            Logger.debug("cache got count=${resource.size}/${resource.total ?: -1} ${resource.uri}")
            return res
        }

        return this._request(resource, isBlocking)
    }

    suspend fun _request(resource: Resource, isBlocking: Boolean): Resource? {
        val request = this._requester.fetch(resource)
        if (request != null) else {
            return null
        }

        this._storeCache(request.resource)
        if (isBlocking) {
            request.done()
            this._storeCache(request.resource)
            this._requester.clear(request.resource)
        } else {

            runBlocking {
                launch(Dispatchers.IO) {
                    request.done()
                    _storeCache(request.resource)
                    _requester.clear(request.resource)
                }
            }

        }

        return request.resource
    }

    suspend fun abort(resource: Resource) {
        this._requester.abort(resource)
    }

    fun _hasCached(resource: Resource): Boolean {
        return this.resourceCache.has(resource.id!!)
    }

    fun _fetchCache(resource: Resource): Resource? {
        return this.resourceCache.get(resource.id!!)
    }

    fun _storeCache(resource: Resource) {
        val cacheTTL = ResourceTTLSuggester.give(resource)
        this.resourceCache.set(resource.id!!, resource, cacheTTL)
    }

    fun _hasFetched(resource: Resource): Boolean {
        return resource.isComplete
    }

    fun _initialize() {
        this._requester = FileRequester()
    }

    suspend fun _activate() {
        this._requester.activate()
    }

    suspend fun _deactivate() {
        this._requester.deactivate()
    }

}

class FileRequester : AbstractFileRequester() {
    override suspend fun _activate() {
        DownloadTaskManagerHolder.instance.activate()
        this._buildTasks()
    }

    override suspend fun _deactivate() {
        DownloadTaskManagerHolder.instance.deactivate()
        this._requestPool.clear()
    }

    override suspend fun _buildTasks() {
        _buildClearRequestTask()
    }

    override suspend fun _buildObtainResourceTask(resource: Resource): SpecTask? {
        val options = ObtainResourceDutyOptions()
        options.content = resource
        val duty = ObtainResourceDuty(options)
        try {

            duty._downloader.fetch()
        } catch (e: Exception) {
            Logger.error("FileRequester._buildObtainResourceTask error", e)
            duty.onFail()
        }

        if (duty._downloader._task == null) {
            Logger.error("FileRequester._buildObtainResourceTask task nil")
        }

        return duty._downloader._task
    }

}

class ObtainResourceDuty(options: ObtainResourceDutyOptions) : AbstractObtainResourceDuty(options) {

    init {
        this._initialize()
    }

    fun _initialize() {
        this._downloader = FileDownloader(options.content!!)
    }

}
