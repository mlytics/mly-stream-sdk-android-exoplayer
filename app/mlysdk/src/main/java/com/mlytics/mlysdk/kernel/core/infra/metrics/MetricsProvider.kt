package com.mlytics.mlysdk.kernel.core.infra.metrics

import com.mlytics.mlysdk.kernel.core.infra.metrics.state.MetricsStatsHolder
import com.mlytics.mlysdk.util.Component

class MetricsProvider : Component() {
    override suspend fun activate() {
        this._loadMetricsStats()
        this._loadMetricsCollector()
    }

    suspend fun _loadMetricsStats() {

    }

    suspend fun _loadMetricsCollector() {
        val metricsCollector = MetricsCollector()
        metricsCollector.activate()
        MetricsCollectorHolder.instance = metricsCollector
    }

    override suspend fun deactivate() {
        this._unloadMetricsCollector()
        this._unloadMetricsStats()
    }

    suspend fun _unloadMetricsCollector() {
        val metricsCollector = MetricsCollectorHolder.instance
        metricsCollector?.deactivate()
        MetricsCollectorHolder.instance = null
    }

    suspend fun _unloadMetricsStats() {
        val metricsStats = MetricsStatsHolder.instance
        metricsStats?.reset()
        MetricsStatsHolder.instance
    }

}
