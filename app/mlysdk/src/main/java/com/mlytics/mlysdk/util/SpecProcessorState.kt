package com.mlytics.mlysdk.util

interface SpecProcessorState {
    var isInitial: Boolean

    var isRunning: Boolean
    var isExiting: Boolean

    var isExited: Boolean

}

enum class EnumProcessorState {
    initial, running, exiting, exited
}

class ProcessorState : SpecProcessorState {
    var state: EnumProcessorState = EnumProcessorState.initial
    override var isInitial: Boolean
        get() {
            return this.state == EnumProcessorState.initial
        }
        set(newValue) {
            this.state = EnumProcessorState.initial
        }

    override var isRunning: Boolean
        get() {
            return this.state == EnumProcessorState.running
        }
        set(newValue) {
            this.state = EnumProcessorState.running
        }

    override var isExiting: Boolean
        get() {
            return this.state == EnumProcessorState.exiting
        }
        set(newValue) {
            this.state = EnumProcessorState.exiting
        }

    override var isExited: Boolean
        get() {
            return this.state == EnumProcessorState.exited
        }
        set(newValue) {
            this.state = EnumProcessorState.exited
        }

    fun toInitial() {
        this.state = EnumProcessorState.initial
    }

    fun toRunning() {
        this.state = EnumProcessorState.running
    }

    fun toExiting() {
        this.state = EnumProcessorState.exiting
    }

    fun toExited() {
        this.state = EnumProcessorState.exited
    }

}
