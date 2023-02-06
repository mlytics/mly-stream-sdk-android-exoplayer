package com.mlytics.mlysdk.kernel.core.service.filter

import android.net.Uri
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.core.servant.CDN
import com.mlytics.mlysdk.kernel.core.servant.MCDNSelector
import com.mlytics.mlysdk.kernel.core.service.filter.seeker.AbstractDownloaderProxy
import com.mlytics.mlysdk.kernel.service.filer.seeker.DownloadTaskManagerHolder
import com.mlytics.mlysdk.util.AbortController
import com.mlytics.mlysdk.util.Backoff
import com.mlytics.mlysdk.util.DateTool
import com.mlytics.mlysdk.util.DownloadTask
import com.mlytics.mlysdk.util.ExponentialDelayer
import com.mlytics.mlysdk.util.HTTPHeader
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MInternalError
import com.mlytics.mlysdk.util.MessageCode
import com.mlytics.mlysdk.util.Queue
import com.mlytics.mlysdk.util.WatchTool
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.net.URL

object BaseDownloaderProxyName {
    var HTTP = "http"
}

class AbstractDownloaderAgent {
    var _proxyNames: MutableList<String> = mutableListOf()

    var _proxyClasses: MutableMap<String, Class<AbstractDownloaderProxy>> = mutableMapOf()

    fun makeProxyClasses(): MutableList<Class<AbstractDownloaderProxy>> {
        val proxyNames = this._proxyNames
        var proxyClasses: MutableList<Class<AbstractDownloaderProxy>> = mutableListOf()

        for (proxyName in proxyNames) {
            val proxyClass = _proxyClasses[proxyName]
            if (proxyClass != null) {
                proxyClasses.add(proxyClass)
            }
        }

        return proxyClasses
    }

}

typealias Payload = DownloadTask

abstract class AbstractHttpDownloaderProxy(_resource: Resource) :
    AbstractDownloaderProxy(_resource) {
    var _requestURI: String? = null
    var _aborter: AbortController = AbortController()
    var _watch: WatchTool = WatchTool()
    var _responseStatus: Int? = null
        get() {
            return this._task?.state
        }

    override suspend fun _fetch() {
        this._buildAborter()

        this._buildPayload()
        when (this._responseStatus) {
            200 -> this._handleEntireContent()
            206 -> this._handlePartialContent()
            null -> this._handleInvalidContent()
        }
    }

    fun _buildAborter() {
        this._aborter = AbortController()
    }

    suspend fun _buildPayload() {
        val url = URL(this._resource.uri)
        var headers: MutableMap<String, String> = mutableMapOf()
        if (this._resource.size > 0) {
            headers[HTTPHeader.RANGE.rawValue] = "bytes=${this._resource.size}-"
        }

        this._task = DownloadTaskManagerHolder.instance.create(url, headers)
        if (this._task == null) {
            print("AbstractHttpDownloaderProxy._buildPayload _task nil ${url}")
        }

        this._aborter.task = this._task
        this._task?.done()

        this._task?.throwIfError()
    }

    suspend fun _handleEntireContent() {
        this._concatResourceChunk(false)
        this._updateResourceType()
        this._updateResourceTotal()
    }

    suspend fun _handlePartialContent() {
        this._concatResourceChunk(true)
        this._updateResourceType()
        this._updateResourceTotal()
    }

    fun _handleInvalidContent() {
        throw MInternalError(
            MessageCode.ESC011, null, mutableMapOf(Pair("payload", this._task ?: ""))
        )
    }

    fun _concatResourceChunk(isPartial: Boolean) {

    }

    fun _updateResourceType() {

    }

    fun _updateResourceTotal() {

    }

    override suspend fun _abort() {
        this._aborter.abort()
    }

}

typealias PerformanceResourceTiming = WatchTool

class MCDNDownloaderProxy(_resource: Resource) : AbstractHttpDownloaderProxy(_resource) {
    var _error: Error? = null
    var _source: CDN? = null
    var _sources: Queue<CDN>? = null
    var _startSize: Int? = null
    var _startTime: Double? = null
    var _endTime: Double? = null
    var _measurement: PerformanceResourceTiming = PerformanceResourceTiming()
    var _backoff = Backoff(
        delayer = ExponentialDelayer(), multiplier = 1.1, maxInterval = 5000
    )
    var _shouldRetry: Boolean = false
        get() {
            return this._sources?.first != null
        }

    var _isWithinInitialTimeout: Boolean = false
        get() {
            val timeout =
                (KernelSettings.download.httpInitialTimeout * 1000 * _resource.priority).toLong()
            return !_resource.ctime.hasElapsedTimeS(timeout)
        }

    override suspend fun _setup() {
        this._resetStates()

        this._checkStates()
        this._ensureSources()

        this._fetchSource()
        this._buildRequestURI()
    }

    suspend fun _resetStates() {
        this._error = null
        this._startSize = _resource.size
        this._startTime = DateTool.now()
    }

    suspend fun _checkStates() {
        if (!_resource.isShareable) {
            return
        }

        if (this._isWithinInitialTimeout) {
            throw MInternalError(MessageCode.ESC012)
        }

    }

    suspend fun _ensureSources() {
        if (this._sources == null) {
            val sel = MCDNSelector.process(_resource)
            this._sources = Queue(sel._outcome)
        }

    }

    suspend fun _fetchSource() {
        this._source = this._sources?.removeFirst()
    }

    suspend fun _buildRequestURI() {

        this._requestURI = Uri.parse(_resource.uri).run {
            var _port = if (port == -1) "" else ":${port}"
            "https://${host}${_port}${path}"
        }

    }

    override suspend fun _fetch() {
        this._buildMeasurement()

        super._fetch()
        this._stopMeasurement()
        runBlocking {
            async {
                _recordDownload()
            }
        }

        this._clearPerformance()
    }

    override suspend fun fetch() {
        while (true) {
            try {

                this._setup()

                this._fetch()
                break
            } catch (e: Exception) {
                Logger.error("download error MCDNDownloaderProxy.fetch ${_requestURI}")
                Logger.error("", e)
                if (!this._shouldRetry) {
                    throw e
                }

            }

        }

    }

    suspend fun _buildMeasurement() {
        this._watch.start()
    }

    suspend fun _stopMeasurement() {
        this._watch.stop()
        Logger.debug("mcdn download url=${_requestURI} cost=${this._watch.elapsedTimeS()}}")
    }

    suspend fun _recordDownload() {
// MCDNDownloadRecorder.process(
//            MCDNDownloadRecorderOptions(source:
//            this._source,
//            resource: _resource,
//            payload: _task,
//            error: this._error, aborter: _aborter, requestURI: _requestURI, startSize: this._startSize, startTime: this._startTime, measurement: this._measurement))
    }

    suspend fun _clearPerformance() {

    }

}

object CDNOriginKeeper {
    var host: String? = null
    fun setOrigin(url: String) {
        val uc = Uri.parse(url)
        host = uc.host
    }

    fun getOrigin(url: URL): URL {

        return url.run {
            val _port = if (port == -1) "" else port.toString()
            val s = "https://${host}${_port}${path}?${query}"
            URL(s)
        }

    }

}
