package com.hautt.multiviewstream.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

enum class StreamLayout(val displayName: String, val iconName: String) {
    GRID_2X2("2x2 Grid", "grid_view"),
    PRIMARY_WITH_THUMBNAILS("Primary + Thumbnails", "view_agenda"),
    SIDE_BY_SIDE("Side by Side", "view_column");

    data class TileFrame(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float
    )

    fun frames(size: Size, count: Int, primaryIndex: Int): List<TileFrame> {
        if (count <= 0) return emptyList()
        val spacing = 4f

        return when (this) {
            GRID_2X2 -> grid2x2Frames(size, count, spacing)
            PRIMARY_WITH_THUMBNAILS -> primaryWithThumbnailsFrames(size, count, primaryIndex, spacing)
            SIDE_BY_SIDE -> sideBySideFrames(size, count, spacing)
        }
    }

    private fun grid2x2Frames(size: Size, count: Int, spacing: Float): List<TileFrame> {
        val cols = if (count <= 1) 1 else 2
        val rows = if (count <= 2) 1 else 2
        val tileW = (size.width - spacing * (cols - 1)) / cols
        val tileH = (size.height - spacing * (rows - 1)) / rows

        return (0 until count).map { i ->
            val col = i % 2
            val row = i / 2
            TileFrame(
                x = col * (tileW + spacing),
                y = row * (tileH + spacing),
                width = tileW,
                height = tileH
            )
        }
    }

    private fun primaryWithThumbnailsFrames(
        size: Size, count: Int, primaryIndex: Int, spacing: Float
    ): List<TileFrame> {
        if (count <= 1) {
            return listOf(TileFrame(0f, 0f, size.width, size.height))
        }

        val thumbnailCount = count - 1
        val thumbnailHeight = (size.height - spacing) * 0.25f
        val primaryHeight = size.height - thumbnailHeight - spacing
        val thumbnailWidth = (size.width - spacing * (thumbnailCount - 1)) / thumbnailCount

        val frames = MutableList(count) { TileFrame(0f, 0f, 0f, 0f) }

        frames[primaryIndex] = TileFrame(0f, 0f, size.width, primaryHeight)

        var thumbIdx = 0
        for (i in 0 until count) {
            if (i != primaryIndex) {
                frames[i] = TileFrame(
                    x = thumbIdx * (thumbnailWidth + spacing),
                    y = primaryHeight + spacing,
                    width = thumbnailWidth,
                    height = thumbnailHeight
                )
                thumbIdx++
            }
        }
        return frames
    }

    private fun sideBySideFrames(size: Size, count: Int, spacing: Float): List<TileFrame> {
        val effectiveCount = minOf(count, 2)
        val tileW = (size.width - spacing * (effectiveCount - 1)) / effectiveCount

        return (0 until count).map { i ->
            if (i < 2) {
                TileFrame(
                    x = i * (tileW + spacing),
                    y = 0f,
                    width = tileW,
                    height = size.height
                )
            } else {
                TileFrame(0f, 0f, 0f, 0f)
            }
        }
    }
}
