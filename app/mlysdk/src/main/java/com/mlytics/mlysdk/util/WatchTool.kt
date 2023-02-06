package com.mlytics.mlysdk.util

class WatchTool {
    private var _startTime: Long
    private var _endTime: Long = Long.MAX_VALUE

    constructor () {
        this._startTime = System.currentTimeMillis()
    }

    fun hasElapsedTimeS(second: Long): Boolean {
        return this.stop() >= second
    }

    fun stop(): Long {
        this._endTime = System.currentTimeMillis()
        return this.elapsedTimeS()
    }

    fun elapsedTimeS(): Long {
        return this._endTime - this._startTime
    }

    fun start() {
        this._startTime = System.currentTimeMillis()
    }

    fun reset() {
        this.start()
    }

}
