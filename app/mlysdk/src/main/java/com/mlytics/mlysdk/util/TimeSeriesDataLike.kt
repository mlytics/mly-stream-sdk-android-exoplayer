package com.mlytics.mlysdk.util

class TimeSeriesDataLike {
    var ctime: Long

    constructor (ctime: Long = System.currentTimeMillis()) {
        this.ctime = ctime
    }

}
