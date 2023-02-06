package com.mlytics.mlysdk.util

//import Foundation
class Cache<T>(var max: Int = 512) {
    private var map: MutableMap<String, CacheObject<T>> = mutableMapOf()
    private var time: MutableMap<Long, Queue<CacheObject<T>>> = mutableMapOf()
    private var shouldClear: Boolean = false
        get() = this.map.size > this.max

    fun has(key: String): Boolean {
        return this.get(key) != null
    }

    fun remove(key: String): T? {
        val cache = this.map.remove(key)
        if (cache != null) else {
            return null
        }

        cache.removed = true
        return cache.value
    }

    fun removeAll() {
        this.map.clear()
        this.time.clear()
    }

    fun set(key: String, value: T, ttl: Long) {
        val cache = CacheObject(key, value, ttl)
        this.map[key] = cache
        this.queue(ttl).append(cache)
        if (this.shouldClear) {
            this.clearExpired()
        }
    }

    fun get(key: String): T? {
        val cache = this.map[key]
        if (cache != null) else {
            return null
        }

        if (cache.isExpired()) {
            this.remove(key)
            return null
        }

        return cache.value
    }

    private fun clearExpired() {
        val now = DateTool.millis()

        for (queue in this.time.values) {
            while (queue.first?.isExpired(now) == true) {
                this.removeFirst(queue)
            }

        }

    }

    private fun queue(ttl: Long): Queue<CacheObject<T>> {
        var q = time[ttl]
        if (q != null) {
            return q
        }

        q = Queue<CacheObject<T>>()
        this.time[ttl] = q
        return q
    }

    private fun removeFirst(queue: Queue<CacheObject<T>>) {
        val first = queue.removeFirst()
        if (first != null) else {
            return
        }

        this.map.remove(first.key)
    }

}

class CacheObject<T> {
    var value: T
    var key: String
    var ttl: Long
    var removed = false

    constructor (forKey: String, value: T, ttl: Long) {
        this.key = forKey
        this.value = value
        this.ttl = DateTool.millis() + ttl
    }

    fun isExpired(time: Long = DateTool.millis()): Boolean {
        return this.removed || this.ttl <= time
    }

}
