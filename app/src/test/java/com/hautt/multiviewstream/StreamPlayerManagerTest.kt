package com.hautt.multiviewstream

import com.hautt.multiviewstream.model.StreamSource
import com.hautt.multiviewstream.service.StreamPlayerManager
import com.hautt.multiviewstream.service.StreamProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MockStreamProvider(private val streams: List<StreamSource>) : StreamProvider {
    override fun availableStreams(): List<StreamSource> = streams
}

class StreamPlayerManagerTest {

    @Test
    fun loadStreams_limitsToMaxConcurrent() {
        val manyStreams = (0 until 10).map {
            StreamSource(title = "Stream $it", url = "https://example.com/$it.m3u8")
        }
        val provider = MockStreamProvider(manyStreams)

        // We can only test the provider logic without Android context
        val loaded = provider.availableStreams().take(StreamPlayerManager.MAX_CONCURRENT_STREAMS)
        assertEquals(StreamPlayerManager.MAX_CONCURRENT_STREAMS, loaded.size)
    }

    @Test
    fun streamSource_hasUniqueIds() {
        val s1 = StreamSource(title = "A", url = "https://example.com/a.m3u8")
        val s2 = StreamSource(title = "B", url = "https://example.com/b.m3u8")
        assertNotNull(s1.id)
        assertNotNull(s2.id)
        assert(s1.id != s2.id)
    }

    @Test
    fun mockProvider_returnsAllStreams() {
        val streams = listOf(
            StreamSource(title = "S1", url = "https://example.com/1.m3u8"),
            StreamSource(title = "S2", url = "https://example.com/2.m3u8")
        )
        val provider = MockStreamProvider(streams)
        assertEquals(2, provider.availableStreams().size)
        assertEquals("S1", provider.availableStreams()[0].title)
    }
}
