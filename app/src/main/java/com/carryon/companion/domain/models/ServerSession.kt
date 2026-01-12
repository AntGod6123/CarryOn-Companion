package com.carryon.companion.domain.models

data class ServerSession(
    val baseUrl: String,
    val bearerToken: String,
    val serverName: String? = null,
    val lastConnectedAt: Long? = null,
    val lastProfileId: String? = null,
)
