package com.mlytics.mlysdk.util

//import Foundation
class RegexTool {
    var reg: Regex

    constructor (reg: String) {
        this.reg = Regex(reg)
    }

    fun matches(string: String?): Boolean {
        val string = string
        if (string != null) else {
            return false
        }
        return this.reg.matches(string)
    }

    fun replace(string: String, map: Map<String, String>): String {
        return reg.replace(string) {
            map[it.value] ?: it.value
        }
    }

    fun replace(string: String, translate: (String) -> String): String {
        return reg.replace(string) {
            translate(it.value)
        }
    }
}
