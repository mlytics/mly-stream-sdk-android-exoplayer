package com.mlytics.mlysdk.util

import com.google.gson.Gson

//import Foundation
object JSONTool {

    fun dumps(obj: Any): String? {
        return Gson().toJson(obj)
    }

}
