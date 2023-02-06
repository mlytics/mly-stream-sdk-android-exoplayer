package com.mlytics.mlysdk.util

import kotlin.coroutines.cancellation.CancellationException

class ChannelHub {
    var data: MutableMap<String, Any> = mutableMapOf()
    var conditions: MutableMap<String, Condition> = mutableMapOf()
    suspend fun close() {
        this.conditions.values.forEach { v ->
            v.deny(CancellationException())
        }
        conditions.clear()
    }

    fun connect(channel: String) {
        this.conditions[channel] = Condition()
    }

    fun deliver(key: String, value: Any) {
        this.data[key] = value
        this.conditions[key]?.pass()
    }

    suspend fun extract(key: String): Any? {
        this.conditions[key]?.done()
        return this.data[key]
    }

}
