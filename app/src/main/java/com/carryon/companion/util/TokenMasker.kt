package com.carryon.companion.util

object TokenMasker {
    fun mask(token: String): String {
        if (token.length <= 6) {
            return "******"
        }
        return token.take(6) + "…"
    }
}
