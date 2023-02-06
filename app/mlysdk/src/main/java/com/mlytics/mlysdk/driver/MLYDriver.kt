package com.mlytics.mlysdk.driver

import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MessageCode
import com.mlytics.mlysdk.util.ValidationError
import kotlinx.coroutines.runBlocking

//import Foundation
object MLYDriver {

    fun initialize(clientId: String, clientKey: String) {
        DriverManager.initialize(clientId, clientKey)
    }

    fun activate() {
        DriverManager.activate()
    }

    fun deactivate() {
        DriverManager.deactivate()
    }

}

object DriverManager {

    var supervisor = DriverSupervisor()
    var isActivated = false
    var isConfigured = false
    var isSupported = true
    fun config(options: MLYDriverOptions?) {

        DriverConfigurator(options).config()
        this.isConfigured = true
    }

    fun initialize(clientId: String, clientKey: String) {

        var options = MLYDriverOptions()
        options.client.id = clientId
        options.client.key = clientKey

        this.config(options)
        this.activate()
    }

    fun activate() {
        if (this.isConfigured) else {
            Logger.error(ValidationError(MessageCode.WSV001))
            return
        }
        runBlocking {
            supervisor.activate()
        }
        this.isActivated = true
    }

    fun deactivate() {
        if (this.isActivated) else {
            return
        }

        this.supervisor.deactivate()
        this.isActivated = false
    }


}
