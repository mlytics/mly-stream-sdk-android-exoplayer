package com.mlytics.mlysdk.kernel.core.infra.metrics.model

import com.mlytics.mlysdk.kernel.core.infra.model.HTTPDownloadRecord
import com.mlytics.mlysdk.kernel.core.infra.model.P2PDownloadRecord
import com.mlytics.mlysdk.util.ObjectLike

typealias HTTPDownloadRecordExport = HTTPDownloadRecord

data class HTTPDownloadPulseTrafficExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()
)

data class HTTPDownloadCumulativeTrafficExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()
)

data class HTTPDownloadWMABandwidthExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()

)

data class HTTPDownloadUsagePulseCountExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()

)

data class HTTPDownloadUsageCumulativeCountExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()
)

data class HTTPDownloadSuccessPulseCountExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()

)

data class HTTPDownloadSuccessCumulativeCountExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()
)

class HTTPDownloadFailurePulseCountExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()

)

data class HTTPDownloadFailureCumulativeCountExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()

)

data class CDNDownloadLastMeanBandwidthExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()
)

data class CDNDownloadLastMeanAvailabilityExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>>? = mutableListOf()
)

data class CDNDownloadLastCurrentScoreExport(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var domain: String? = null,
    var isEnabled: Boolean? = null,
    var dataset: MutableList<TimeSeriesData<Double>> = mutableListOf()
)

typealias P2PDownloadRecordExport = P2PDownloadRecord

data class P2SPSystemStateExport(
    var tracker: TrackerMetrics,
    var node: NodeMetrics,
    var swarms: ObjectLike<SwarmMetrics> = ObjectLike()
)
