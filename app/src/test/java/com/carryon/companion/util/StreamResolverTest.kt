package com.carryon.companion.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class StreamResolverTest {
    @Test
    fun `resolves stream url from json`() {
        val json = """
            {
              "streamUrl": "http://10.0.0.2:9000/streams/movie.m3u8"
            }
        """.trimIndent()

        val result = StreamResolver.resolveFromJson(json)

        assertNotNull(result)
        assertEquals("http://10.0.0.2:9000/streams/movie.m3u8", result?.url)
        assertEquals(StreamResolver.Source.Json, result?.source)
    }

    @Test
    fun `resolves stream url from html fixture`() {
        val html = javaClass.getResource("/streamresolver/watch_with_streamurl.html")!!.readText()

        val result = StreamResolver.resolveFromHtml(html)

        assertNotNull(result)
        assertEquals("http://10.0.0.2:9000/streams/show.m3u8", result?.url)
        assertEquals(StreamResolver.Source.Html, result?.source)
    }

    @Test
    fun `returns null when no url found`() {
        val html = "<html><body>No stream</body></html>"

        val result = StreamResolver.resolveFromHtml(html)

        assertNull(result)
    }
}
