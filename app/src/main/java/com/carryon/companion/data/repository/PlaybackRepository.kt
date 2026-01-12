package com.carryon.companion.data.repository

import com.carryon.companion.data.api.CarryOnApiService
import com.carryon.companion.data.dto.PlaybackSeekRequestDto
import com.carryon.companion.domain.models.PlaybackStatus

class PlaybackRepository(private val apiService: CarryOnApiService) {
    suspend fun getStatus(): PlaybackStatus = apiService.getPlaybackStatus().toDomain()

    suspend fun pause() {
        apiService.pausePlayback()
    }

    suspend fun resume() {
        apiService.resumePlayback()
    }

    suspend fun seek(positionMs: Long, mediaId: String? = null, profileId: String? = null, durationMs: Long? = null) {
        apiService.seek(
            PlaybackSeekRequestDto(
                positionMs = positionMs,
                mediaId = mediaId,
                profileId = profileId,
                durationMs = durationMs,
            ),
        )
    }

    suspend fun sendControl(action: String) {
        apiService.control(mapOf("action" to action))
    }
}
