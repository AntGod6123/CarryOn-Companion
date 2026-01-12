package com.carryon.companion.domain.models

data class MediaItem(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val durationMs: Long?,
    val progressMs: Long?,
    val type: String,
    val year: String? = null,
    val overview: String? = null,
    val rating: String? = null,
)
