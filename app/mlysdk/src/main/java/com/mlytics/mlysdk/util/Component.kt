package com.mlytics.mlysdk.util

interface SpecComponent {
    var isActivated: Boolean

    var isRunning: Boolean

    suspend fun activate()
    suspend fun deactivate()
    suspend fun reactivate()
}

open class Component : SpecComponent {
    override var isActivated = false
    override var isRunning = false
    override suspend fun activate() {
        this.isActivated = true
    }

    override suspend fun deactivate() {
        this.isActivated = false
    }

    override suspend fun reactivate() {
        deactivate()
        activate()
    }

}
