package com.carryon.companion.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenShareResponseDto(
    val token: String,
    val name: String? = null,
)
