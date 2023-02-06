package com.mlytics.mlysdk.driver

import com.mlytics.mlysdk.kernel.system.SystemBooterHolder
import com.mlytics.mlysdk.util.Backoff
import com.mlytics.mlysdk.util.ExponentialDelayer
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.Processor
import com.mlytics.mlysdk.util.ProcessorOptions
import com.mlytics.mlysdk.util.SpecProcess
import com.mlytics.mlysdk.util.SpecProcessorState
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

//import Foundation
object DriverDaemon {

    var _processor: Processor? = null

    suspend fun activate() {
        this._processor = Processor(ProcessorOptions())
        this._processor?.register(createProcess())
        this._processor?.activate()
        this._processor?.throwIfError()
    }

    fun deactivate() {
        this._processor?.deactivate()
        this._processor = null
    }

    fun createProcess(): SpecProcess {
        return SystemProcess()
    }

}

class SystemProcess : SpecProcess {
    override fun Initialize() {

    }
    override suspend fun StartDaemon(state: SpecProcessorState) {
        runBlocking {
            SystemBooterHolder.activate()
            while (state.isRunning) {
                delay(1000)
            }
            SystemBooterHolder.deactivate()
        }
    }
}

class DriverSupervisor {
    var _isActive: Boolean = false
    var _backoff: Backoff = Backoff(
        ExponentialDelayer(), maxInterval = 60
    )
    var _daemon = DriverDaemon

    suspend fun activate() {
        this._isActive = true
        while (this._isActive) {
            try {

                this._daemon.activate()
                this._backoff.reset()
            } catch (err: Exception) {
                Logger.error(err)
                this._backoff.delay()
            }

        }

    }

    fun deactivate() {
        this._daemon.deactivate()
        this._isActive = false
    }

}
