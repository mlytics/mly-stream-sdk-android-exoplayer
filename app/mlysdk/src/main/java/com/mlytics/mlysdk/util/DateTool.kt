package com.mlytics.mlysdk.util

import java.text.SimpleDateFormat
import java.util.*

//import Foundation
class DateTool(
    var now: Long = System.currentTimeMillis(), var format: String = "yyyy-MM-dd HH:mm:ss"
) {
    companion object {

        fun millis(): Long {
            return DateTool().milli()
        }

        fun seconds(): Long {
            return DateTool().second()
        }

        fun now(): Double {
            return DateTool().secondDouble()
        }
    }

    fun milli(): Long {
        return now
    }

    fun second(): Long {
        return now / 1000
    }

    fun secondDouble(): Double {
        return now.toDouble() / 1000
    }

    val _format: SimpleDateFormat by lazy {
        SimpleDateFormat(format)
    }

    override fun toString(): String {
        return _format.format(Date(now))
    }

    fun reset() {
        now = System.currentTimeMillis()
    }

}