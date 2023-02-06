package com.mlytics.mlysdk.kernel.core.const.service

import java.net.URL

enum class ContentType(val rawValue: String) {
    XML("application/xml"),
    JSON("application/json"),
    BYTE("application/octet-stream"),
    TEXT("text/plain"),
    FILE("multipart/form-data"),
    FORM("application/x-www-form-urlencoded"),
    HTML("text/html"),
    WEBVTT("text/vtt"),
    HLS_M3U("audio/mpegurl"),
    HLS_M3U_2("audio/x-mpegurl"),
    HLS_M3U8("application/vnd.apple.mpegurl"),
    HLS_M3U8_2("application/x-mpegurl"),
    HLS_TS("video/mp2t")
}

object URLContentType {
    fun from(url: URL): ContentType {
        return if (url.path.contains(".m3u8")) {
            ContentType.HLS_M3U8_2
        } else {
            ContentType.HLS_TS
        }
    }
}