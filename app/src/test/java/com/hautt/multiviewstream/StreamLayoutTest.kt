package com.hautt.multiviewstream

import androidx.compose.ui.geometry.Size
import com.hautt.multiviewstream.model.StreamLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamLayoutTest {

    private val testSize = Size(400f, 600f)

    // Grid 2x2

    @Test
    fun grid2x2_fourItems_returnsFourFrames() {
        val frames = StreamLayout.GRID_2X2.frames(testSize, 4, 0)
        assertEquals(4, frames.size)
        frames.forEach {
            assertTrue(it.width > 0)
            assertTrue(it.height > 0)
        }
    }

    @Test
    fun grid2x2_singleItem_fillsContainer() {
        val frames = StreamLayout.GRID_2X2.frames(testSize, 1, 0)
        assertEquals(1, frames.size)
        assertEquals(testSize.width, frames[0].width)
        assertEquals(testSize.height, frames[0].height)
    }

    @Test
    fun grid2x2_twoItems_sameRow() {
        val frames = StreamLayout.GRID_2X2.frames(testSize, 2, 0)
        assertEquals(2, frames.size)
        assertEquals(frames[0].y, frames[1].y)
    }

    // Primary with Thumbnails

    @Test
    fun primaryWithThumbnails_fourItems_primaryIsLargest() {
        val frames = StreamLayout.PRIMARY_WITH_THUMBNAILS.frames(testSize, 4, 0)
        assertEquals(4, frames.size)

        val primaryArea = frames[0].width * frames[0].height
        for (i in 1..3) {
            val thumbArea = frames[i].width * frames[i].height
            assertTrue(primaryArea > thumbArea)
        }
    }

    @Test
    fun primaryWithThumbnails_singleItem_fillsContainer() {
        val frames = StreamLayout.PRIMARY_WITH_THUMBNAILS.frames(testSize, 1, 0)
        assertEquals(1, frames.size)
        assertEquals(testSize.width, frames[0].width)
        assertEquals(testSize.height, frames[0].height)
    }

    @Test
    fun primaryWithThumbnails_differentPrimaryIndex() {
        val frames = StreamLayout.PRIMARY_WITH_THUMBNAILS.frames(testSize, 4, 2)
        assertEquals(4, frames.size)
        assertEquals(testSize.width, frames[2].width)
        assertTrue(frames[2].height > frames[0].height)
    }

    // Side by Side

    @Test
    fun sideBySide_twoItems_fullHeight() {
        val frames = StreamLayout.SIDE_BY_SIDE.frames(testSize, 2, 0)
        assertEquals(2, frames.size)
        assertEquals(testSize.height, frames[0].height)
        assertEquals(testSize.height, frames[1].height)
    }

    @Test
    fun sideBySide_extraStreamsHidden() {
        val frames = StreamLayout.SIDE_BY_SIDE.frames(testSize, 4, 0)
        assertEquals(4, frames.size)
        assertEquals(0f, frames[2].width)
        assertEquals(0f, frames[3].width)
    }

    // Edge cases

    @Test
    fun emptyCount_returnsEmpty() {
        StreamLayout.entries.forEach { layout ->
            val frames = layout.frames(testSize, 0, 0)
            assertTrue(frames.isEmpty())
        }
    }
}
