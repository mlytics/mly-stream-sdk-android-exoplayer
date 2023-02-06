package com.mlytics.mlysdk.kernel.core.infra.metrics

import com.mlytics.mlysdk.util.ChannelHub
import com.mlytics.mlysdk.util.Emittery
import com.mlytics.mlysdk.util.TaskManager

class MetricsCollector : Emittery() {
    var _taskManager = TaskManager<Any>()
    var _recordHub = ChannelHub()

    companion object {
        var instance: MetricsCollector = MetricsCollector()
    }

    suspend fun activate() {
        this._taskManager.activate()
        this._openRecordHub()
        this._bindEvents()
        this._buildTasks()
    }

    suspend fun deactivate() {
        this._taskManager.deactivate()
        this._recordHub.close()
        this.clearListeners()
    }

    suspend fun _openRecordHub() {
        this._recordHub.connect(MetricsCollectorEvent.HTTP_DOWNLOAD_RECORD)
        this._recordHub.connect(MetricsCollectorEvent.CDN_CONFIG_RECORDS)
        this._recordHub.connect(MetricsCollectorEvent.CDN_DOWNLOAD_RECORD)
        this._recordHub.connect(MetricsCollectorEvent.P2P_DOWNLOAD_RECORD)
        this._recordHub.connect(MetricsCollectorEvent.TRACKER_STATE_RECORD)
        this._recordHub.connect(MetricsCollectorEvent.NODE_STATE_RECORD)
        this._recordHub.connect(MetricsCollectorEvent.SWARM_STATE_RECORD)
        this._recordHub.connect(MetricsCollectorEvent.USER_STATE_RECORD)
        this._recordHub.connect(MetricsCollectorEvent.PURGE_CDN_RECORDS)
    }

    suspend fun _bindEvents() {
        this.on(MetricsCollectorEvent.HTTP_DOWNLOAD_RECORD) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.HTTP_DOWNLOAD_RECORD, record)
        }

        this.on(MetricsCollectorEvent.CDN_CONFIG_RECORDS) { records ->
            this._recordHub.deliver(MetricsCollectorEvent.CDN_CONFIG_RECORDS, records)
        }

        this.on(MetricsCollectorEvent.CDN_DOWNLOAD_RECORD) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.CDN_DOWNLOAD_RECORD, record)
        }

        this.on(MetricsCollectorEvent.P2P_DOWNLOAD_RECORD) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.P2P_DOWNLOAD_RECORD, record)
        }

        this.on(MetricsCollectorEvent.TRACKER_STATE_RECORD) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.TRACKER_STATE_RECORD, record)
        }

        this.on(MetricsCollectorEvent.NODE_STATE_RECORD) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.NODE_STATE_RECORD, record)
        }

        this.on(MetricsCollectorEvent.SWARM_STATE_RECORD) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.SWARM_STATE_RECORD, record)
        }

        this.on(MetricsCollectorEvent.USER_STATE_RECORD) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.USER_STATE_RECORD, record)
        }

        this.on(MetricsCollectorEvent.PURGE_CDN_RECORDS) { record ->
            this._recordHub.deliver(MetricsCollectorEvent.PURGE_CDN_RECORDS, record)
        }

    }

    suspend fun _buildTasks() {
        this._buildHandleHTTPDownloadRecordTask()
        this._buildHandleHTTPDownloadPulseTrafficTask()
        this._buildHandleHTTPDownloadCumulativeTrafficTask()
        this._buildHandleHTTPDownloadWMABandwidthTask()
        this._buildHandleHTTPDownloadUsagePulseCountTask()
        this._buildHandleHTTPDownloadUsageCumulativeCountTask()
        this._buildHandleHTTPDownloadSuccessPulseCountTask()
        this._buildHandleHTTPDownloadSuccessCumulativeCountTask()
        this._buildHandleHTTPDownloadFailurePulseCountTask()
        this._buildHandleHTTPDownloadFailureCumulativeCountTask()
        this._buildHandleCDNConfigRecordsTask()
        this._buildHandleCDNDownloadRecordTask()
        this._buildHandleCDNDownloadLastMeanBandwidthTask()
        this._buildHandleCDNDownloadLastMeanAvailabilityTask()
        this._buildHandleCDNDownloadLastCurrentScoreTask()
        this._buildHandleP2PDownloadRecordTask()
        this._buildHandleTrackerStateRecordTask()
        this._buildHandleNodeStateRecordTask()
        this._buildHandleSwarmStateRecordTask()
        this._buildHandleUserStateRecordTask()
        this._buildHandlePurgeCDNRecordsTask()
        this._buildHandlePruneMetricsStatsTask()
    }

    suspend fun _buildHandleHTTPDownloadRecordTask() {
//         this._taskManager.createCyclicTask(
//            TaskState( MetricsCollectorTaskName.HANDLE_HTTP_DOWNLOAD_RECORD,
//             HTTPDownloadRecordHandler(),
//            sleepSeconds= 1, maxErrorRetry=-1, maxTotalRetry=-1))
    }

    suspend fun _buildHandleHTTPDownloadPulseTrafficTask() {
//         this._taskManager.createCyclicTask(
//            TaskState( MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_PULSE_TRAFFIC,
//             VoidFlow
//                 (HTTPDownloadPulseTrafficHandler()
//        ), sleepFirst= true, sleepSeconds= 3, sleepJitter= 0, maxErrorRetry=-1, maxTotalRetry=-1))
    }

    suspend fun _buildHandleHTTPDownloadCumulativeTrafficTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_CUMULATIVE_TRAFFIC,
//            flow: VoidFlow
//            (HTTPDownloadCumulativeTrafficHandler(
//                content: HTTPDownloadCumulativeTrafficHandlerOptions
//                ()
//            )),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleHTTPDownloadWMABandwidthTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_WMA_BANDWIDTH,
//            flow: VoidFlow
//            (HTTPDownloadWMABandwidthHandler(content: HTTPDownloadWMABandwidthHandlerOptions())),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleHTTPDownloadUsagePulseCountTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_USAGE_PULSE_COUNT,
//            flow: VoidFlow
//            (HTTPDownloadUsagePulseCountHandler(
//                content: HTTPDownloadUsagePulseCountHandlerOptions
//                ()
//            )),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleHTTPDownloadUsageCumulativeCountTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_USAGE_CUMULATIVE_COUNT,
//            flow: VoidFlow
//            (HTTPDownloadUsageCumulativeCountHandler(
//                content: HTTPDownloadUsageCumulativeCountHandlerOptions
//                ()
//            )),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleHTTPDownloadSuccessPulseCountTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_SUCCESS_PULSE_COUNT,
//            flow: VoidFlow
//            (HTTPDownloadSuccessPulseCountHandler(
//                content: HTTPDownloadSuccessPulseCountHandlerOptions
//                ()
//            )),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleHTTPDownloadSuccessCumulativeCountTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_SUCCESS_CUMULATIVE_COUNT,
//            flow: VoidFlow
//            (HTTPDownloadSuccessCumulativeCountHandler(content: CursorsTimeSeriesDataData())),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleHTTPDownloadFailurePulseCountTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_FAILURE_PULSE_COUNT,
//            flow: VoidFlow
//            (HTTPDownloadFailurePulseCountHandler(content: CursorsTimeSeriesDataData())),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleHTTPDownloadFailureCumulativeCountTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_HTTP_DOWNLOAD_FAILURE_CUMULATIVE_COUNT,
//            flow: VoidFlow
//            (HTTPDownloadFailureCumulativeCountHandler(content: CursorsTimeSeriesDataData())),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleCDNConfigRecordsTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_CDN_CONFIG_RECORDS,
//            flow: VoidFlow
//            (CDNConfigRecordsHandler(content:())
//        ), sleepSeconds: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleCDNDownloadRecordTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_CDN_DOWNLOAD_RECORD,
//            flow: VoidFlow
//            (CDNDownloadRecordHandler(content:())
//        ), sleepSeconds: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleCDNDownloadLastMeanBandwidthTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_CDN_DOWNLOAD_LAST_MEAN_BANDWIDTH,
//            flow: VoidFlow
//            (CDNDownloadLastMeanBandwidthHandler(content: CursorsTimeSeriesDataData())),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleCDNDownloadLastMeanAvailabilityTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_CDN_DOWNLOAD_LAST_MEAN_AVAILABILITY,
//            flow: VoidFlow
//            (CDNDownloadLastMeanAvailabilityHandler(content: CursorsTimeSeriesDataData())),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleCDNDownloadLastCurrentScoreTask() {
//         this._taskManager.createCyclicTask(
//            TaskState(name: MetricsCollectorTaskName. HANDLE_CDN_DOWNLOAD_LAST_CURRENT_SCORE,
//            flow: VoidFlow
//            (CDNDownloadLastCurrentScoreHandler(
//                content: CDNDownloadLastMeanAvailabilityHandlerOptions
//                ()
//            )),
//            sleepFirst: true, sleepSeconds: 3, sleepJitter: 0, maxErrorRetry:-1, maxTotalRetry:-1))
    }

    suspend fun _buildHandleP2PDownloadRecordTask() {

    }

    suspend fun _buildHandleTrackerStateRecordTask() {

    }

    suspend fun _buildHandleNodeStateRecordTask() {

    }

    suspend fun _buildHandleSwarmStateRecordTask() {

    }

    suspend fun _buildHandleUserStateRecordTask() {

    }

    suspend fun _buildHandlePurgeCDNRecordsTask() {

    }

    suspend fun _buildHandlePruneMetricsStatsTask() {

    }

}

object MetricsCollectorEvent {
    val HTTP_DOWNLOAD_RECORD = "http_download_record"
    val CDN_CONFIG_RECORDS = "cdn_config_records"
    val CDN_DOWNLOAD_RECORD = "cdn_download_record"
    val P2P_DOWNLOAD_RECORD = "p2p_download_record"
    val TRACKER_STATE_RECORD = "tracker_state_record"
    val NODE_STATE_RECORD = "node_state_record"
    val SWARM_STATE_RECORD = "swarm_state_record"
    val USER_STATE_RECORD = "user_state_record"
    val PURGE_CDN_RECORDS = "purge_cdn_records"
}

object MetricsCollectorTaskName {
    val HANDLE_HTTP_DOWNLOAD_RECORD = "metrics collector: handle http download record"
    val HANDLE_HTTP_DOWNLOAD_PULSE_TRAFFIC = "metrics collector: handle http download pulse traffic"
    val HANDLE_HTTP_DOWNLOAD_CUMULATIVE_TRAFFIC =
        "metrics collector: handle http download cumulative traffic"
    val HANDLE_HTTP_DOWNLOAD_WMA_BANDWIDTH = "metrics collector: handle http download wma bandwidth"
    val HANDLE_HTTP_DOWNLOAD_USAGE_PULSE_COUNT =
        "metrics collector: handle http download usage pulse count"
    val HANDLE_HTTP_DOWNLOAD_USAGE_CUMULATIVE_COUNT =
        "metrics collector: handle http download usage cumulative count"
    val HANDLE_HTTP_DOWNLOAD_SUCCESS_PULSE_COUNT =
        "metrics collector: handle http download success pulse count"
    val HANDLE_HTTP_DOWNLOAD_SUCCESS_CUMULATIVE_COUNT =
        "metrics collector: handle http download success cumulative count"
    val HANDLE_HTTP_DOWNLOAD_FAILURE_PULSE_COUNT =
        "metrics collector: handle http download failure pulse count"
    val HANDLE_HTTP_DOWNLOAD_FAILURE_CUMULATIVE_COUNT =
        "metrics collector: handle http download failure cumulative count"
    val HANDLE_CDN_CONFIG_RECORDS = "metrics collector: handle cdn config records"
    val HANDLE_CDN_DOWNLOAD_RECORD = "metrics collector: handle cdn download record"
    val HANDLE_CDN_DOWNLOAD_LAST_MEAN_BANDWIDTH =
        "metrics collector: handle cdn download last mean bandwidth"
    val HANDLE_CDN_DOWNLOAD_LAST_MEAN_AVAILABILITY =
        "metrics collector: handle cdn download last mean availability"
    val HANDLE_CDN_DOWNLOAD_LAST_CURRENT_SCORE =
        "metrics collector: handle cdn download last current score"
    val HANDLE_P2P_DOWNLOAD_RECORD = "metrics collector: handle p2p download record"
    val HANDLE_TRACKER_STATE_RECORD = "metrics collector: handle tracker state record"
    val HANDLE_NODE_STATE_RECORD = "metrics collector: handle node state record"
    val HANDLE_SWARM_STATE_RECORD = "metrics collector: handle swarm state record"
    val HANDLE_USER_STATE_RECORD = "metrics collector: handle user state record"
    val HANDLE_PURGE_CDN_RECORDS = "metrics collector: handle purge cdn records"
    val HANDLE_PRUNE_METRICS_STATS = "metrics collector: handle prune metrics stats"
}
