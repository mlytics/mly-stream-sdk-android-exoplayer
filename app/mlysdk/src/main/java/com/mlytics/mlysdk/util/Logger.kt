package com.mlytics.mlysdk.util

import android.util.Log

//import Foundation
//import Sentry
enum class LoggerLevel(val rawValue: Int) {
    TRACE(Log.VERBOSE), DEBUG(Log.DEBUG), INFO(Log.INFO), WARN(Log.WARN), ERROR(Log.ERROR), CRITICAL(
        Log.ASSERT
    )
}

interface LoggerHandler {
    var level: LoggerLevel

    fun log(level: LoggerLevel, message: String?, params: LogParams?, options: LogOptions?)
}

object LoggerConsole : LoggerHandler {
    override var level: LoggerLevel = LoggerLevel.DEBUG
    override fun log(
        level: LoggerLevel, message: String?, params: LogParams?, options: LogOptions?
    ) {
        if (level.rawValue < this.level.rawValue) {
            return
        }
        Log.println(level.rawValue, message, "${params ?: ""}, ${options ?: ""}")
    }
}

object Logger {
    var loggers: MutableList<LoggerHandler> = mutableListOf(LoggerConsole)

    fun trace(message: String? = null, params: LogParams? = null, options: LogOptions? = null) {
        log(LoggerLevel.TRACE, message, params, options)
    }

    fun track(message: String? = null, params: LogParams? = null, options: LogOptions? = null) {
        log(LoggerLevel.TRACE, message, params, options)
    }

    fun debug(message: String? = null, params: LogParams? = null, options: LogOptions? = null) {
        log(LoggerLevel.DEBUG, message, params, options)
    }

    fun info(message: String? = null, params: LogParams? = null, options: LogOptions? = null) {
        log(LoggerLevel.INFO, message, params, options)
    }

    fun warn(message: String? = null, params: LogParams? = null, options: LogOptions? = null) {
        log(LoggerLevel.WARN, message, params, options)
    }

    fun error(message: String? = null, params: LogParams? = null, options: LogOptions? = null) {
        log(LoggerLevel.ERROR, message, params, options)
    }

    fun critical(message: String? = null, params: LogParams? = null, options: LogOptions? = null) {
        log(LoggerLevel.CRITICAL, message, params, options)
    }

    fun log(
        level: LoggerLevel,
        message: String? = null,
        params: LogParams? = null,
        options: LogOptions? = null
    ) {

        for (logger in loggers) {
            logger.log(level, message, params, options)
        }

    }

    fun error(error: Exception) {
        error(null, null, LogOptions(null, error))
    }

    fun error(message: String, error: Exception) {
        error(message, null, LogOptions(null, error))
    }

}

object LoggerTool {

    val LOGGER_LEVELS =
        listOf<String>("", "TRACE", "TRACK", "INFO", "WARN", "DEBUG", "ERROR", "CRITICAL")

    fun levelString(level: LoggerLevel): String {
        return LOGGER_LEVELS[level.rawValue]
    }

}

typealias LogParams = MutableMap<String, Any>

data class LogOptions(
    var traceID: String? = null, var error: Exception? = null, var frame: Int? = null
) {
    var description: String = ""
        get() = error?.message ?: ""

}
