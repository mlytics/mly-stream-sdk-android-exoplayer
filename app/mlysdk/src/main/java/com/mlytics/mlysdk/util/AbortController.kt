package com.mlytics.mlysdk.util

class AbortController {
    var task: Cancelable? = null
    fun abort() {
        task?.cancel()
    }

    var isAborted: Boolean = false
        get() {
            return task?.isAborted ?: false
        }
}

interface Cancelable {
    fun cancel()
    var isAborted: Boolean
}