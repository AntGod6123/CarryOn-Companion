package com.carryon.companion

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carryon.companion.data.api.CarryOnApiService
import com.carryon.companion.data.repository.PlaybackRepository
import com.carryon.companion.domain.models.PlaybackStatus
import com.carryon.companion.domain.usecases.TestConnectionUseCase
import com.carryon.companion.util.AuthorizationInterceptor
import com.carryon.companion.util.TokenMasker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CompanionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CompanionUiState())
    val uiState: StateFlow<CompanionUiState> = _uiState.asStateFlow()

    private var playbackRepository: PlaybackRepository? = null
    private var testConnectionUseCase: TestConnectionUseCase? = null

    fun updateBaseUrl(value: String) {
        _uiState.update { it.copy(baseUrl = value) }
    }

    fun updateBearerToken(value: String) {
        _uiState.update { it.copy(bearerToken = value) }
    }

    fun connect() {
        val baseUrl = uiState.value.baseUrl.trim()
        val bearerToken = uiState.value.bearerToken.trim()
        if (baseUrl.isBlank() || bearerToken.isBlank()) {
            _uiState.update {
                it.copy(
                    statusMessage = "Please provide both base URL and token.",
                    statusColor = Color(0xFFFFB74D),
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, statusMessage = null) }
        viewModelScope.launch {
            val api = createApiService(baseUrl) { bearerToken }
            val repository = PlaybackRepository(api)
            playbackRepository = repository
            testConnectionUseCase = TestConnectionUseCase(repository)
            val result = testConnectionUseCase?.execute()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    statusMessage = if (result?.isSuccess == true) {
                        "Connected to ${baseUrl.trimEnd('/')}"
                    } else {
                        val masked = TokenMasker.mask(bearerToken)
                        "Connection failed for ${baseUrl.trimEnd('/')} (token: $masked)."
                    },
                    statusColor = if (result?.isSuccess == true) {
                        Color(0xFF81C784)
                    } else {
                        Color(0xFFEF5350)
                    },
                )
            }
        }
    }

    fun refreshStatus() {
        val repository = playbackRepository ?: return
        viewModelScope.launch {
            runCatching { repository.getStatus() }
                .onSuccess { status ->
                    _uiState.update { it.copy(playbackStatus = status) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(statusMessage = "Failed to fetch playback status.", statusColor = Color(0xFFEF5350))
                    }
                }
        }
    }

    fun pausePlayback() {
        val repository = playbackRepository ?: return
        viewModelScope.launch {
            runCatching { repository.pause() }
                .onSuccess { refreshStatus() }
                .onFailure {
                    _uiState.update {
                        it.copy(statusMessage = "Pause failed.", statusColor = Color(0xFFEF5350))
                    }
                }
        }
    }

    fun resumePlayback() {
        val repository = playbackRepository ?: return
        viewModelScope.launch {
            runCatching { repository.resume() }
                .onSuccess { refreshStatus() }
                .onFailure {
                    _uiState.update {
                        it.copy(statusMessage = "Resume failed.", statusColor = Color(0xFFEF5350))
                    }
                }
        }
    }

    fun seekPlayback(positionMs: Long) {
        val repository = playbackRepository ?: return
        viewModelScope.launch {
            runCatching { repository.seek(positionMs) }
                .onSuccess { refreshStatus() }
                .onFailure {
                    _uiState.update {
                        it.copy(statusMessage = "Seek failed.", statusColor = Color(0xFFEF5350))
                    }
                }
        }
    }

    private fun createApiService(baseUrl: String, tokenProvider: () -> String): CarryOnApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(tokenProvider))
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl.trimEnd('/') + "/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(CarryOnApiService::class.java)
    }
}

data class CompanionUiState(
    val baseUrl: String = "",
    val bearerToken: String = "",
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val statusColor: Color = Color(0xFF81C784),
    val playbackStatus: PlaybackStatus? = null,
)
