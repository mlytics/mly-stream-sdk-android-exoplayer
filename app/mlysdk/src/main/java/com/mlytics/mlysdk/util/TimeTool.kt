package com.mlytics.mlysdk.util

import java.text.SimpleDateFormat
import java.util.*

object TimeTool {
    fun makeNowFstring(format: String): String {
        return SimpleDateFormat(format).format(Date())
    }

}
