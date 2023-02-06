package com.mlytics.mlysdk.kernel.core.infra.metrics

import com.mlytics.mlysdk.kernel.core.const.base.FlowKey
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNDownloadLastCurrentScoreExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNDownloadLastMeanAvailabilityExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNDownloadLastMeanBandwidthExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.CDNMetricsDownload
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadCumulativeTrafficExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadFailureCumulativeCountExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadFailurePulseCountExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadPulseTrafficExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadRecordExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadSuccessCumulativeCountExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadSuccessPulseCountExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadUsageCumulativeCountExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadUsagePulseCountExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.HTTPDownloadWMABandwidthExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.P2PDownloadRecordExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.model.P2SPSystemStateExport
import com.mlytics.mlysdk.kernel.core.infra.metrics.state.MetricsStatsHolder
import com.mlytics.mlysdk.kernel.core.infra.model.HTTPDownloadRecord
import com.mlytics.mlysdk.util.AbstractFlow
import com.mlytics.mlysdk.util.Emittery

public class MetricsExporter : Emittery() {

}

public object MetricsExporterEvent {
    val HTTP_DOWNLOAD_RECORD = "http_download_record"
    val HTTP_DOWNLOAD_PULSE_TRAFFIC = "http_download_pulse_traffic"
    val HTTP_DOWNLOAD_CUMULATIVE_TRAFFIC = "http_download_cumulative_traffic"
    val HTTP_DOWNLOAD_WMA_BANDWIDTH = "http_download_wma_bandwidth"
    val HTTP_DOWNLOAD_USAGE_PULSE_COUNT = "http_download_usage_pulse_count"
    val HTTP_DOWNLOAD_USAGE_CUMULATIVE_COUNT = "http_download_usage_cumulative_count"
    val HTTP_DOWNLOAD_SUCCESS_PULSE_COUNT = "http_download_success_pulse_count"
    val HTTP_DOWNLOAD_SUCCESS_CUMULATIVE_COUNT = "http_download_success_cumulative_count"
    val HTTP_DOWNLOAD_FAILURE_PULSE_COUNT = "http_download_failure_pulse_count"
    val HTTP_DOWNLOAD_FAILURE_CUMULATIVE_COUNT = "http_download_failure_cumulative_count"
    val CDN_DOWNLOAD_LAST_MEAN_BANDWIDTH = "cdn_download_last_mean_bandwidth"
    val CDN_DOWNLOAD_LAST_MEAN_AVAILABILITY = "cdn_download_last_mean_availability"
    val CDN_DOWNLOAD_LAST_CURRENT_SCORE = "cdn_download_last_current_score"
    val P2P_DOWNLOAD_RECORD = "p2p_download_record"
    val P2SP_SYSTEM_STATE = "p2sp_system_state"
    val SOURCE_PULSE_TRAFFIC = "source_pulse_traffic"
    val SOURCE_CUMULATIVE_TRAFFIC = "source_cumulative_traffic"
}

public object MetricsExporterHolder {
    val _instance = MetricsExporter()

    val instance: MetricsExporter
        get() {
            return _instance
        }

    fun isListened(eventName: String): Boolean {
        return _instance.listenerCount(eventName) > 0
    }

    public suspend fun emit(eventName: String, eventData: Any) {
        _instance.emit(eventName, eventData)
    }

}

public class ExportHTTPDownloadRecordHandler(content: HTTPDownloadRecord) :
    AbstractFlow<HTTPDownloadRecord>(content) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_RECORD)
    }

    public suspend fun _exportResult() {
        val result = this._content
        MetricsExporterHolder.emit(MetricsExporterEvent.HTTP_DOWNLOAD_RECORD, result)
    }

}

public data class ExportHTTPDownloadRecordContent(var record: HTTPDownloadRecordExport) {

}

typealias ExportHTTPDownloadRecordOptions = ExportHTTPDownloadRecordContent

public class ExportHTTPDownloadPulseTrafficHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_PULSE_TRAFFIC)
    }

    public suspend fun _intakeResult() {
        val result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadPulseTrafficExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.traffic.pulse.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result = this._require(FlowKey.RESULT) as MutableList<HTTPDownloadPulseTrafficExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadPulseTrafficExport(
                type = origin.type,
                dataset = origin.download!!.traffic.pulse.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        val result = this._require(FlowKey.RESULT) as MutableList<HTTPDownloadPulseTrafficExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.HTTP_DOWNLOAD_PULSE_TRAFFIC, result)
    }

}

public class ExportHTTPDownloadCumulativeTrafficHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {

        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_CUMULATIVE_TRAFFIC)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadCumulativeTrafficExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.traffic.cumulation.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadCumulativeTrafficExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadCumulativeTrafficExport(
                type = origin.type, dataset = origin.download!!.traffic.cumulation.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadCumulativeTrafficExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.HTTP_DOWNLOAD_CUMULATIVE_TRAFFIC, result)
    }

}

public class ExportHTTPDownloadWMABandwidthHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_WMA_BANDWIDTH)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadWMABandwidthExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.bandwidth.wma.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result = this._require(FlowKey.RESULT) as MutableList<HTTPDownloadWMABandwidthExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadWMABandwidthExport(
                type = origin.type, dataset = origin.download!!.bandwidth.wma.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        var result = this._require(FlowKey.RESULT) as MutableList<HTTPDownloadWMABandwidthExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.HTTP_DOWNLOAD_WMA_BANDWIDTH, result)
    }

}

public class ExportHTTPDownloadUsagePulseCountHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_USAGE_PULSE_COUNT)
    }

    public suspend fun _intakeResult() {
        val result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadUsagePulseCountExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.count.usage.pulse.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result = this._require(FlowKey.RESULT) as MutableList<HTTPDownloadUsagePulseCountExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadUsagePulseCountExport(
                type = origin.type,
                dataset = origin.download!!.count.usage.pulse.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        val result = this._require(FlowKey.RESULT) as MutableList<HTTPDownloadUsagePulseCountExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.HTTP_DOWNLOAD_USAGE_PULSE_COUNT, result)
    }

}

public class ExportHTTPDownloadUsageCumulativeCountHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_USAGE_CUMULATIVE_COUNT)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadUsageCumulativeCountExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.count.usage.cumulation.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadUsageCumulativeCountExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadUsageCumulativeCountExport(
                type = origin.type,
                dataset = origin.download!!.count.usage.cumulation.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadUsageCumulativeCountExport>
        MetricsExporterHolder.emit(
            MetricsExporterEvent.HTTP_DOWNLOAD_USAGE_CUMULATIVE_COUNT,
            result
        )
    }

}

public class ExportHTTPDownloadSuccessPulseCountHandler : AbstractFlow<Unit>(content = Unit) {
    override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_SUCCESS_PULSE_COUNT)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadSuccessPulseCountExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.count.success.pulse.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadSuccessPulseCountExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadSuccessPulseCountExport(
                type = origin.type,
                dataset = origin.download!!.count.success.pulse.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadSuccessPulseCountExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.HTTP_DOWNLOAD_SUCCESS_PULSE_COUNT, result)
    }

}

public class ExportHTTPDownloadSuccessCumulativeCountHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_SUCCESS_CUMULATIVE_COUNT)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadSuccessCumulativeCountExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.count.success.cumulation.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadSuccessCumulativeCountExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadSuccessCumulativeCountExport(
                type = origin.type,
                dataset = origin.download!!.count.success.cumulation.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadSuccessCumulativeCountExport>
        MetricsExporterHolder.emit(
            MetricsExporterEvent.HTTP_DOWNLOAD_SUCCESS_CUMULATIVE_COUNT,
            result
        )
    }

}

public class ExportHTTPDownloadFailurePulseCountHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_FAILURE_PULSE_COUNT)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadFailurePulseCountExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.count.failure.pulse.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadFailurePulseCountExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadFailurePulseCountExport(
                type = origin.type,
                dataset = origin.download!!.count.failure.pulse.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadFailurePulseCountExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.HTTP_DOWNLOAD_FAILURE_PULSE_COUNT, result)
    }

}

public class ExportHTTPDownloadFailureCumulativeCountHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._injectOrigin()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.HTTP_DOWNLOAD_FAILURE_CUMULATIVE_COUNT)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            HTTPDownloadFailureCumulativeCountExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = cdn.download!!.count.failure.cumulation.dataset
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _injectOrigin() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadFailureCumulativeCountExport>
        val origin = MetricsStatsHolder.origin
        result.add(
            HTTPDownloadFailureCumulativeCountExport(
                type = origin.type,
                dataset = origin.download!!.count.failure.cumulation.dataset
            )
        )
    }

    public suspend fun _exportResult() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<HTTPDownloadFailureCumulativeCountExport>
        MetricsExporterHolder.emit(
            MetricsExporterEvent.HTTP_DOWNLOAD_FAILURE_CUMULATIVE_COUNT,
            result
        )
    }

}

public class ExportCDNDownloadLastMeanBandwidthHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.CDN_DOWNLOAD_LAST_MEAN_BANDWIDTH)
    }

    public suspend fun _intakeResult() {
        var result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            CDNDownloadLastMeanBandwidthExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = (cdn.download as? CDNMetricsDownload)?.meanBandwidth?.last?.dataset!!
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _exportResult() {
        var result =
            this._require(FlowKey.RESULT) as MutableList<CDNDownloadLastMeanBandwidthExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.CDN_DOWNLOAD_LAST_MEAN_BANDWIDTH, result)
    }

}

public class ExportCDNDownloadLastMeanAvailabilityHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.CDN_DOWNLOAD_LAST_MEAN_AVAILABILITY)
    }

    public suspend fun _intakeResult() {
        val result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            CDNDownloadLastMeanAvailabilityExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = (cdn.download as? CDNMetricsDownload)?.meanBandwidth?.last?.dataset!!
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _exportResult() {
        val result =
            this._require(FlowKey.RESULT) as MutableList<CDNDownloadLastMeanAvailabilityExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.CDN_DOWNLOAD_LAST_MEAN_AVAILABILITY, result)
    }

}

public class ExportCDNDownloadLastCurrentScoreHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.CDN_DOWNLOAD_LAST_CURRENT_SCORE)
    }

    public suspend fun _intakeResult() {
        val result = MetricsStatsHolder.cdns.data.values.map { cdn ->
            CDNDownloadLastCurrentScoreExport(
                id = cdn.id,
                name = cdn.name,
                type = cdn.type,
                domain = cdn.domain,
                isEnabled = cdn.isEnabled,
                dataset = (cdn.download as? CDNMetricsDownload)?.meanBandwidth?.last?.dataset!!
            )
        }

        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _exportResult() {
        val result = this._require(FlowKey.RESULT) as MutableList<CDNDownloadLastCurrentScoreExport>
        MetricsExporterHolder.emit(MetricsExporterEvent.CDN_DOWNLOAD_LAST_CURRENT_SCORE, result)
    }

}

public class ExportP2PDownloadRecordHandler(content: ExportP2PDownloadRecordContent) :
    AbstractFlow<ExportP2PDownloadRecordContent>(
        content
    ) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.P2P_DOWNLOAD_RECORD)
    }

    public suspend fun _exportResult() {
        var result = this._content.record
        MetricsExporterHolder.emit(MetricsExporterEvent.P2P_DOWNLOAD_RECORD, result)
    }

}

public data class ExportP2PDownloadRecordContent(var record: P2PDownloadRecordExport) {

}

typealias ExportP2PDownloadRecordOptions = ExportP2PDownloadRecordContent

public class ExportP2SPSystemStateHandler : AbstractFlow<Unit>(content = Unit) {
    public override suspend fun process() {
        if (this._shouldExport()) {
            this._intakeResult()
            this._exportResult()
        }

    }

    public fun _shouldExport(): Boolean {
        return MetricsExporterHolder.isListened(MetricsExporterEvent.P2SP_SYSTEM_STATE)
    }

    public suspend fun _intakeResult() {
        val tracker = MetricsStatsHolder.tracker
        val node = MetricsStatsHolder.node
        val swarms = MetricsStatsHolder.swarms
        var result = P2SPSystemStateExport(tracker = tracker, node = node, swarms = swarms)
        this._expose(FlowKey.RESULT, result)
    }

    public suspend fun _exportResult() {
        var result = this._require(FlowKey.RESULT) as P2SPSystemStateExport
        MetricsExporterHolder.emit(MetricsExporterEvent.P2SP_SYSTEM_STATE, result)
    }

}
