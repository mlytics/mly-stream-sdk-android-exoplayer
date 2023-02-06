package com.mlytics.mlysdk.util

//import Foundation
class ValidTool {

    var required: Boolean = false
    var reg: RegexTool? = null

    constructor (reg: String?, required: Boolean = false) {
        val reg = reg
        if (reg != null) {
            this.reg = RegexTool(reg)
        }

    }

    fun valid(v: String?): Boolean {
        if (v == null) {
            return required
        }

        val reg = this.reg
        if (reg != null) else {
            return true
        }

        return reg.matches(v)
    }

}
