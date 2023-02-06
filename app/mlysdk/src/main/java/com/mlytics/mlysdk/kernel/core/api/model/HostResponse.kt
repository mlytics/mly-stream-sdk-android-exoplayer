package com.mlytics.mlysdk.kernel.core.api.model

//import Foundation
data class HostResponse(
    var token: String?,
    var config: String?,
    var score: String?,
    var metering: String?,
    var websocket: MutableList<String?>?
)

data class TokenResponse(
    var data: TokenDataResponse, var meta: TokenMetaResponse
)

data class TokenDataResponse(
    var peerID: String?, var token: String?
)

data class TokenMetaResponse(
    var code: Int?, var message: String?, var status: String?
)

data class TokenRequesterReadTokenOptions(
    var clientID: String? = null,
    var origin: String? = null,
    var nonce: Int? = null,
    var signature: String? = null
)

data class TokenRequesterRenewTokenOptions(
    var clientID: String? = null,
    var origin: String? = null,
    var nonce: Int? = null,
    var signature: String? = null,
    var token: String? = null
)

data class ConfigRequesterReadClientConfigOptions(
    var clientID: String? = null
)

data class ConfigRequesterReadPlatformConfigOptions(
    var clientID: String? = null
)

data class ClientConfigResponse(
    var client_id: String? = null,
    var enable_metering_report: Boolean? = null,
    var metering_report: ClientConfigMeteringReportResponse? = null,
    var mode: String? = null,
    var stream_id: String? = null
)

data class ClientConfigMeteringReportResponse(
    var enable: Boolean? = null, var sample_rate: Double? = null
)

data class PlatformConfigResponse(
    var algorithm_id: String? = null,
    var algorithm_ver: String? = null,
    var platforms: MutableList<PlatformConfigPlatformResponse>? = null
)

class PlatformConfigPlatformResponse(
    var enable: Boolean? = null,
    var host: String? = null,
    var id: String? = null,
    var name: String? = null,
    var score: Double? = null
)

data class CDNScoreRequesterReadPlatformScoresOptions(
    var algorithmID: String? = null, var platformIDs: List<String>? = null
)

data class CDNScoreAPIReadPlatformScoresOutcome(
    var platforms: List<CDNScoreAPIReadPlatformScore>? = null
)

data class CDNScoreAPIReadPlatformScore(
    var id: String? = null, var score: Double? = null
)

data class MeteringAPICreateCDNDownloadMeteringData(
    var data: List<MeteringAPICreateCDNDownloadMeteringDataItem>

)

data class MeteringAPICreateCDNDownloadMeteringDataItem(
    var time: Double?,
    var streamID: String?,
    var clientID: String?,
    var sessionID: String?,
    var ok: Boolean?,
    var error: String?,
    var httpCode: Int?,
    var url: String?,
    var masterURL: String?,
    var sourceURL: String?,
    var hostname: String?,
    var platformID: String?,
    var transferSize: Int?,
    var duration: Double?,
    var isComplete: Boolean?,
    var sampleRate: Double?,
    var algorithmID: String?,
    var algorithmVer: String?,

    )

data class MeteringAPICreateCDNDownloadMeteringContent(
    var records: List<MeteringAPICreateCDNDownloadMeteringContentItem>
)

data class MeteringAPICreateCDNDownloadMeteringContentItem(
    var id: String?,
    var contentSize: Int?,
    var startTime: Double?,
    var elapsedTime: Double?,
    var isSuccess: Boolean?,
    var isComplete: Boolean?,
    var swarmURI: String?,
    var sourceURI: String?,
    var requestURI: String?,
    var responseCode: Int?,
    var errorMessage: String?,
    var algorithmID: String?,
    var algorithmVersion: String?,

    )

data class MeteringAPICreateP2PDownloadMeteringContent(
    var records: List<MeteringAPICreateP2PDownloadMeteringContentItem>
)

data class MeteringAPICreateP2PDownloadMeteringContentItem(
    var peerID: String?,
    var contentSize: Int?,
    var startTime: Double?,
    var elapsedTime: Double?,
    var isComplete: Boolean?,
    var swarmURI: String?,
    var sourceURI: String?,
    var requestURI: String?,
    var algorithmID: String?,
    var algorithmVersion: String?
)

data class MeteringAPICreateP2PDownloadMeteringOptions(
    var data: List<MeteringAPICreateP2PDownloadMeteringOptionsItem>
)

data class MeteringAPICreateP2PDownloadMeteringOptionsItem(
    var time: Double?,
    var streamID: String?,
    var clientID: String?,
    var sessionID: String?,
    var peerID: String?,
    var peerType: String?,
    var targetPeerID: String?,
    var targetPeerType: String?,
    var url: String?,
    var masterURL: String?,
    var sourceURL: String?,
    var transferType: String?,
    var transferSize: Int?,
    var duration: Double?,
    var isComplete: Boolean?,
    var sampleRate: Double?,
    var algorithmID: String?,
    var algorithmVer: String?

)
