package com.mlytics.mlysdk.util

//import Foundation
class TaskManager<T> : Component() {
    var states: MutableMap<String, TaskState> = mutableMapOf()
    suspend fun createCyclicTask(state: TaskState): TaskState {
        this.states[state.name] = state
        while (isActivated && state.isActivated) {
            state.delay()
            state.process()
        }
        return state
    }

    suspend fun createCyclicTask(
        name: String,
        flow: Flow,
        sleepFirst: Boolean = true,
        sleepSeconds: Double,
        sleepJitter: Double = 0.0,
        maxErrorRetry: Int = -1,
        maxTotalRetry: Int = -1
    ): TaskState {
        val state = TaskState(
            name, flow, sleepFirst, sleepSeconds, sleepJitter, maxErrorRetry, maxTotalRetry
        )
        return this.createCyclicTask(state)
    }

    suspend fun createCyclicTask(
        name: String,
        abstractFlow: AbstractFlow<*>,
        sleepFirst: Boolean = true,
        sleepSeconds: Double,
        sleepJitter: Double = 0.0,
        maxErrorRetry: Int = -1,
        maxTotalRetry: Int = -1
    ): TaskState {
        return this.createCyclicTask(
            name,
            VoidFlow(abstractFlow),
            sleepFirst,
            sleepSeconds,
            sleepJitter,
            maxErrorRetry,
            maxTotalRetry
        )
    }

}

class TaskState {
    var name: String
    var flow: Flow
    var sleepFirst: Boolean
    var sleepSeconds: Double
    var sleepJitter: Double
    var maxErrorRetry: Int
    var maxTotalRetry: Int
    var count: Int = 0
    var result: Any? = null
    var isActivated: Boolean
        get() {
            return maxTotalRetry == -1 || count <= maxTotalRetry
        }
        set(newValue) {

        }

    constructor (
        name: String,
        flow: Flow,
        sleepFirst: Boolean = true,
        sleepSeconds: Double,
        sleepJitter: Double = 0.0,
        maxErrorRetry: Int = -1,
        maxTotalRetry: Int = -1
    ) {
        this.name = name
        this.flow = flow
        this.sleepFirst = sleepFirst
        this.sleepSeconds = sleepSeconds
        this.sleepJitter = sleepJitter
        this.maxErrorRetry = maxErrorRetry
        this.maxTotalRetry = maxTotalRetry
    }

    constructor (
        name: String,
        flow: AbstractFlow<*>,
        sleepFirst: Boolean = true,
        sleepSeconds: Double,
        sleepJitter: Double = 0.0,
        maxErrorRetry: Int = -1,
        maxTotalRetry: Int = -1
    ) {
        this.name = name
        this.flow = VoidFlow(flow)
        this.sleepFirst = sleepFirst
        this.sleepSeconds = sleepSeconds
        this.sleepJitter = sleepJitter
        this.maxErrorRetry = maxErrorRetry
        this.maxTotalRetry = maxTotalRetry
    }

    constructor (
        name: String,
        sleepFirst: Boolean = false,
        sleepSeconds: Double,
        sleepJitter: Double = 0.0,
        maxErrorRetry: Int = -1,
        maxTotalRetry: Int = -1,
        callee: suspend () -> Unit
    ) {
        this.name = name
        this.sleepFirst = sleepFirst
        this.sleepSeconds = sleepSeconds
        this.sleepJitter = sleepJitter
        this.maxErrorRetry = maxErrorRetry
        this.maxTotalRetry = maxTotalRetry
        this.flow = BlockFlow(callee)
    }

    suspend fun delay() {
        if (this.count > 0 || this.sleepFirst) {
            kotlinx.coroutines.delay(this.sleepSeconds.toLong() * 1000)
        }

    }

    fun willProcess() {
        this.count += 1
    }

    suspend fun process() {
        willProcess()
        this.result = flow.process()
    }

}
