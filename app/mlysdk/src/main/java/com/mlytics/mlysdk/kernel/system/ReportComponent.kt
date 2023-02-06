package com.mlytics.mlysdk.kernel.system

import com.mlytics.mlysdk.kernel.core.infra.ReportProvider
import com.mlytics.mlysdk.util.Component

class ReportComponent : Component() {
    var _provider: ReportProvider = ReportProvider()
    
    suspend fun _create() {
        this._provider.activate()
    }

    suspend fun _destroy() {
        this._provider.deactivate()
    }

}
