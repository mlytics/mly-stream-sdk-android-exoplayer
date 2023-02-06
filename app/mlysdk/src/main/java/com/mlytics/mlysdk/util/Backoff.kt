package com.mlytics.mlysdk.util

//import Foundation
class Backoff(
    var delayer: Delayer = LinearDelayer(),
    var baseInterval: Long = 0,
    var multiplier: Double = 1.1,
    var maxInterval: Long = 5000,
    var maxAttempts: Long = Long.MAX_VALUE
) {

    init {
        this.delayer.baseInterval = baseInterval
        this.delayer.multiplier = multiplier
        this.delayer.maxInterval = maxInterval
        this.delayer.maxAttempts = maxAttempts
    }

    suspend fun delay() {
        val ms = this.delayer.next()
        Logger.debug("delay ms=${ms}")
        delay(ms)
    }

    private suspend fun delay(ms: Long) {
        kotlinx.coroutines.delay(ms)
    }

    fun reset() {
        this.delayer.reset()
    }

    fun build(closure: () -> Delayer): Backoff {
        val delayer = closure()
        val r = Backoff(delayer)
        return r
    }

}

class LinearDelayer() : Delayer(
    baseInterval = 500, multiplier = 2.0, maxInterval = 10000
) {
    override fun computeInterval(): Long {
        return baseInterval + (multiplier * attempts.count()).toLong()
    }
}

class ExponentialDelayer() : Delayer(
    baseInterval = 100, multiplier = 1.8, maxInterval = 20000, maxAttempts = 5000
) {

    override fun computeInterval(): Long {
        return baseInterval + (Math.pow(multiplier, attempts.count().toDouble())).toLong()
    }
}

abstract class Delayer(
    var baseInterval: Long = 0,
    var multiplier: Double = 1.0,
    var jitterFactor: Double = 0.2,
    var maxInterval: Long = 5000,
    var maxMilliSeconds: Long = 60000,
    var maxAttempts: Long = Long.MAX_VALUE
) {

    open var interval: Long = 0
    open var milliSeconds = Counter()
    open var attempts = Counter()

    var _isOverMaxAttempts: Boolean = false
        get() = this.maxAttempts >= 0 && this.attempts.hasCountedOver(this.maxAttempts)

    var _isOverMaxSeconds: Boolean = false
        get() = this.maxMilliSeconds >= 0 && this.milliSeconds.hasCountedOver(this.maxMilliSeconds)

    fun next(): Long {
        this._increaseSeconds()
        this._increaseAttempts()
        if (this._isOverMaxSeconds) {
            return this.maxMilliSeconds
        }

        if (this._isOverMaxAttempts) {
            return Long.MAX_VALUE
        }

        return this.interval
    }

    fun _increaseSeconds() {
        val interval = this._makeNextInterval()
        this.interval += interval
        this.milliSeconds.plus(interval)
    }

    fun _makeNextInterval(): Long {
        var interval = this.computeInterval()
        interval = this._jitterInterval(interval)
        interval = this._restrictInterval(interval)
        return interval
    }

    abstract fun computeInterval(): Long

    fun _jitterInterval(interval: Long): Long {
        return (interval * (1 - this.jitterFactor + 2 * MathTool.randomDouble() * this.jitterFactor)).toLong()
    }

    fun _restrictInterval(interval: Long): Long {
        return if (interval <= maxInterval) interval else this.maxInterval
    }

    fun _increaseAttempts() {
        this.attempts.up()
    }

    fun reset() {
        this.interval = 0
        this.milliSeconds.reset()
        this.attempts.reset()
    }

}

class Counter {
    var _count: Long = 0
    var _initial: Long = 0
    var _interval: Long = 1
    fun count(): Long {
        return this._count
    }

    fun reset(): Counter {
        this._count = this._initial
        return this
    }

    fun up(): Counter {
        this._count += this._interval
        return this
    }

    fun down(): Counter {
        this._count -= this._interval
        return this
    }

    fun plus(value: Long): Counter {
        this._count += value
        return this
    }

    fun minus(value: Long): Counter {
        this._count -= value
        return this
    }

    fun hasCountedOver(value: Long): Boolean {
        return this._count > value
    }

    fun hasCountedBelow(value: Long): Boolean {
        return this._count < value
    }

    fun hasCountedUpTo(value: Long): Boolean {
        return this._count >= value
    }

    fun hasCountedDownTo(value: Long): Boolean {
        return this._count <= value
    }

}
