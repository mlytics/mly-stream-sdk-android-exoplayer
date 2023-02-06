package com.mlytics.mlysdk.kernel.core.api.base

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.api.model.CDNScoreAPIReadPlatformScoresOutcome
import com.mlytics.mlysdk.kernel.core.api.model.CDNScoreRequesterReadPlatformScoresOptions
import com.mlytics.mlysdk.kernel.core.api.model.ClientConfigResponse
import com.mlytics.mlysdk.kernel.core.api.model.ConfigRequesterReadClientConfigOptions
import com.mlytics.mlysdk.kernel.core.api.model.ConfigRequesterReadPlatformConfigOptions
import com.mlytics.mlysdk.kernel.core.api.model.HostResponse
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateCDNDownloadMeteringData
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateP2PDownloadMeteringOptions
import com.mlytics.mlysdk.kernel.core.api.model.PlatformConfigResponse
import com.mlytics.mlysdk.kernel.core.api.model.TokenRequesterReadTokenOptions
import com.mlytics.mlysdk.kernel.core.api.model.TokenRequesterRenewTokenOptions
import com.mlytics.mlysdk.kernel.core.api.model.TokenResponse
import com.mlytics.mlysdk.util.PrepareCall
import com.mlytics.mlysdk.util.Requester

class HostRequester :
    Requester(KernelSettings.server.host.fqdn, KernelSettings.download.httpResponseTimeout) {

    suspend fun readHosts(): HostResponse? {
        return fetch("/host.json")
    }

}

class TokenRequester : Requester(
    KernelSettings.server.token.fqdn, KernelSettings.download.httpResponseTimeout
) {

    suspend fun readToken(options: TokenRequesterReadTokenOptions): TokenResponse? {
        return fetch(
            "/token/jwt/", mutableMapOf(Pair("client_id", options.clientID)), mutableMapOf(
                Pair("origin", options.origin),
                Pair("nonce", options.nonce.toString()),
                Pair("signature", options.signature)
            )
        )
    }

    suspend fun renewToken(options: TokenRequesterRenewTokenOptions): TokenResponse? {
        return fetch(
            "/token/jwt/renew/", mutableMapOf(Pair("client_id", options.clientID)), mutableMapOf(
                Pair("origin", options.origin),
                Pair("nonce", options.nonce.toString()),
                Pair("signature", options.signature),
                Pair("authorization", "token ${options.token}")
            )
        )
    }

}

class ConfigRequester : Requester(
    KernelSettings.server.config.fqdn, KernelSettings.download.httpResponseTimeout
) {

    suspend fun readClientConfig(options: ConfigRequesterReadClientConfigOptions): ClientConfigResponse? {
        return fetch("/${options.clientID}-config.json")
    }

    suspend fun readPlatformConfig(options: ConfigRequesterReadPlatformConfigOptions): PlatformConfigResponse? {
        return fetch("/${options.clientID}-platforms.json")
    }

}

class CDNScoreRequester : Requester(
    com.mlytics.mlysdk.kernel.core.KernelSettings.server.cdnScore.fqdn,
    com.mlytics.mlysdk.kernel.core.KernelSettings.download.httpResponseTimeout
) {

    suspend fun readPlatformScores(options: CDNScoreRequesterReadPlatformScoresOptions): CDNScoreAPIReadPlatformScoresOutcome? {
        return fetch(
            "/scorer/algorithms/${options.algorithmID}/scores/", mutableMapOf(
                Pair("platforms[]", options.platformIDs),
                Pair("stream_id", KernelSettings.stream.streamID)
            )
        )
    }

}

class MeteringRequester : Requester(
    com.mlytics.mlysdk.kernel.core.KernelSettings.server.metering.fqdn,
    com.mlytics.mlysdk.kernel.core.KernelSettings.download.httpResponseTimeout
) {

    suspend fun createCDNDownloadMetering(options: MeteringAPICreateCDNDownloadMeteringData): PrepareCall? {
        return fetch("/metering/", mutableMapOf(Pair("data", options)))
    }

    suspend fun createP2PDownloadMetering(options: MeteringAPICreateP2PDownloadMeteringOptions): PrepareCall? {
        return fetch("/p2p-metering/", mutableMapOf(Pair("data", options)))
    }

}
