package com.mlytics.mlysdk.kernel.service.filer.seeker

import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.core.service.filter.seeker.AbstractFileDownloader

class FileDownloader(_resource: Resource) : AbstractFileDownloader(_resource) {

    override suspend fun _fetch() {

        super._fetch()
    }

}
