package com.mlytics.mlysdk.kernel.core.infra.model

class CDNConfigRecord {
    var id: String? = null
    var name: String? = null
    var type: String? = null
    var domain: String? = null
    var isEnabled: Boolean? = null
    var meanBandwidth: Double? = null
    var meanAvailability: Double? = null
    var currentScore: Double? = null
}

class CDNDownloadRecord {
    var ctime: Double? = null
    var id: String? = null
    var name: String? = null
    var type: String? = null
    var domain: String? = null
    var meanBandwidth: Double? = null
    var meanAvailability: Double? = null
    var currentScore: Double? = null
}

class HTTPDownloadRecord {
    var ctime: Double? = null
    var id: String? = null
    var name: String? = null
    var type: String? = null
    var domain: String? = null
    var totalSize: Int? = null
    var contentType: String? = null
    var contentSize: Int? = null
    var startTime: Double? = null
    var elapsedTime: Double? = null
    var bandwidth: Double? = null
    var isAborted: Boolean? = null
    var isSuccess: Boolean? = null
    var isOutlier: Boolean? = null
    var isComplete: Boolean? = null
    var swarmID: String? = null
    var swarmURI: String? = null
    var sourceURI: String? = null
    var requestURI: String? = null
    var responseCode: Int? = null
    var errorMessage: String? = null
    var algorithmID: String? = null
    var algorithmVersion: String? = null

}

class P2PDownloadRecord {
    var ctime: Double? = null
    var peerID: String? = null
    var totalSize: Int? = null
    var contentType: String? = null
    var contentSize: Int? = null
    var startTime: Double? = null
    var elapsedTime: Double? = null
    var bandwidth: Double? = null
    var isOutlier: Boolean? = null
    var isComplete: Boolean? = null
    var swarmID: String? = null
    var swarmURI: String? = null
    var sourceURI: String? = null
    var requestURI: String? = null
    var algorithmID: String? = null
    var algorithmVersion: String? = null
}

class TrackerStateRecord {
    var peerID: String? = null
    var isAvailable: Boolean? = null
}

class NodeStateRecord {
    var peerID: String? = null
    var isAvailable: Boolean? = null
}

class SwarmStateRecord {
    var swarmID: String? = null
    var isAvailable: Boolean? = null
}

class UserStateRecord {
    var peerID: String? = null
    var swarmID: String? = null
    var isAvailable: Boolean? = null
}
