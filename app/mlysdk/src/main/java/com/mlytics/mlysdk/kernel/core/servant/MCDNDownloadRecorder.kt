package com.mlytics.mlysdk.kernel.core.servant

//import com.mlytics.mlysdk.kernel.core.infra.metrics.MetricsCollectorEvent
//import com.mlytics.mlysdk.kernel.core.infra.metrics.MetricsCollectorHolder
//import com.mlytics.mlysdk.kernel.core.infra.metrics.model.MetricsConstant
//import com.mlytics.mlysdk.kernel.core.infra.metrics.state.MetricsStatsHolder
//import com.mlytics.mlysdk.kernel.core.infra.model.CDNDownloadRecord
//import com.mlytics.mlysdk.kernel.core.infra.model.HTTPDownloadRecord
import com.mlytics.mlysdk.kernel.core.const.base.FlowKey
import com.mlytics.mlysdk.kernel.core.const.service.DomainType
import com.mlytics.mlysdk.kernel.core.infra.model.HTTPDownloadRecord
import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.core.service.filter.PerformanceResourceTiming
import com.mlytics.mlysdk.util.AbortController
import com.mlytics.mlysdk.util.AbstractFlow
import com.mlytics.mlysdk.util.SpecTask

//import Foundation
class MCDNDownloadRecorder(content: MCDNDownloadRecorderContent) :
    AbstractFlow<MCDNDownloadRecorderContent>(content) {
    var _cdn: CDN? = null

    override suspend fun process() {
        this._intakeHTTPDownloadRecord()
        this._forwardHTTPDownloadRecord()
        if (this._shouldUpdateCDNStats()) {
            this._intakeCDNStats()
            this._updateCDNBandwidth()
            this._updateCDNMeanBandwidth()
            this._updateCDNMeanAvailability()
            this._updateCDNCurrentScore()
            this._forwardCDNDownloadRecord()
        }

    }

    suspend fun _shouldUpdateCDNStats(): Boolean {
        val record = this._require(FlowKey.RECORD) as? HTTPDownloadRecord
        return record?.type == DomainType.CDN.rawValue
    }

    suspend fun _intakeHTTPDownloadRecord() {
        val record = HTTPDownloadRecordBuilder(this._content).build()
        this._expose(FlowKey.RECORD, record)
    }

    suspend fun _forwardHTTPDownloadRecord() {
        val record = this._require(FlowKey.RECORD) as? HTTPDownloadRecord
// MetricsCollectorHolder.emit(MetricsCollectorEvent.HTTP_DOWNLOAD_RECORD, record)
    }

    suspend fun _intakeCDNStats() {
        val record = this._require(FlowKey.RECORD) as HTTPDownloadRecord
        this._cdn = MCDNStatsHolder.cdns[record.id!!]
    }

    suspend fun _updateCDNBandwidth() {
        val record = this._require(FlowKey.RECORD) as HTTPDownloadRecord
        val bandwidth = record.bandwidth
        if (bandwidth != null) else {
            return
        }

        MCDNStatsHolder.networkBandwidth = Math.max(
            MCDNConstant.QUALIFIED_DOWNLOAD_BANDWIDTH,
            (MCDNStatsHolder.networkBandwidth + bandwidth) / 2
        )
    }

    suspend fun _updateCDNMeanBandwidth() {
        val record = this._require(FlowKey.RECORD) as HTTPDownloadRecord
        if (record.bandwidth == null) {
            return
        }

        MeanBandwidthCalculator(MeanBandwidthCalculatorContent(this._cdn!!)).process()
    }

    suspend fun _updateCDNMeanAvailability() {
        MeanAvailabilityCalculator(MeanAvailabilityCalculatorContent(this._cdn!!)).process()
    }

    suspend fun _updateCDNCurrentScore() {
        CurrentScoreCalculator(CurrentScoreCalculatorContent(this._cdn!!)).process()
    }

    suspend fun _forwardCDNDownloadRecord() {
//        MetricsCollectorHolder.emit(
//            MetricsCollectorEvent.CDN_DOWNLOAD_RECORD, CDNDownloadRecord(
//                DateTool.now(),
//                id = this._cdn!!.id,
//                name = this._cdn!!.name,
//                type = this._cdn!!.type,
//                domain = this._cdn!!.domain,
//                meanBandwidth = this._cdn!!.meanBandwidth,
//                meanAvailability = this._cdn!!.meanAvailability,
//                currentScore = this._cdn!!.currentScore
//            )
//        )
    }

    object suspend

    suspend fun process(options: MCDNDownloadRecorderOptions) {
        MCDNDownloadRecorder(options).process()
    }

}

typealias MCDNDownloadRecorderContent = HTTPDownloadRecordBuilderOptions
typealias MCDNDownloadRecorderOptions = MCDNDownloadRecorderContent

class HTTPDownloadRecordBuilder {
    var _content: HTTPDownloadRecord? = null
    var _options: HTTPDownloadRecordBuilderOptions? = null

    constructor (_options: HTTPDownloadRecordBuilderOptions) {
        this._options = _options
        this._initialize()
    }

    fun build(): HTTPDownloadRecord {
        return this._content!!
    }

    fun _initialize() {
        this._setContent()
        this._setContentSize()
        this._setElapsedTime()
        this._setIsOutlier()
        this._setBandwidth()
    }

    fun _setContent() {
        val options = this._options!!
//        this._content = HTTPDownloadRecord(
//            ctime = DateTool.now(),
//            id = options.source?.id,
//            name = options.source?.name,
//            type = options.source?.type,
//            domain = options.source?.domain,
//            totalSize = options.resource?.total,
//            contentType = options.resource?.type,
//            startTime = options.startTime,
//            isAborted = options.aborter?.task?.isAborted,
//            isSuccess = options.error == null,
//            isComplete = options.resource?.isComplete,
//            swarmID = options.resource?.swarmID,
//            swarmURI = options.resource?.swarmURI,
//            sourceURI = options.resource?.sourceURI,
//            requestURI = options.requestURI,
//            responseCode = options.payload?.state,
//            errorMessage = options.error?.message,
//            algorithmID = MCDNStatsHolder.algorithmID,
//            algorithmVersion = MCDNStatsHolder.algorithmVersion
//        )
    }

    fun _setContentSize() {
        val options = this._options!!
        this._content?.contentSize = options.resource!!.size - options.startSize!!
    }

    fun _setElapsedTime() {
        val measurement = this._options!!.measurement!!
        if (this._content!!.isSuccess!!) {
            return
        }

        this._content!!.elapsedTime = measurement.elapsedTimeS().toDouble().div(1000)
    }

    fun _setIsOutlier() {
        this._content!!.isOutlier =
            this._content!!.contentSize!! < MCDNConstant.MIN_SIZE_FOR_BANDWIDTH_MEASUREMENT
    }

    fun _setBandwidth() {
        if (this._content!!.isSuccess!! || this._content!!.isOutlier!!) {
            return
        }

        this._content!!.bandwidth =
            this._content!!.contentSize!!.toDouble() / this._content!!.elapsedTime!!
    }

}

data class HTTPDownloadRecordBuilderOptions(
    var source: CDN? = null,
    var resource: Resource? = null,
    var payload: SpecTask? = null,
    var error: Error? = null,
    var aborter: AbortController? = null,
    var requestURI: String? = null,
    var startSize: Int? = null,
    var startTime: Double? = null,
    var measurement: PerformanceResourceTiming? = null,
)

class MeanBandwidthCalculator(var options: MeanBandwidthCalculatorContent) :
    AbstractFlow<MeanBandwidthCalculatorContent>(options) {
    var _cdn: CDN

    init {
        _cdn = options.cdn
    }

    override suspend fun process() {
        this._intakeResult()
        this._updateCDN()
    }

    suspend fun _intakeResult() {
//        val metrics = MetricsStatsHolder.instance.cdns()[this._cdn.id!!]
//        val dataset = metrics!!.download!!.bandwidth.dataset
//        var baseline = DateTool.now() - MetricsConstant.DURATION_OF_RETAINED_DATA
//        if (dataset!!.count() > 0) {
//            baseline = Math.min(dataset[0].ctime!!, baseline)
//        }
//
//        val result = MathCalculator.weightedAverage(dataset.map { data ->
//            WeightedData(data.ctime!! - baseline, data.value!!)
//        })
//        this._expose(FlowKey.RESULT, result)
    }

    suspend fun _updateCDN() {
        val result = this._require(FlowKey.RESULT) as Double
        this._cdn.meanBandwidth = result
    }

    object suspend

    suspend fun process(options: MeanBandwidthCalculatorContent) {
        MeanBandwidthCalculator(options).process()
    }

}

data class MeanBandwidthCalculatorContent(var cdn: CDN)

class MeanAvailabilityCalculator(content: MeanAvailabilityCalculatorContent) :
    AbstractFlow<MeanAvailabilityCalculatorContent>(content) {

    override suspend fun process() {
        this._intakeResult()
        this._updateCDN()
    }

    suspend fun _intakeResult() {
//        val metrics = MetricsStatsHolder.instance.cdns()!![this._content._cdn.id!!]
//        if (metrics != null) else {
//            return
//        }
//
//        val dataset = metrics.download!!.outcome.dataset
//        var baseline = DateTool.now() - MetricsConstant.DURATION_OF_RETAINED_DATA
//        if (dataset.size > 0) {
//            baseline = Math.min(dataset[0].ctime!!, baseline)
//        }
//
//        val result = MathCalculator.weightedAverage(dataset.map { data ->
//            WeightedData(data.ctime!! - baseline, data.value!!)
//        })
//        this._expose(FlowKey.RESULT, result)
    }

    suspend fun _updateCDN() {
        val result = this._require(FlowKey.RESULT) as Double
        if (result != null) else {
            return
        }

        this._content._cdn.meanAvailability = result
    }

}

data class MeanAvailabilityCalculatorContent(var _cdn: CDN)

class CurrentScoreCalculator : AbstractFlow<CurrentScoreCalculatorContent> {
    var _cdn: CDN
    var _scoreParts: FeedbackScoreParts

    constructor (content: CurrentScoreCalculatorContent) : super(content) {
        this._cdn = content.cdn
        this._scoreParts = FeedbackScoreParts()
    }

    override suspend fun process() {
        this._scoreBandwidth()
        this._scoreAvailability()
        this._updateCDN()
    }

    suspend fun _scoreBandwidth() {
        val bandwidthScore = this._cdn.meanBandwidth / MCDNStatsHolder.networkBandwidth
        this._scoreParts.bandwidth = Math.min(bandwidthScore, 1.0)
    }

    suspend fun _scoreAvailability() {
        val availabilityScore =
            Math.pow(this._cdn.meanAvailability, MCDNConstant.PENALTY_POWER_OF_AVAILABILITY)
        this._scoreParts.availability = availabilityScore
    }

    suspend fun _updateCDN() {
        val currentScore = this._cdn.currentScore
        val feedbackScore = this._scoreParts.bandwidth * this._scoreParts.availability
        this._cdn.currentScore = (currentScore + feedbackScore) / 2
    }

}

data class CurrentScoreCalculatorContent(var cdn: CDN)

data class FeedbackScoreParts(
    var bandwidth: Double = 0.0, var availability: Double = 0.0
)
