package com.mlytics.mlysdk.util

import java.security.MessageDigest
import java.util.*

//import Foundation
//import CommonCrypto
//import CryptoKit
object HashTool {

    private fun sha256(data: String?): ByteArray? {
        if (data == null) {
            return null
        }
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(data.toByteArray(Charsets.US_ASCII))
        return result
    }

    fun sha256base64(string: String?): String? {
        if (string == null) {
            return null
        }
        val sha256 = sha256(string)
        val base64 = Base64.getEncoder().encode(sha256)
        var result = base64?.toString(Charsets.US_ASCII)
        return result
    }

    fun sha256base64url(string: String?): String? {
        val to = sha256base64(string)
        Logger.debug("sha256a ${to}")
        val result = base64url(to)
        Logger.debug("sha256b ${result}")
        return result
    }

    val URL_REG = RegexTool("[+/=]{1}")
    val URL_MAP = mapOf(Pair("/", "_"), Pair("+", "-"), Pair("=", ""))
    fun base64url(string: String?): String? {
        if (string == null) {
            return null
        }
        return URL_REG.replace(string, URL_MAP)
    }

    private val base16 = "0123456789abcdef".toCharArray()
    fun base16(data: ByteArray?): String? {
        return StringBuilder().apply {
            data?.forEach {
                var i = it.toInt() and 0xFF
                val a = i and 0xF0 ushr 0x04 and 0x0F
                var b = i and 0x0F
                append(base16[a])
                append(base16[b])
            }
        }.toString()
    }

    fun sha256base16(string: String?): String? {
        val to = sha256(string)
        val result = base16(to)
        return result
    }
}