package com.mlytics.mlysdk.kernel.core.infra.metrics.model

import com.mlytics.mlysdk.kernel.core.infra.model.HTTPDownloadRecord
import com.mlytics.mlysdk.kernel.core.infra.model.P2PDownloadRecord
import com.mlytics.mlysdk.util.DateTool
import com.mlytics.mlysdk.util.ObjectLike

class DomainMetricsCount {
    var usage = DomainMetricsUsage()
    var success = DomainMetricsUsage()
    var failure = DomainMetricsUsage()
}

open class DomainMetricsDownload {
    var count = DomainMetricsCount()
    var outcome = DomainMetricsUsage()
    var traffic = DomainMetricsUsage()
    var bandwidth = DomainMetricsBandwidth()
}

class DomainMetricsUsage : DataSet() {
    var pulse = DataSet()
    var cumulation = DataSet()
}

open class DataSet {
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()
}

class DataSetLast : DataSet() {
    var last = DataSet()
}

class DomainMetricsBandwidth : DataSet() {
    var wma = DataSet()
}

open class DomainMetrics {
    var id: String? = null
    var type: String? = null
    var name: String? = null
    var domain: String? = null
    var isEnabled: Boolean? = null
    var download: DomainMetricsDownload? = null
    var cdndownload: CDNMetricsDownload?
        get() {
            return download as? CDNMetricsDownload
        }
        set(value) {
            download = value
        }
}

class CDNMetricsDownload : DomainMetricsDownload() {
    var meanBandwidth = DataSetLast()
    var meanAvailability = DataSetLast()
    var currentScore = DataSetLast()
}

typealias CDNMetrics = DomainMetrics

typealias OriginMetrics = DomainMetrics

class TrackerMetrics {
    var peerID: String? = null
    var isAvailable: Boolean? = null
}

class NodeMetrics {
    var peerID: String? = null
    var isAvailable: Boolean? = null
}

data class SwarmMetrics(
    var swarmID: String? = null,
    var isAvailable: Boolean? = null,
    var users: ObjectLike<UserMetrics> = ObjectLike()
)

data class UserMetrics(
    var peerID: String?, var isAvailable: Boolean?
)

class SourceMetrics {
    var http_download_records: MutableList<HTTPDownloadRecord> = mutableListOf()
    var p2p_download_records: MutableList<P2PDownloadRecord> = mutableListOf()
}

data class TimeSeriesData<T>(
    var value: T? = null, var ctime: Double = DateTool.now()
)