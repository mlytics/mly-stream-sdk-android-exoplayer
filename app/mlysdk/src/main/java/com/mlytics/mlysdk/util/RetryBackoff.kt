package com.mlytics.mlysdk.util

//import Foundation
class RetryBackoff {
    var start: Long
    var delayer: Delayer

    constructor (delayer: Delayer) {
        this.delayer = delayer
        this.start = System.currentTimeMillis()
    }

    fun next(): Boolean {
        return System.currentTimeMillis() - this.start >= this.delayer.next()
    }

    fun reset() {
        this.start = System.currentTimeMillis()
        this.delayer.reset()
    }

}
