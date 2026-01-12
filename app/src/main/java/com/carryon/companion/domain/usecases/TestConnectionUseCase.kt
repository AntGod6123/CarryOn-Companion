package com.carryon.companion.domain.usecases

import com.carryon.companion.data.repository.PlaybackRepository
import com.carryon.companion.util.AppError
import com.carryon.companion.util.AuthError
import com.carryon.companion.util.NetworkError
import com.carryon.companion.util.ServerError
import com.carryon.companion.util.UnknownError
import retrofit2.HttpException
import java.io.IOException

class TestConnectionUseCase(private val playbackRepository: PlaybackRepository) {
    suspend fun execute(): Result<Unit> {
        return try {
            playbackRepository.getStatus()
            Result.success(Unit)
        } catch (error: Throwable) {
            Result.failure(mapError(error))
        }
    }

    private fun mapError(error: Throwable): AppError {
        return when (error) {
            is HttpException -> when (error.code()) {
                401, 403 -> AuthError()
                in 500..599 -> ServerError("Server error ${error.code()}")
                else -> UnknownError("HTTP ${error.code()}")
            }
            is IOException -> NetworkError.Unreachable(error.message ?: "Network error")
            else -> UnknownError(error.message ?: "Unknown error")
        }
    }
}
