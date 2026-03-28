package dev.suresh.http

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.util.*
import kotlin.concurrent.atomics.AtomicBoolean

class CurlLoggingConfig {
  var logger: KLogger? = null
  var sanitizedHeaders = setOf(HttpHeaders.Authorization)
  var enabled = AtomicBoolean(true)
}

val CurlLogging =
    createClientPlugin("CurlLogging", ::CurlLoggingConfig) {
      val logger = pluginConfig.logger
      val sanitized = pluginConfig.sanitizedHeaders.map { it.lowercase() }.toSet()
      val enabled = pluginConfig.enabled

      on(SendingRequest) { request, content ->
        when {
          enabled.load() && logger?.isDebugEnabled() == true ->
              logger.debug { toCurl(request, content, sanitized) }
        }
      }
    }

private fun toCurl(
    request: HttpRequestBuilder,
    content: OutgoingContent,
    sanitized: Set<String>,
): String = buildString {
  append("curl")

  val method = request.method.value
  if (method != "GET") append(" -X $method")

  val headers = headers {
    appendAll(request.headers)
    content.contentType?.let { appendIfNameAbsent(HttpHeaders.ContentType, it.toString()) }
  }

  headers.forEach { name, values ->
    val value =
        when {
          name.lowercase() in sanitized -> "***"
          else -> values.joinToString(", ")
        }
    append(" -H '$name: $value'")
  }

  // Body — only in-memory content (TextContent, ByteArrayContent) is included.
  // Streaming bodies (WriteChannelContent, ReadChannelContent) are intentionally
  // skipped to avoid consuming the one-shot stream and breaking the actual request.
  when (content) {
    is TextContent -> append(" -d '${content.text}'")
    is ByteArrayContent -> append(" -d '${content.bytes().decodeToString()}'")
    else -> Unit
  }

  // Compressed flag
  if (request.headers.contains(HttpHeaders.AcceptEncoding)) {
    append(" --compressed")
  }

  append(" '${request.url.buildString()}'")
}
