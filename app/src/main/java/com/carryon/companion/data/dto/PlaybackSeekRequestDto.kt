package com.carryon.companion.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaybackSeekRequestDto(
    val positionMs: Long,
    val mediaId: String? = null,
    val profileId: String? = null,
    val durationMs: Long? = null,
    val isFinished: Boolean? = null,
)
