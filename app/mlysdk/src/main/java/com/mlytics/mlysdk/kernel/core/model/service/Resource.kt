package com.mlytics.mlysdk.kernel.core.model.service

import android.net.Uri
import com.mlytics.mlysdk.util.HashTool
import com.mlytics.mlysdk.util.WatchTool

//import Foundation
class Resource {
    var id: String? = null
    var swarmID: String? = null
    var swarmURI: String? = null
    var sourceURI: String? = null
    var uri: String? = null
    var type: String? = null
    var total: Int? = null
    var ctime: WatchTool = WatchTool()
    var mtime: WatchTool = WatchTool()
    var content: ByteArray? = null
    var priority = 0
    var isShareable = false
    var isComplete: Boolean = false
    var size: Int = 0
        get() {
            return content?.size ?: 0
        }

    constructor (uri: String) {
        this.id = Resource.makeID(uri)
        this.uri = uri
    }

    fun concat(options: Any) {

    }

    fun nextRange(): ResourceRange {
        return ResourceRange()
    }

    companion object {
        fun makeID(uri: String): String {
            return Uri.parse(uri).run {
                var _port = if (port == null || port == -1) "" else port.toString()
                HashTool.sha256base64("${host}:${_port}${path}?${query}") ?: uri
            }
        }
    }

}

class ResourceRange {
    var start: Int? = null
    var end: Int? = null
}

class ResourceConcatOptions {
    var chunk: ByteArray? = null
    var range: ResourceRange? = null
}

class ResourceStat {
    var id: String? = null
    var completion: Int? = null
}
