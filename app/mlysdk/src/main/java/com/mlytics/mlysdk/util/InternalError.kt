package com.mlytics.mlysdk.util

class MInternalError(
    val code: MessageCodeObject,
    val error: Error? = null,
    var params: MutableMap<String, Any>? = null
) : Exception() {
    override var message: String = ""
        get() {
            return "<${this::class.simpleName}| ${this.code.logContent}> caused from: ${this.error?.message ?: ""}"
        }

}
