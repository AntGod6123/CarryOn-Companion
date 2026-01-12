package com.carryon.companion.data.api

import com.carryon.companion.data.dto.PlaybackSeekRequestDto
import com.carryon.companion.data.dto.PlaybackStatusDto
import com.carryon.companion.data.dto.TokenShareResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CarryOnApiService {
    @GET("/api/server/token/share")
    suspend fun requestTokenShare(): TokenShareResponseDto

    @GET("/api/playback/status")
    suspend fun getPlaybackStatus(): PlaybackStatusDto

    @POST("/api/playback/pause")
    suspend fun pausePlayback()

    @POST("/api/playback/resume")
    suspend fun resumePlayback()

    @POST("/api/playback/seek")
    suspend fun seek(@Body request: PlaybackSeekRequestDto)

    @POST("/api/control")
    suspend fun control(@Body request: Map<String, String>)

    @GET("/api/watch/{mediaId}")
    suspend fun getWatchPage(@Path("mediaId") mediaId: String): String

    @GET("/api/livetv/channels")
    suspend fun getLiveTvChannels(): List<Map<String, Any?>>
}
