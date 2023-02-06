package com.mlytics.mlysdk.kernel.core.utility

import android.net.Uri
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.service.filter.CDNOriginKeeper

//import Foundation
object ProxyURLModifier {
    fun replace(url: String): Uri {
        CDNOriginKeeper.setOrigin(url)
        val _scheme = KernelSettings.proxy.scheme
        val _host = KernelSettings.proxy.host
        val _port = KernelSettings.proxy.port
        return Uri.parse(url).run {
            Uri.parse("${_scheme}${_host}:${_port}${path}")
        }
    }
}
