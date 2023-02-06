package com.mlytics.mlysdk.kernel.core

import com.mlytics.mlysdk.driver.MLYDriverOptions
import com.mlytics.mlysdk.kernel.core.api.model.PlatformConfigResponse
import com.mlytics.mlysdk.util.ByteTool
import com.mlytics.mlysdk.util.MessageCode
import com.mlytics.mlysdk.util.TimeTool
import com.mlytics.mlysdk.util.ValidTool
import com.mlytics.mlysdk.util.ValidationError

object KernelSettings {
    var webrtc: WebRTCSettings = WebRTCSettings()
    var client: ClientSettings = ClientSettings()
    var server: ServerSettings = ServerSettings()
    var system: SystemSettings = SystemSettings()
    var stream: StreamSettings = StreamSettings()
    var report: ReportSettings = ReportSettings()
    var download: DownloadSettings = DownloadSettings()
    var proxy: ProxySettings = ProxySettings()
    var platforms: PlatformConfigResponse = PlatformConfigResponse()
}

class WebRTCSettings {
    var options: RTCConfiguration = RTCConfiguration()
}

class RTCConfiguration

data class ClientSettings(
    var id: String? = null,
    var key: String? = null,
    var token: String? = null,
    var origin: String? = "null",
    var peerID: String? = null,
    var sessionID: String? = SessionID.make()
)

data class ServerVersion(
    var fqdn: String = "vsp.mlytics.com", var version: String = "v1"
)

data class ServersVersion(
    var fqdns: MutableList<String?> = mutableListOf(), var version: String = "v1"
)

class ServerSettings {
    var host: ServerVersion = ServerVersion()
    var token: ServerVersion = ServerVersion()
    var config: ServerVersion = ServerVersion()
    var cdnScore: ServerVersion = ServerVersion()
    var metering: ServerVersion = ServerVersion()
    var tracker: ServersVersion = ServersVersion()
}

data class SystemSettings(
    var mode: String = "none", var isP2PAllowed: Boolean = false
)

data class StreamSettings(
    var streamID: String = "", var maxBufferTime: Double = 1.0, var maxBufferSize: Int = 512
)

data class ReportSettings(
    var isEnabled: Boolean = false, var sampleRate: Double = 1.0
)

data class DownloadSettings(
    var maxCacheItems: Int = 512,
    var maxP2PPossibility: Double = 1.0,
    var httpInitialTimeout: Double = 10.0,
    var httpResponseTimeout: Double = 20.0,
)

data class ProxySettings(
    var port: Int = 34567, var host: String = "127.0.0.1", var scheme: String = "http"
)

object SessionID {
    val TIME_PART_FORMAT = "YYYYMMDDHHmmss"
    val RANDOM_PART_LENGTH = 20
    fun make(): String {
        val timePart = TimeTool.makeNowFstring(TIME_PART_FORMAT)
        val randomPart = ByteTool.makeRandomBase36String(RANDOM_PART_LENGTH)
        return "session-${timePart}-${randomPart}"
    }
}

class DriverInfo {
    var sessionID: String? = null
        get() {
            return KernelSettings.client.sessionID
        }

}

class KernelValidator(var options: MLYDriverOptions?) {

    companion object {
        val ID = ValidTool("^[0-9a-zA-Z]{20}$", true)
        val KEY = ValidTool("^[0-9a-zA-Z]{32}$", true)
    }

    fun verify() {
        verify_()
    }

    fun verify_() {
        verifySchema_()
        verifyClientID_()
        verifyClientKey_()
    }

    fun verifySchema_() {

    }

    fun verifyClientID_() {
        if (ID.valid(options?.client?.id)) else {
            throw ValidationError(MessageCode.WSV001)
        }

    }

    fun verifyClientKey_() {
        if (KEY.valid(options?.client?.key)) else {
            throw ValidationError(MessageCode.WSV001)
        }

    }


}

class KernelConfigurator {
    var options: MLYDriverOptions?

    constructor (options: MLYDriverOptions?) {
        this.options = options
    }

    fun config() {
        KernelSettings.client.id = options?.client?.id
        KernelSettings.client.key = options?.client?.key
    }



}
