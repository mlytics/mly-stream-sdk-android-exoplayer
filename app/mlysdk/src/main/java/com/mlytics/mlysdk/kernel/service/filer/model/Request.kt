package com.mlytics.mlysdk.kernel.service.filer.model

import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.util.SpecTask

class Request(
    var task: SpecTask, var resource: Resource
) {

    var isAborted: Boolean = false
        get() {
            return this.task.isAborted
        }

    suspend fun exit() {
        this.task.exit()
    }

    suspend fun done() {
        this.task.done()
        this.resource.content = this.task.data
        this.resource.type = this.task.type
        this.resource.total = this.task.data?.size
        this.resource.isComplete = true
    }

    suspend fun abort(error: Exception) {
        this.task.abort()
    }

}
