package  com.mlytics.mlysdk.util

open class Requester(var host: String = "", var timeout: Double = 30.0) {

    init {
        if (!this.host.isEmpty() && !this.host.startsWith("http")) {
            this.host = "https://${this.host}"
        }
    }

    suspend inline fun <reified T> fetch(
        path: String,
        queries: Map<String, Any?>? = null,
        headers: Map<String, String?>? = null,
        aborter: AbortController? = null,
        timeout: Double? = null
    ): T? {
        var url = "${host}${path}"
        val task = PrepareCall(url, queries, headers)
        aborter?.task = task
        return task.await().toObject()
    }

}
