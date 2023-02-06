package com.mlytics.mlysdk.util

import java.net.URL

object URLTool

fun URL.portString(): String {
    return if (this.port == -1) "" else ":" + this.port
}

