package com.mlytics.mlysdk.kernel.service.filer

import com.mlytics.mlysdk.kernel.service.filer.seeker.FileSeeker
import com.mlytics.mlysdk.util.Component

object FilerProvider : Component() {
    override suspend fun activate() {
        this._loadFileSeeker()
    }

    suspend fun _loadFileSeeker() {
        FileSeeker._activate()
    }

    override suspend fun deactivate() {
        this._unloadFileSeeker()
    }

    suspend fun _unloadFileSeeker() {
        FileSeeker._deactivate()
    }

}
