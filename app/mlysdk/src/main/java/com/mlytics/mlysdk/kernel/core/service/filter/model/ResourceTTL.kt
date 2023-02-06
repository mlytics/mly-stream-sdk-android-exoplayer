package com.mlytics.mlysdk.kernel.core.service.filter.model

import com.mlytics.mlysdk.kernel.core.const.service.ContentType
import com.mlytics.mlysdk.kernel.core.model.service.Resource

val resourceTTLPool: MutableMap<String, Long> = mutableMapOf(
    Pair(ContentType.HLS_M3U.rawValue, ResourceTTL.TWO_SECOND),
    Pair(ContentType.HLS_M3U_2.rawValue, ResourceTTL.TWO_SECOND),
    Pair(ContentType.HLS_M3U8.rawValue, ResourceTTL.TWO_SECOND),
    Pair(ContentType.HLS_M3U8_2.rawValue, ResourceTTL.TWO_SECOND),
    Pair(ContentType.HLS_TS.rawValue, ResourceTTL.TWO_HOUR)
)

object ResourceTTL {

    val ONE_DAY: Long = 24 * 60 * 60 * 1000

    val TWO_HOUR: Long = 2 * 60 * 60 * 1000

    val TWO_SECOND: Long = 2 * 1000
}

object ResourceTTLSuggester {

    val DEFAULT_RESOURCE_TTL: Long = ResourceTTL.TWO_HOUR

    fun give(resource: Resource): Long {
        val type = resource.type
        if (type != null) {
            val ttl = resourceTTLPool[type]
            if (ttl != null) {
                return ttl
            }

        }

        return ResourceTTLSuggester.DEFAULT_RESOURCE_TTL
    }

}
