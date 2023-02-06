package com.mlytics.mlysdk.kernel.system

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.api.base.ConfigRequester
import com.mlytics.mlysdk.kernel.core.api.base.HostRequester
import com.mlytics.mlysdk.kernel.core.api.base.TokenRequester
import com.mlytics.mlysdk.kernel.core.api.model.ConfigRequesterReadClientConfigOptions
import com.mlytics.mlysdk.kernel.core.api.model.ConfigRequesterReadPlatformConfigOptions
import com.mlytics.mlysdk.kernel.core.api.model.TokenRequesterReadTokenOptions
import com.mlytics.mlysdk.kernel.core.servant.CDN
import com.mlytics.mlysdk.kernel.core.servant.MCDNStatsHolder
import com.mlytics.mlysdk.util.DateTool
import com.mlytics.mlysdk.util.Flow
import com.mlytics.mlysdk.util.HashTool
import com.mlytics.mlysdk.util.Logger
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SystemInitializer : Flow() {
    override suspend fun process() {
        UpdateServerHostsHandler().process()
//        UpdateClientTokenHandler().process()
//        UpdateClientConfigHandler().process()
//        UpdateSystemConfigHandler().process()
        runBlocking {
            val token = launch {
                UpdateClientTokenHandler().process()
            }
            val client = launch {
                UpdateClientConfigHandler().process()
            }
            val system = launch {
                UpdateSystemConfigHandler().process()
            }
            joinAll(token, client, system)
        }
    }

}

class SystemDeinitializer : Flow() {
    override suspend fun process(): Unit {
    }
}

class UpdateServerHostsHandler : Flow() {
    override suspend fun process(): Unit {
        Logger.debug("ReadHostsFlow() process")
        val resp = HostRequester().readHosts()
        if (resp != null)
        else {
            Logger.error("ReadHostsFlow() ERROR")
            return
        }

        KernelSettings.server.token.fqdn = resp.token!!
        KernelSettings.server.config.fqdn = resp.config!!
        KernelSettings.server.metering.fqdn = resp.metering!!
        KernelSettings.server.cdnScore.fqdn = resp.score!!
        KernelSettings.server.tracker.fqdns = resp.websocket!!
    }

}

class UpdateClientTokenHandler : Flow() {
    fun intakeNonce(): Int {
        return DateTool.seconds().toInt()
    }
//hashed1	String	"55dfceac37e7517702582ff8f8eeeafc31d5ee2c7071962eb1b057386ce5c2db"
    fun intakeSignature(): String {
        val id = KernelSettings.client.id!!
        val key = KernelSettings.client.key!!
        val origin = KernelSettings.client.origin!!
        val nonce = intakeNonce().toString()
        val hashed1 = HashTool.sha256base16(nonce)!!
        val hashed2 = HashTool.sha256base16("${origin}${id}${hashed1}")!!
        val signature = HashTool.sha256base64url("${key}${hashed2}")!!
        return signature
    }

    override suspend fun process(): Unit {
        Logger.debug("ReadTokenFlow() process")
        var options = TokenRequesterReadTokenOptions()
        options.clientID = KernelSettings.client.id
        options.origin = KernelSettings.client.origin
        options.nonce = intakeNonce()
        options.signature = intakeSignature()
        val resp = TokenRequester().readToken(options)
        if (resp != null)
        else {
            Logger.error("ReadTokenFlow() ERROR")
            return
        }

        KernelSettings.client.token = resp.data.token
        KernelSettings.client.peerID = resp.data.peerID
    }

}

class UpdateClientConfigHandler : Flow() {
    override suspend fun process(): Unit {
        Logger.debug("ClientConfigFlow() process")
        var options = ConfigRequesterReadClientConfigOptions()
        options.clientID = KernelSettings.client.id
        val resp = ConfigRequester().readClientConfig(options)
        if (resp != null)
        else {
            Logger.error("ClientConfigFlow() ERROR")
            return
        }

        KernelSettings.system.mode = resp.mode ?: "none"
        KernelSettings.system.isP2PAllowed = resp.mode?.contains("p2p") ?: false
        KernelSettings.report.isEnabled = resp.enable_metering_report ?: false
        KernelSettings.report.sampleRate = resp.metering_report?.sample_rate ?: 1.0
        KernelSettings.stream.streamID = resp.stream_id ?: "null"
    }

}

class UpdateSystemConfigHandler : Flow() {
    override suspend fun process(): Unit {
        Logger.debug("PlatformConfigFlow() process")
        var options = ConfigRequesterReadPlatformConfigOptions()
        options.clientID = KernelSettings.client.id
        val resp = ConfigRequester().readPlatformConfig(options)
        if (resp != null)
        else {
            Logger.error("PlatformConfigFlow() ERROR")
            return
        }

        KernelSettings.platforms = resp
        val platforms = resp.platforms
        if (platforms != null) else {
            Logger.error("PlatformConfigFlow() ERROR: nil platforms")
            return
        }


        for (platform in platforms) {
            val cdn = CDN()
            cdn.domain = platform.host
            cdn.id = platform.id
            cdn.businessScore = platform.score ?: 1.0
            cdn.currentScore = platform.score ?: 1.0
            cdn.isEnabled = platform.enable
            cdn.name = platform.name
            val id = cdn.id
            if (id != null) {
                MCDNStatsHolder.cdns[id] = cdn
            }

        }

    }

}

