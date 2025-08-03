package dev.suresh.http

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.engine.curl.*

actual fun httpClient(
    name: String,
    timeout: Timeout,
    retry: Retry,
    httpLogger: KLogger,
    config: HttpClientConfigurer
) =
    HttpClient(Curl) {
      config(this)
      engine { sslVerify = true }
    }
