package com.mlytics.mlysdk.kernel.core.base

enum class BaseComponentName(val rawValue: String) {
    SYSTEM("system"), METRICS("metrics"), REPORT("report"), FILER("filer")
}

object BaseComponentBundle {
    val REQUIRED = listOf<BaseComponentName>(
        BaseComponentName.SYSTEM,
        BaseComponentName.METRICS,
        BaseComponentName.REPORT,
        BaseComponentName.FILER
    )
}
