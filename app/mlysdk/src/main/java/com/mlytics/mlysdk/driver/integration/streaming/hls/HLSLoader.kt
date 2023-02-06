package com.mlytics.mlysdk.driver.integration.streaming.hls

import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.service.filer.seeker.FileSeeker

//import Foundation
object HLSLoader {
    suspend fun load(url: String): Resource? {
        val resource = Resource(url)
        return FileSeeker.fetch(resource)
    }
}

class HLSController
