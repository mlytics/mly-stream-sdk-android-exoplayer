package com.mlytics.mlysdk.util

//import Foundation
interface SpecFlow {
    suspend fun process(): Any?
    suspend fun create()
    suspend fun destory()
}

abstract class AbstractFlow<T> {
    var _content: T
    private var data: MutableMap<String, Any> = mutableMapOf()

    constructor (content: T) {
        this._content = content
    }

    abstract suspend fun process()

    suspend fun create() {

    }

    suspend fun destory() {

    }

    suspend fun _remove(vararg keys: String) {

        for (key in keys) {
            this.data.remove(key)
        }

    }

    suspend fun _expose(key: String, value: Any?) {
        if (value == null) {
            this.data.remove(key)
        } else {
            this.data[key] = value
        }

    }

    suspend fun _require(key: String): Any? {
        return this.data[key]
    }

}

class VoidFlow<T> : Flow {
    var flow: AbstractFlow<T>

    constructor (flow: AbstractFlow<T>) {
        this.flow = flow
    }

    override suspend fun process() {
        this.flow.process()
    }

    override suspend fun create() {
        flow.create()
    }

    override suspend fun destory() {
        flow.destory()
    }

}

class BlockFlow : Flow {
    var block: suspend () -> Unit

    constructor (block: suspend () -> Unit) {
        this.block = block
    }

    override suspend fun process() {
        return block()
    }

}

abstract class Flow : SpecFlow {
    private var data: MutableMap<String, Any> = mutableMapOf()

    suspend fun _remove(vararg keys: String) {

        for (key in keys) {
            this.data.remove(key)
        }

    }

    override suspend fun process() {
    }

    override suspend fun create() {
    }

    override suspend fun destory() {
    }

    suspend fun _expose(key: String, value: Any?) {
        if (value == null) {
            _remove(key)
        } else {
            this.data[key] = value
        }

    }

    suspend fun _require(key: String): Any? {
        return this.data[key]
    }

}

class FlowOptions<T> {
    var id: String? = null
    var content: T? = null
}
