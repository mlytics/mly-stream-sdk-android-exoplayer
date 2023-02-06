package com.mlytics.mlysdk.kernel.core.infra

class ReportProvider {
    suspend fun activate() {
        this._loadReportSubmitter()
    }

    suspend fun _loadReportSubmitter() {
        val reportSubmitter = ReportSubmitter()
        reportSubmitter.activate()
        ReportSubmitterHolder.instance = reportSubmitter
    }

    suspend fun deactivate() {
        this._unloadReportSubmitter()
    }

    suspend fun _unloadReportSubmitter() {
        val reportSubmitter = ReportSubmitterHolder.instance
        reportSubmitter?.deactivate()
        ReportSubmitterHolder.instance = null
    }

}
