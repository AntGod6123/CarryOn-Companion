package com.carryon.companion.data.repository

import com.carryon.companion.data.dto.PlaybackStatusDto
import com.carryon.companion.domain.models.PlaybackStatus

fun PlaybackStatusDto.toDomain(): PlaybackStatus = PlaybackStatus(
    isPlaying = isPlaying,
    positionMs = positionMs,
    durationMs = durationMs,
    mediaId = mediaId,
    controlsEnabled = controlsEnabled,
)
