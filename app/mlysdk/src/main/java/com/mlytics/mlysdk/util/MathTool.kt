package com.mlytics.mlysdk.util

object MathTool {

    fun randomDouble(): Double {
        return Math.random()
    }

    fun random(n: Int): Int {
        return (randomDouble() * n).toInt()
    }
}
