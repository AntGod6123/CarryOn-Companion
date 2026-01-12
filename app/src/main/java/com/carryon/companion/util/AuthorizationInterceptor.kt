package com.carryon.companion.util

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(
    private val tokenProvider: () -> String?,
    private val remoteTokenProvider: () -> String? = { null },
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        tokenProvider()?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }
        remoteTokenProvider()?.let { token ->
            builder.header("x-remote-token", token)
        }
        return chain.proceed(builder.build())
    }
}
