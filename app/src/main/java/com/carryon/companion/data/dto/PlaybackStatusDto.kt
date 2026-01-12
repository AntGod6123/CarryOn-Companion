package com.carryon.companion.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaybackStatusDto(
    val isPlaying: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val mediaId: String? = null,
    val controlsEnabled: Boolean = true,
)
