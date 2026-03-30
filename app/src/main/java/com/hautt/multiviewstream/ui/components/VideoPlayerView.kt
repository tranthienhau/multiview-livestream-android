package com.hautt.multiviewstream.ui.components

import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            SurfaceView(context).also { surfaceView ->
                player.setVideoSurfaceView(surfaceView)
            }
        },
        update = { surfaceView ->
            player.setVideoSurfaceView(surfaceView)
        },
        modifier = modifier
    )
}
