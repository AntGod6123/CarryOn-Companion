package com.carryon.companion.domain.models

data class PlaybackStatus(
    val isPlaying: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val mediaId: String?,
    val controlsEnabled: Boolean,
)
