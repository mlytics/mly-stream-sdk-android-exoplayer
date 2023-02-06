package com.mlytics.mlysdk.kernel.service.filer.seeker.base

import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.util.MInternalError
import com.mlytics.mlysdk.util.MessageCode
import com.mlytics.mlysdk.util.SpecTask

abstract class AbstractDownloader(var _resource: Resource) {
    var _isAborted = false
    var _task: SpecTask? = null

    open suspend fun fetch() {

        this._fetch()

        this._throwIfAborted()
    }

    abstract suspend fun _fetch()

    suspend fun abort() {
        this._isAborted = true
        this._abort()
    }

    abstract suspend fun _abort()

    suspend fun _throwIfAborted() {
        if (this._isAborted) {
            throw MInternalError(MessageCode.ESC001)
        }

    }

}
