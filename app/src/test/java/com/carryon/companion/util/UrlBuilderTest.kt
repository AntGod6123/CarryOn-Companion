package com.carryon.companion.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UrlBuilderTest {
    @Test
    fun `build joins base path and query`() {
        val url = UrlBuilder.build(
            baseUrl = "http://10.0.0.2:9000/",
            path = "/api/watch/123",
            query = mapOf("profileId" to "abc", "t" to "token"),
        )

        assertEquals("http://10.0.0.2:9000/api/watch/123?profileId=abc&t=token", url)
    }

    @Test
    fun `append media token handles existing query`() {
        val url = UrlBuilder.appendMediaToken("http://10.0.0.2:9000/api/watch/123?profileId=abc", "token")

        assertEquals("http://10.0.0.2:9000/api/watch/123?profileId=abc&t=token", url)
    }
}
