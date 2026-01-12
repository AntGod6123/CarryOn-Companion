package com.carryon.companion.util

sealed interface AppError {
    val message: String
}

sealed class NetworkError(override val message: String) : AppError {
    data class Timeout(override val message: String = "Request timed out") : NetworkError(message)
    data class Dns(override val message: String = "DNS resolution failed") : NetworkError(message)
    data class Unreachable(override val message: String = "Server unreachable") : NetworkError(message)
}

data class AuthError(override val message: String = "Unauthorized") : AppError

data class ParseError(override val message: String = "Failed to parse response") : AppError

data class ServerError(override val message: String = "Server error") : AppError

data class UnknownError(override val message: String = "Unknown error") : AppError
