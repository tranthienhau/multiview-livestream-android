package com.hautt.multiviewstream.ui.grid

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.hautt.multiviewstream.model.StreamLayout
import com.hautt.multiviewstream.model.StreamSource
import com.hautt.multiviewstream.model.StreamStatus
import com.hautt.multiviewstream.ui.components.StreamTileView

@Composable
fun MultiStreamGridView(
    sources: List<StreamSource>,
    players: Map<String, ExoPlayer>,
    statuses: Map<String, StreamStatus>,
    layout: StreamLayout,
    primaryIndex: Int,
    onTileTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize()) {
        var containerSize = Size.Zero

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { intSize ->
                    containerSize = Size(intSize.width.toFloat(), intSize.height.toFloat())
                }
        ) {
            if (containerSize.width > 0 && containerSize.height > 0) {
                val frames = layout.frames(containerSize, sources.size, primaryIndex)

                sources.forEachIndexed { index, source ->
                    if (index < frames.size) {
                        val frame = frames[index]
                        if (frame.width > 0 && frame.height > 0) {
                            val animX by animateFloatAsState(
                                targetValue = frame.x,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "x_$index"
                            )
                            val animY by animateFloatAsState(
                                targetValue = frame.y,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "y_$index"
                            )
                            val animW by animateFloatAsState(
                                targetValue = frame.width,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "w_$index"
                            )
                            val animH by animateFloatAsState(
                                targetValue = frame.height,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "h_$index"
                            )

                            StreamTileView(
                                source = source,
                                player = players[source.id],
                                status = statuses[source.id] ?: StreamStatus.IDLE,
                                isPrimary = index == primaryIndex && layout == StreamLayout.PRIMARY_WITH_THUMBNAILS,
                                onClick = { onTileTap(index) },
                                modifier = Modifier
                                    .offset { IntOffset(animX.toInt(), animY.toInt()) }
                                    .size(
                                        width = with(density) { animW.toDp() },
                                        height = with(density) { animH.toDp() }
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
