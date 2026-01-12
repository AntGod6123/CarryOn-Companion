package com.carryon.companion.util

import java.net.URI
import java.net.URLEncoder

object UrlBuilder {
    fun build(baseUrl: String, path: String, query: Map<String, String> = emptyMap()): String {
        val normalizedBase = baseUrl.trimEnd('/')
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val uri = URI("$normalizedBase$normalizedPath")
        if (query.isEmpty()) {
            return uri.toString()
        }
        val encodedQuery = query.entries.joinToString("&") { (key, value) ->
            "${encode(key)}=${encode(value)}"
        }
        return URI(
            uri.scheme,
            uri.authority,
            uri.path,
            encodedQuery,
            uri.fragment,
        ).toString()
    }

    fun appendMediaToken(url: String, mediaToken: String): String {
        val separator = if (url.contains("?")) "&" else "?"
        return "$url${separator}t=${encode(mediaToken)}"
    }

    private fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())
}
