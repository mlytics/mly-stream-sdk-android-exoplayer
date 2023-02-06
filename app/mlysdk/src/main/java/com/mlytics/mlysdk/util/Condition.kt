package com.mlytics.mlysdk.util

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//import Foundation
class Condition {
    var continuations: MutableList<Continuation<Unit>> = mutableListOf()
    var error: Exception? = null
    fun reset() {
        this.error = null
        this.resume()
    }

    private fun resume() {
        while (!this.continuations.isEmpty()) {
            this.continuations.removeLast().resume(Unit)
        }
    }

    suspend fun done() {
        return suspendCoroutine { continuation ->
            this.continuations.add(continuation)
        }
    }

    fun pass(error: Exception? = null) {
        this.error = error
        this.resume()
    }

    fun deny(error: Exception) {
        this.error = error
        this.resume()
    }

}
