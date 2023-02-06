//package com.mlytics.mlysdk.vendor
//
////import Foundation
////import Sentry
//public class LoggerSentry : LoggerHandler {
//    var level: LoggerLevel = .ERROR
//    public fun log(
//        level: LoggerLevel,
//        message: String? = null,
//        params: LogParams? = null,
//        options: LogOptions? = null
//    ) {
//        if (this.level < level.rawValue) {
//            return
//        }
//
//        val message = message
//        if (message != null) {
//            SentrySDK.capture(message: message)
//        }
//
//        val error = options?.error
//        if (error != null) {
//            SentrySDK.capture(error: error)
//        }
//
//    }
//
//}
//
//public class SentryComponent : Component {
//    object var instance: SentryComponent?
//    public override constructor () {
//        super.init()
//        instance = this
//    }
//
//    private object var SENTRY_DSN = "https://ae5860091d524009a29130553a1770f7@o255849.ingest.sentry.io/4504314925809664"
//    public override suspend fun activate() {
// super.activate()
//        SentrySDK.start { options ->
//            options.dsn = SENTRY_DSN
//            options.debug = true
//            options.tracesSampleRate = 0.8
//            options.enableAppHangTracking = true
//            options.enableFileIOTracking = true
//            options.enableCoreDataTracking = true
//            options.enableCaptureFailedRequests = true
//        }
//
//        Logger.loggers.append(LoggerSentry())
//    }
//
//    public override suspend fun deactivate() {
// super.deactivate()
//        instance = null
//        SentrySDK.close()
//    }
//
//}
