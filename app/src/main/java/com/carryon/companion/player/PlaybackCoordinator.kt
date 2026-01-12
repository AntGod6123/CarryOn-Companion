package com.carryon.companion.player

import com.carryon.companion.data.repository.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaybackCoordinator(
    private val playbackRepository: PlaybackRepository,
    private val scope: CoroutineScope,
    private val mode: Mode = Mode.PlayerOfRecord,
) {
    enum class Mode {
        PlayerOfRecord,
        RemoteControlOnly,
    }

    private var pollingJob: Job? = null

    fun startPolling(intervalMs: Long = 2500L, onStatus: (PlaybackState) -> Unit) {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (true) {
                val status = playbackRepository.getStatus()
                onStatus(
                    PlaybackState(
                        isPlaying = status.isPlaying,
                        positionMs = status.positionMs,
                        durationMs = status.durationMs,
                        mediaId = status.mediaId,
                    ),
                )
                delay(intervalMs)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    suspend fun pause(localPause: suspend () -> Unit = {}) {
        if (mode == Mode.PlayerOfRecord) {
            localPause()
        }
        playbackRepository.pause()
    }

    suspend fun resume(localResume: suspend () -> Unit = {}) {
        if (mode == Mode.PlayerOfRecord) {
            localResume()
        }
        playbackRepository.resume()
    }

    suspend fun seek(positionMs: Long, localSeek: suspend () -> Unit = {}) {
        if (mode == Mode.PlayerOfRecord) {
            localSeek()
        }
        playbackRepository.seek(positionMs)
    }
}

data class PlaybackState(
    val isPlaying: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val mediaId: String?,
)
