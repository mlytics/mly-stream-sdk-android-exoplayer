package com.mlytics.mlysdk.util

open class Emittery {
    var ev = EventTool()
    var count: MutableMap<String, Int> = mutableMapOf()
    fun clearListeners() {
        this.count.clear()
        this.ev.unregisterAll()
    }

    fun on(event: String, handler: (Any) -> Unit) {
        this.count[event] = (this.count[event] ?: 0) + 1
        this.ev.on(event, handler)
    }

    fun emit(event: String, data: Any) {
        this.ev.emit(event, data)
    }

    fun listenerCount(event: String): Int {
        return this.count[event] ?: 0
    }

}
