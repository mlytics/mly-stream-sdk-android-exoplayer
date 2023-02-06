package com.mlytics.mlysdk.kernel.system

import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import com.mlytics.mlysdk.driver.integration.streaming.hls.HLSLoader
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.const.service.URLContentType
import com.mlytics.mlysdk.kernel.core.service.filter.CDNOriginKeeper
import com.mlytics.mlysdk.util.Backoff
import com.mlytics.mlysdk.util.Component
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MathTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

//import Foundation
//import GCDWebServer
object ProxyComponent : Component() {

    private var usePort: Int = 34567
    private var server: AsyncHttpServer = AsyncHttpServer()
    var scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var backoff = Backoff()

    override suspend fun deactivate() {
        this.scope.cancel()
        this.server.stop()
        this.isActivated = false
    }

    override suspend fun activate() {
        this.buildServer()
        this.isActivated = true
    }

    private suspend fun buildServer(){
        server.setErrorCallback {
            Logger.error("server start failed: port=${usePort}", it)
            if (!this@ProxyComponent.isActivated) {
                return@setErrorCallback
            }
            runBlocking {
                changePort()
                delay(1000)
                listen()
            }
        }
        server.get(
            ".*"
        ) { request, response ->
            scope.launch {
                handle(request, response)
            }
        }
        this.listen()
    }

    private fun listen() {
        server.listen(usePort)
        KernelSettings.proxy.port = this.usePort
    }

    private fun changePort() {
        this.usePort = 10000 + MathTool.random(65535 - 10000)
    }

    private suspend fun handle(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse) {
        Logger.debug("proxy: start ${request.url}")

        val url = URL(request.url)
        val origin = CDNOriginKeeper.getOrigin(url)
        Logger.debug("proxy: origin ${origin}")
        val res = HLSLoader.load(origin.toString())
        if (res == null) {
            Logger.error("proxy: nil resource")
            return
        }

        val content = res.content
        if (content == null) {
            Logger.error("proxy: nil data")
            return
        }

        if (res.type == null) {
            res.type = URLContentType.from(url).rawValue
        }

        Logger.debug("proxy: done ${url} count=${content.size} ")
        response.send(res.type, res.content)
    }

}
