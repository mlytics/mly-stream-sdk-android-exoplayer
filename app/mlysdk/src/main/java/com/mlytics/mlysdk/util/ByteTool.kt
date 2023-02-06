package com.mlytics.mlysdk.util

object ByteTool {

    private const val letters: String = "0123456789abcdefghijklmnopqrstuvwxyz"

    fun makeRandomBase36String(length: Int): String {
        var randomString = CharArray(length)
        for (i in 0 until length) {
            val rand = MathTool.random(length)
            var char = letters[rand]
            randomString[i] = char
        }
        return randomString.concatToString()
    }

}
