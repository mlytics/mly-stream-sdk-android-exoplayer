package com.mlytics.mlysdk.util

import kotlinx.coroutines.delay

//import Foundation
class Processor(var options: ProcessorOptions = ProcessorOptions()) {

    var _state = ProcessorState()
    var _processStats: MutableList<ProcessStat> = mutableListOf()
    var _isEveryProcessExited: Boolean
        get() {
            for (stat in _processStats) {
                if (stat.isActive) {
                    return false
                }
            }
            return true
        }
        set(newValue) {

        }

    fun register(process: SpecProcess) {
        this._processStats.add(ProcessStat(process))
    }

    suspend fun activate() {
        _initProcesses()
        _startProcesses()
        _serveProcesses()
    }

    fun deactivate() {
        this._state.toExiting()
    }

    suspend fun _initProcesses() {

        for (processStat in this._processStats) {
            processStat.initProcess()
        }

        this._state.toRunning()
    }

    suspend fun _startProcesses() {
        for (processStat in this._processStats) {
            processStat.startProcess(this._state)
            this._state.toExiting()
        }
    }

    suspend fun _serveProcesses() {
        val watch = WatchTool()
        while (true) {
            if (this._state.isExiting) {
                _emitShutInfo()
                _waitProcesses()
                _emitExitInfo()
                break
            }
            if (watch.hasElapsedTimeS(options.heartbeatInterval)) {
                _emitBeatInfo()
                watch.reset()
            }
        }
        this._state.toExited()
    }

    fun _emitBeatInfo() {
        val stats = _makeProcessStatInfos()
        Logger.info("Processor heartbeats. ${stats}")
    }

    fun _emitShutInfo() {
        Logger.info("Processor will graceful shutdown in ${this.options.gracefulTimeout} ms.")
    }

    suspend fun _waitProcesses() {
        val watch = WatchTool()
        while (true) {
            _emitBeatInfo()
            if (this._isEveryProcessExited) {
                break
            }

            if (watch.hasElapsedTimeS(this.options.gracefulTimeout)) {
                break
            }

            delay(1000)
        }

    }

    fun _emitExitInfo() {
        val stats = _makeProcessStatInfos()
        if (this._isEveryProcessExited) {
            Logger.info("Processor graceful shutdown normally. ${stats}")
        } else {
            Logger.warn("Processor graceful shutdown abnormally. ${stats}")
        }

    }

    fun _makeProcessStatInfos(): List<String> {
        return this._processStats.map {
            it.process.toString()
        }

    }

    fun throwIfError() {
        val errors = this._processStats.filter({ it.error != null }).map({ it.error })
        if (errors.isEmpty()) else {
            throw ValidationError(MessageCode.EMU060)
        }
    }
}

data class ProcessorOptions(
    var gracefulTimeout: Long = 10 * 1000,
    var heartbeatInterval: Long = 5 * 60 * 1000
)

interface SpecProcess {
    fun Initialize()
    suspend fun StartDaemon(state: SpecProcessorState)
}

interface SpecProcessStat {
    var error: Exception?

    var isActive: Boolean

}

class ProcessStat : SpecProcessStat {
    override var error: Exception? = null
    override var isActive: Boolean = false
    var process: SpecProcess?

    constructor (process: SpecProcess? = null) {
        this.process = process
    }

    suspend fun initProcess() {

        process?.Initialize()
        this.isActive = true
    }

    suspend fun startProcess(state: SpecProcessorState) {
        try {

            this.process?.StartDaemon(state)
            this.isActive = false
            Logger.info("Process exit failed. ${this}")
        } catch (err: Exception) {
            this.error = err
            Logger.error("Process exit failed. ${this}", err)
            throw err
        }

    }

}
