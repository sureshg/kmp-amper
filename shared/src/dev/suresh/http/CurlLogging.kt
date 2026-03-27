package dev.suresh.http

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
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

      onRequest { request, _ ->
        when {
          enabled.load() && logger?.isDebugEnabled() == true ->
              logger.debug { buildCurlCommand(request, sanitized) }
        }
      }
    }

private fun buildCurlCommand(request: HttpRequestBuilder, sanitized: Set<String>): String =
    buildString {
      append("curl")

      val method = request.method.value
      if (method != "GET") append(" -X $method")

      // Merge request headers with content-type from body if present
      val headers = headers {
        appendAll(request.headers)
        (request.body as? OutgoingContent)?.contentType?.let {
          appendIfNameAbsent(HttpHeaders.ContentType, it.toString())
        }
      }

      headers.forEach { name, values ->
        val value = if (name.lowercase() in sanitized) "***" else values.joinToString(", ")
        append(" -H '$name: $value'")
      }

      // Body — only in-memory content (TextContent, ByteArrayContent) is included.
      // Streaming bodies (WriteChannelContent, ReadChannelContent) are intentionally
      // skipped to avoid consuming the one-shot stream and breaking the actual request.
      when (val body = request.body) {
        is TextContent -> append(" -d '${body.text}'")
        is ByteArrayContent -> append(" -d '${body.bytes().decodeToString()}'")
        else -> {}
      }

      // Compressed flag
      if (request.headers.contains(HttpHeaders.AcceptEncoding)) {
        append(" --compressed")
      }

      append(" '${request.url.buildString()}'")
    }
