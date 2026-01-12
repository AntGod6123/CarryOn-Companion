package com.carryon.companion.util

import org.json.JSONObject

object StreamResolver {
    enum class Source {
        Json,
        Html,
    }

    data class Result(
        val url: String,
        val source: Source,
    )

    fun resolveFromJson(jsonBody: String): Result? {
        val json = JSONObject(jsonBody)
        val candidates = listOf(
            json.optString("streamUrl"),
            json.optString("playUrl"),
            json.optString("src"),
            json.optString("url"),
        ).filter { it.isNotBlank() }
        val match = candidates.firstOrNull { looksLikeStreamUrl(it) }
        return match?.let { Result(it, Source.Json) }
    }

    fun resolveFromHtml(html: String): Result? {
        val urlPatterns = listOf(
            "\\bhttps?://[^\"'\\s>]+\\.m3u8\\b",
            "\\bhttps?://[^\"'\\s>]+\\.mpd\\b",
            "\\bhttps?://[^\"'\\s>]+\\.mp4\\b",
        )
        val match = urlPatterns.asSequence()
            .mapNotNull { pattern -> Regex(pattern, RegexOption.IGNORE_CASE).find(html)?.value }
            .firstOrNull()

        if (match != null) {
            return Result(match, Source.Html)
        }

        val keyPatterns = listOf(
            "streamUrl",
            "playUrl",
            "src",
            "source",
        )
        for (key in keyPatterns) {
            val regex = Regex("$key\\s*[:=]\\s*['\"]([^'\"]+)['\"]", RegexOption.IGNORE_CASE)
            val candidate = regex.find(html)?.groupValues?.getOrNull(1)
            if (candidate != null && looksLikeStreamUrl(candidate)) {
                return Result(candidate, Source.Html)
            }
        }

        return null
    }

    private fun looksLikeStreamUrl(url: String): Boolean {
        return url.contains(".m3u8", ignoreCase = true) ||
            url.contains(".mpd", ignoreCase = true) ||
            url.contains(".mp4", ignoreCase = true) ||
            url.startsWith("http://") ||
            url.startsWith("https://")
    }
}
