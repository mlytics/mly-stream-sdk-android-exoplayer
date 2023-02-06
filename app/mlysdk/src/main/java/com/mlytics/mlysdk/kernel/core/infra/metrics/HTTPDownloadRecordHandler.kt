package com.mlytics.mlysdk.kernel.core.infra.metrics

import com.mlytics.mlysdk.kernel.core.const.base.FlowKey
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNMetricsDownload
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.DataSet
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.DomainMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.DomainMetricsDownload
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.DomainMetricsUsage
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.MetricsConstant
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.SourceMetrics
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.TimeSeriesData
import com.mlytics.mlysdk.kernel.core.infra.metrics.state.MetricsStatsHolder
import com.mlytics.mlysdk.kernel.core.infra.model.CDNConfigRecord
import com.mlytics.mlysdk.kernel.core.infra.model.CDNDownloadRecord
import com.mlytics.mlysdk.kernel.core.infra.model.HTTPDownloadRecord
import com.mlytics.mlysdk.util.AbstractFlow
import com.mlytics.mlysdk.util.DateTool
import com.mlytics.mlysdk.util.Flow
import com.mlytics.mlysdk.util.MathCalculator
import com.mlytics.mlysdk.util.TaskState
import com.mlytics.mlysdk.util.WeightedData

interface SpecCursorsTimeSeriesDataData {
    var cursors: MutableMap<String, TimeSeriesDataData<Double>>
}

data class CursorsTimeSeriesDataData(override var cursors: MutableMap<String, TimeSeriesDataData<Double>>) :
    SpecCursorsTimeSeriesDataData

data class TimeSeriesDataData<T>(var value: T? = null, var data: TimeSeriesData<T>? = null)

abstract class BaseMetricsHandler<T : SpecCursorsTimeSeriesDataData>(content: T) :
    AbstractFlow<T>(content) {

    abstract fun _dataset(download: DomainMetricsDownload): Pair<DataSet?, DataSet?>
    abstract suspend fun _exportMetrics(): Unit

    override suspend fun process() {
        this._iterateMetrics {
            this._ensureCursor()
            this._intakeIndex()
            this._intakeData()
            this._updateMetrics()
        }

        this._exportMetrics()
    }

    suspend fun _iterateMetrics(callback: suspend () -> Unit) {
        var metricsList: MutableList<Any> = mutableListOf()
        MetricsStatsHolder.cdns.data.values.forEach {
            metricsList.add(it)
        }

        metricsList.add(MetricsStatsHolder.origin)

        for (metrics in metricsList) {
            this._expose(FlowKey.METRICS, metrics)
            callback()
        }

        this._remove(FlowKey.METRICS, FlowKey.INDEX, FlowKey.DATA)
    }

    suspend fun _ensureCursor() {
        val metrics = this._require(FlowKey.METRICS) as DomainMetrics
        this._content.cursors[metrics.id!!] = TimeSeriesDataData<Double>()
    }

    suspend fun _intakeIndex() {
        val metrics = this._require(FlowKey.METRICS) as DomainMetrics
        val cursor = this._content.cursors[metrics.id!!]!!
        val (from, _) = this._dataset(metrics.download!!)
        val index = from?.dataset?.indexOfLast { it == cursor.data }
        this._expose(FlowKey.INDEX, index)
    }

    suspend fun _intakeData() {
        val metrics = this._require(FlowKey.METRICS) as DomainMetrics
        val index = this._require(FlowKey.INDEX) as Int
        val dataset = metrics.download!!.traffic.dataset
        var value = 0.0

        for (i in index until dataset.size) {
            value += dataset[i].value!!
        }

        val data = TimeSeriesData(value)
        this._expose(FlowKey.DATA, data)
    }

    suspend fun _updateMetrics() {
        val metrics = this._require(FlowKey.METRICS) as DomainMetrics
        val data = this._require(FlowKey.DATA) as TimeSeriesData<Double>
        val (from, to) = this._dataset(metrics.download!!)
        to?.dataset?.add(data)
        this._content.cursors[metrics.id!!] = TimeSeriesDataData(data = from?.dataset?.last())
    }

}

typealias  HTTPDownloadRecordDuty = TaskState

class HTTPDownloadRecordHandler : Flow() {
    override suspend fun process() {
        this._intakeRecord()
        this._intakeMetrics()
        this._updateMetrics()
        this._exportMetrics()
    }

    suspend fun _intakeRecord() {
        val record =
            MetricsCollector.instance._recordHub.extract(MetricsCollectorEvent.HTTP_DOWNLOAD_RECORD)
        this._expose(FlowKey.RECORD, record)
    }

    suspend fun _intakeMetrics() {
        val record = this._require(FlowKey.RECORD) as HTTPDownloadRecord
        var metrics: Any
        if (record.id == null) {
            metrics = MetricsStatsHolder.origin
        } else {
            metrics = MetricsStatsHolder.cdns[record.id!!]!!
        }

        this._expose(FlowKey.METRICS, metrics)
    }

    suspend fun _updateMetrics() {
        val record = this._require(FlowKey.RECORD) as HTTPDownloadRecord
        val metrics = this._require(FlowKey.METRICS) as DomainMetrics
        metrics.download!!.count.usage.dataset.add(TimeSeriesData(1.0, record.ctime!!))
        var value: Double = 1.0
        if (record.isSuccess!!) {
            metrics.download!!.count.success.dataset.add(TimeSeriesData(1.0, record.ctime!!))
        } else {
            value = 0.0
            metrics.download!!.count.failure.dataset.add(TimeSeriesData(1.0, record.ctime!!))
        }

        metrics.download!!.outcome.dataset.add(TimeSeriesData(value, record.ctime!!))
        metrics.download!!.traffic.dataset.add(
            TimeSeriesData(
                record.contentSize?.toDouble(), record.ctime!!
            )
        )
        if (record.bandwidth != null) {
            metrics.download!!.bandwidth.dataset.add(
                TimeSeriesData(
                    record.bandwidth,
                    record.ctime!!
                )
            )
        }

        MetricsStatsHolder.source.http_download_records.add(record)
    }

    suspend fun _exportMetrics() {
        val record = this._require(FlowKey.RECORD) as HTTPDownloadRecord
        ExportHTTPDownloadRecordHandler(record).process()
    }

}

data class HTTPDownloadPulseTrafficDutyContent(var cursors: MutableMap<String, TimeSeriesDataData<Double>> = mutableMapOf())

class HTTPDownloadPulseTrafficHandler : AbstractFlow<Unit>(content = Unit) {
    fun _dataset(download: DomainMetricsDownload): Pair<DomainMetricsUsage, DataSet> {
        return Pair(download.traffic, download.traffic.pulse)
    }

    suspend fun _exportMetrics() {
        ExportHTTPDownloadPulseTrafficHandler().process()
    }

    override suspend fun process() {
    }

}

typealias HTTPDownloadCumulativeTrafficHandlerContent = CursorsTimeSeriesDataData
typealias HTTPDownloadCumulativeTrafficHandlerOptions = HTTPDownloadCumulativeTrafficHandlerContent
typealias HTTPDownloadPulseTrafficHandlerContent = HTTPDownloadPulseTrafficDutyContent
typealias HTTPDownloadPulseTrafficHandlerOptions = HTTPDownloadPulseTrafficHandlerContent

class HTTPDownloadCumulativeTrafficHandler(content: HTTPDownloadCumulativeTrafficHandlerOptions) :
    BaseMetricsHandler<HTTPDownloadCumulativeTrafficHandlerOptions>(content) {
    override fun _dataset(download: DomainMetricsDownload): Pair<DataSet, DataSet> {
        return Pair(download.traffic, download.traffic.cumulation)
    }

    override suspend fun _exportMetrics() {
        ExportHTTPDownloadCumulativeTrafficHandler().process()
    }

}

class HTTPDownloadWMABandwidthHandler(content: HTTPDownloadWMABandwidthHandlerOptions) :
    AbstractFlow<HTTPDownloadWMABandwidthHandlerOptions>(content) {
    override suspend fun process() {
        this._iterateMetrics({
            this._intakeData()
            this._updateMetrics()
        })
        this._exportMetrics()
    }

    suspend fun _iterateMetrics(callback: suspend () -> Unit) {
        var metricsList: MutableList<Any> = mutableListOf()
        MetricsStatsHolder.cdns.data.values.forEach {
            metricsList.add(it)
        }

        metricsList.add(MetricsStatsHolder.origin)

        for (metrics in metricsList) {
            this._expose(FlowKey.METRICS, metrics)
            callback()
        }

        this._remove(FlowKey.METRICS, FlowKey.INDEX, FlowKey.DATA)
    }

    suspend fun _intakeData() {
        val metrics = this._require(FlowKey.METRICS) as DomainMetrics
        val now = DateTool.now()
        val baseline = now - MetricsConstant.DURATION_OF_WMA_DATA * 1
        val dataset = metrics.download!!.bandwidth.dataset.filter { data ->
            data.ctime >= baseline
        }

        val value: Double = MathCalculator.weightedAverage(dataset.map { data ->
            WeightedData(offset = data.ctime - baseline, value = data.value!!)
        })
        val data = TimeSeriesData(value)
        this._expose(FlowKey.DATA, data)
    }

    suspend fun _updateMetrics() {
        val metrics = this._require(FlowKey.METRICS) as DomainMetrics
        val data = this._require(FlowKey.DATA) as TimeSeriesData<Double>
        metrics.download!!.bandwidth.wma.dataset.add(data)
    }

    suspend fun _exportMetrics() {
        ExportHTTPDownloadWMABandwidthHandler().process()
    }

}

typealias HTTPDownloadWMABandwidthDutyContent = CursorsTimeSeriesDataData
typealias HTTPDownloadWMABandwidthHandlerContent = HTTPDownloadWMABandwidthDutyContent
typealias HTTPDownloadWMABandwidthHandlerOptions = HTTPDownloadWMABandwidthHandlerContent
typealias HTTPDownloadUsagePulseCountHandlerContent = CursorsTimeSeriesDataData
typealias HTTPDownloadUsagePulseCountHandlerOptions = HTTPDownloadUsagePulseCountHandlerContent

class HTTPDownloadUsagePulseCountHandler(content: HTTPDownloadUsagePulseCountHandlerOptions) :
    BaseMetricsHandler<HTTPDownloadUsagePulseCountHandlerOptions>(content) {
    override fun _dataset(download: DomainMetricsDownload): Pair<DomainMetricsUsage?, DataSet?> {
        if (download is CDNMetricsDownload) {
            return Pair(download.count.usage, download.count.usage.pulse)
        }

        return Pair(null, null)
    }

    override suspend fun _exportMetrics() {
        ExportHTTPDownloadUsagePulseCountHandler().process()
    }

}

typealias HTTPDownloadUsageCumulativeCountHandlerContent = CursorsTimeSeriesDataData
typealias HTTPDownloadUsageCumulativeCountHandlerOptions = HTTPDownloadUsageCumulativeCountHandlerContent

class HTTPDownloadUsageCumulativeCountHandler(content: HTTPDownloadUsageCumulativeCountHandlerOptions) :
    BaseMetricsHandler<HTTPDownloadUsageCumulativeCountHandlerOptions>(content) {
    override fun _dataset(download: DomainMetricsDownload): Pair<DomainMetricsUsage?, DataSet?> {
        if (download is CDNMetricsDownload) {
            return Pair(download.count.usage, download.count.usage.cumulation)
        }

        return Pair(null, null)
    }

    override suspend fun _exportMetrics() {
        ExportHTTPDownloadUsageCumulativeCountHandler().process()
    }

}

typealias HTTPDownloadSuccessPulseCountHandlerContent = CursorsTimeSeriesDataData
typealias HTTPDownloadSuccessPulseCountHandlerOptions = HTTPDownloadSuccessPulseCountHandlerContent

class HTTPDownloadSuccessPulseCountHandler(content: HTTPDownloadSuccessPulseCountHandlerOptions) :
    BaseMetricsHandler<HTTPDownloadSuccessPulseCountHandlerOptions>(content) {
    override fun _dataset(download: DomainMetricsDownload): Pair<DomainMetricsUsage, DataSet> {
        return Pair(download.count.success, download.count.success.pulse)
    }

    override suspend fun _exportMetrics() {
        ExportHTTPDownloadSuccessPulseCountHandler().process()
    }

}

class HTTPDownloadSuccessCumulativeCountHandler(content: CursorsTimeSeriesDataData) :
    BaseMetricsHandler<CursorsTimeSeriesDataData>(content) {
    override fun _dataset(download: DomainMetricsDownload): Pair<DomainMetricsUsage?, DataSet?> {
        if (download is CDNMetricsDownload) {
            return Pair(download.count.success, download.count.success.cumulation)
        }

        return Pair(null, null)
    }

    override suspend fun _exportMetrics() {
        ExportHTTPDownloadSuccessCumulativeCountHandler().process()
    }

}

class HTTPDownloadFailurePulseCountHandler(content: CursorsTimeSeriesDataData) :
    BaseMetricsHandler<CursorsTimeSeriesDataData>(content) {
    override fun _dataset(download: DomainMetricsDownload): Pair<DomainMetricsUsage?, DataSet?> {
        if (download is CDNMetricsDownload) {
            return Pair(download.count.failure, download.count.failure.pulse)
        }

        return Pair(null, null)
    }

    override suspend fun _exportMetrics() {
        ExportHTTPDownloadFailurePulseCountHandler().process()
    }

}

class HTTPDownloadFailureCumulativeCountHandler(content: CursorsTimeSeriesDataData) :
    BaseMetricsHandler<CursorsTimeSeriesDataData>(content) {
    override fun _dataset(download: DomainMetricsDownload): Pair<DomainMetricsUsage?, DataSet?> {
        if (download is CDNMetricsDownload) {
            return Pair(download.count.failure, download.count.failure.cumulation)
        }

        return Pair(null, null)
    }

    override suspend fun _exportMetrics() {
        ExportHTTPDownloadFailureCumulativeCountHandler().process()
    }

}

class CDNConfigRecordsHandler(content: Unit) : AbstractFlow<Unit>(content) {
    override suspend fun process() {
        this._intakeRecords()
        this._iterateRecords {
            this._updateMetrics()
        }

    }

    suspend fun _intakeRecords() {
        val records =
            MetricsCollector.instance._recordHub.extract(MetricsCollectorEvent.CDN_CONFIG_RECORDS) as MutableList<CDNConfigRecord>
        this._expose(FlowKey.RECORDS, records)
    }

    suspend fun _iterateRecords(callback: suspend () -> Unit) {
        val records = this._require(FlowKey.RECORDS) as MutableList<CDNConfigRecord>

        for (record in records) {
            this._expose(FlowKey.RECORD, record)
            callback()
        }

        this._remove(FlowKey.RECORD)
    }

    suspend fun _updateMetrics() {
        val record = this._require(FlowKey.RECORD) as CDNConfigRecord
        MetricsStatsHolder.setupCDN(record)
    }

}

class CDNDownloadRecordHandler : AbstractFlow<Unit>(content = Unit) {
    override suspend fun process() {
        this._intakeRecord()
        this._updateMetrics()
    }

    suspend fun _intakeRecord() {
        val record =
            MetricsCollector.instance._recordHub.extract(MetricsCollectorEvent.CDN_DOWNLOAD_RECORD) as CDNDownloadRecord
        this._expose(FlowKey.RECORD, record)
    }

    suspend fun _updateMetrics() {
        val record = this._require(FlowKey.RECORD) as CDNDownloadRecord
        val metrics = MetricsStatsHolder.cdns[record.id!!]!!
        metrics.cdndownload!!.meanBandwidth.dataset.add(
            TimeSeriesData(
                record.meanBandwidth, ctime = record.ctime!!
            )
        )
        metrics.cdndownload!!.meanAvailability.dataset.add(
            TimeSeriesData(
                record.meanAvailability, ctime = record.ctime!!
            )
        )
        metrics.cdndownload!!.currentScore.dataset.add(
            TimeSeriesData(
                record.currentScore, ctime = record.ctime!!
            )
        )
    }

}

class CDNDownloadLastMeanBandwidthHandler(content: CursorsTimeSeriesDataData) :
    AbstractFlow<CursorsTimeSeriesDataData>(content) {
    override suspend fun process() {
        this._iterateMetrics({
            this._ensureCursor()
            this._intakeData()
            this._updateMetrics()
        }
        )
        this._exportMetrics()
    }

    suspend fun _iterateMetrics(callback: suspend () -> Unit) {
        val metricsList = MetricsStatsHolder.cdns.data.values

        for (metrics in metricsList) {
            this._expose(FlowKey.METRICS, metrics)
            callback()
        }

        this._remove(FlowKey.METRICS, FlowKey.INDEX, FlowKey.DATA)
    }

    suspend fun _ensureCursor() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        this._content.cursors[metrics.id!!] = TimeSeriesDataData()
    }

    suspend fun _intakeData() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val cursor = this._content.cursors[metrics.id!!]!!
        val dataset = metrics.cdndownload!!.meanBandwidth.dataset
        val data = TimeSeriesData(cursor.value)
        this._expose(FlowKey.DATA, data)
    }

    suspend fun _updateMetrics() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val data = this._require(FlowKey.DATA) as TimeSeriesData<Double>
        val dataset = metrics.cdndownload!!.meanBandwidth.dataset
        metrics.cdndownload!!.meanBandwidth.last.dataset.add(data)
        this._content.cursors[metrics.id!!] =
            TimeSeriesDataData(data = dataset[dataset.size - 1], value = data.value)
    }

    suspend fun _exportMetrics() {
        ExportCDNDownloadLastMeanBandwidthHandler().process()
    }

}

class CDNDownloadLastMeanAvailabilityHandler(content: CursorsTimeSeriesDataData) :
    AbstractFlow<CursorsTimeSeriesDataData>(content) {
    override suspend fun process() {
        this._iterateMetrics({
            this._ensureCursor()
            this._intakeData()
            this._updateMetrics()
        })
        this._exportMetrics()

    }

    suspend fun _iterateMetrics(callback: suspend () -> Unit) {
        val metricsList = MetricsStatsHolder.cdns.data.values

        for (metrics in metricsList) {
            this._expose(FlowKey.METRICS, metrics)
            callback()
        }

        this._remove(FlowKey.METRICS, FlowKey.INDEX, FlowKey.DATA)
    }

    suspend fun _ensureCursor() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val cursor = this._content.cursors[metrics.id!!]
        if (cursor != null) {
            this._content.cursors[metrics.id!!] =
                TimeSeriesDataData(data = TimeSeriesData(0.0), value = 1.0)
        }

    }

    suspend fun _intakeData() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val cursor = this._content.cursors[metrics.id!!]!!
        val dataset = metrics.cdndownload!!.meanAvailability.dataset
        val data = TimeSeriesData<Double>(dataset[dataset.size - 1].value ?: cursor.value)
        this._expose(FlowKey.DATA, data)
    }

    suspend fun _updateMetrics() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val data = this._require(FlowKey.DATA) as TimeSeriesData<Double>
        val dataset = metrics.cdndownload!!.meanAvailability.dataset
        metrics.cdndownload!!.meanAvailability.last.dataset.add(data)
        this._content.cursors[metrics.id!!] =
            TimeSeriesDataData(data = dataset[dataset.size - 1], value = data.value)
    }

    suspend fun _exportMetrics() {
        ExportCDNDownloadLastMeanAvailabilityHandler().process()
    }

}

typealias CDNDownloadLastMeanAvailabilityHandlerContent = CursorsTimeSeriesDataData
typealias CDNDownloadLastMeanAvailabilityHandlerOptions = CDNDownloadLastMeanAvailabilityHandlerContent

class CDNDownloadLastCurrentScoreHandler(content: CDNDownloadLastMeanAvailabilityHandlerOptions) :
    AbstractFlow<CDNDownloadLastMeanAvailabilityHandlerOptions>(content) {
    override suspend fun process() {
        this._iterateMetrics({
            this._ensureCursor()
            this._intakeData()
            this._updateMetrics()
        })
        this._exportMetrics()
    }

    suspend fun _iterateMetrics(callback: suspend () -> Unit) {
        val metricsList = MetricsStatsHolder.cdns.data.values

        for (metrics in metricsList) {
            this._expose(FlowKey.METRICS, metrics)
            callback()
        }

        this._remove(FlowKey.METRICS, FlowKey.INDEX, FlowKey.DATA)
    }

    suspend fun _ensureCursor() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val cursor = this._content.cursors[metrics.id!!]
        if (cursor != null) {
            this._content.cursors[metrics.id!!] =
                TimeSeriesDataData(data = TimeSeriesData(0.0), value = 1.0)
        }

    }

    suspend fun _intakeData() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val cursor = this._content.cursors[metrics.id!!]!!
        val dataset = metrics.cdndownload!!.currentScore.dataset
        val data = TimeSeriesData(dataset[dataset.size - 1].value)
        this._expose(FlowKey.DATA, data)
    }

    suspend fun _updateMetrics() {
        val metrics = this._require(FlowKey.METRICS) as CDNMetrics
        val data = this._require(FlowKey.DATA) as TimeSeriesData<Double>
        val dataset = metrics.cdndownload!!.currentScore.dataset
        metrics.cdndownload!!.currentScore.last.dataset.add(data)
        this._content.cursors[metrics.id!!] =
            TimeSeriesDataData(data = dataset.last(), value = data.value)
    }

    suspend fun _exportMetrics() {
        ExportCDNDownloadLastCurrentScoreHandler().process()
    }

}

typealias PurgeCDNRecordsHandlerContent = CursorsTimeSeriesDataData
typealias PurgeCDNRecordsHandlerOptions = PurgeCDNRecordsHandlerContent

class PruneMetricsStatsHandler(content: PurgeCDNRecordsHandlerOptions) :
    AbstractFlow<PurgeCDNRecordsHandlerOptions>(content) {
    var _pruneTime = DateTool.now() - MetricsConstant.DURATION_OF_RETAINED_DATA * 1
    override suspend fun process() {
        this._updateCDNs()
        this._updateOrigin()
        this._updateSource()
    }

    suspend fun _updateCDNs() {

        for (metrics in MetricsStatsHolder.cdns.data.values) {
            this._pruneCDN(metrics)
            this._pruneDomain(metrics)
        }

    }

    suspend fun _updateOrigin() {
        val metrics = MetricsStatsHolder.origin
        this._pruneDomain(metrics)
    }

    suspend fun _updateSource() {
        val metrics = MetricsStatsHolder.source
        this._pruneSource(metrics)
    }

    suspend fun _pruneCDN(metrics: CDNMetrics) {
        this._pruneDataset(metrics.cdndownload!!.meanBandwidth)
        this._pruneDataset(metrics.cdndownload!!.meanBandwidth.last)
        this._pruneDataset(metrics.cdndownload!!.meanAvailability)
        this._pruneDataset(metrics.cdndownload!!.meanAvailability.last)
        this._pruneDataset(metrics.cdndownload!!.currentScore)
        this._pruneDataset(metrics.cdndownload!!.currentScore.last)
    }

    suspend fun _pruneDomain(metrics: DomainMetrics) {
        this._pruneDataset(metrics.download!!.traffic)
        this._pruneDataset(metrics.download!!.traffic.pulse)
        this._pruneDataset(metrics.download!!.traffic.cumulation)
        this._pruneDataset(metrics.download!!.bandwidth)
        this._pruneDataset(metrics.download!!.bandwidth.wma)
        this._pruneDataset(metrics.download!!.count.usage)
        this._pruneDataset(metrics.download!!.count.usage.pulse)
        this._pruneDataset(metrics.download!!.count.usage.cumulation)
        this._pruneDataset(metrics.download!!.count.success)
        this._pruneDataset(metrics.download!!.count.success.pulse)
        this._pruneDataset(metrics.download!!.count.success.cumulation)
        this._pruneDataset(metrics.download!!.count.failure)
        this._pruneDataset(metrics.download!!.count.failure.pulse)
        this._pruneDataset(metrics.download!!.count.failure.cumulation)
    }

    suspend fun _pruneSource(metrics: SourceMetrics) {
        metrics.http_download_records.clear()
    }

    suspend fun _pruneDataset(dataset: DataSet) {
        dataset.dataset.clear()
    }

}

object MetricsCollectorHolder {
    var instance: MetricsCollector? = null
    suspend fun emit(eventName: String, eventData: Any) {
        this.instance!!.emit(eventName, eventData)
    }
}
