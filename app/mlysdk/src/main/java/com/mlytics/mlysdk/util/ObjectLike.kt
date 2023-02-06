package com.mlytics.mlysdk.util

class ObjectLike<T> {
    var data: MutableMap<String, T> = mutableMapOf()
    operator fun get(key: String): T? {
        return this.data[key]
    }

    operator fun set(key: String, value: T) {
        if (value == null) {
            this.data.remove(key)
            return
        }
        this.data[key] = value
    }
}
