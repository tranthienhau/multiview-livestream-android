package com.hautt.multiviewstream.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.hautt.multiviewstream.model.StreamSource
import com.hautt.multiviewstream.model.StreamStatus

@Composable
fun StreamTileView(
    source: StreamSource,
    player: ExoPlayer?,
    status: StreamStatus,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (isPrimary) 12.dp else 8.dp))
            .background(Color.Black)
            .clickable(onClick = onClick)
    ) {
        // Video layer
        if (player != null) {
            VideoPlayerView(
                player = player,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                StatusBadge(status = status)

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = source.title,
                    color = Color.White,
                    fontSize = if (isPrimary) 14.sp else 10.sp,
                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isPrimary) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Audio",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: StreamStatus) {
    when (status) {
        StreamStatus.PLAYING -> {
            Row(
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0.85f), RoundedCornerShape(50))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        StreamStatus.LOADING, StreamStatus.BUFFERING -> {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 1.5.dp,
                color = Color.White
            )
        }
        StreamStatus.ERROR -> {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color.Yellow,
                modifier = Modifier.size(14.dp)
            )
        }
        StreamStatus.PAUSED -> {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Paused",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
        }
        StreamStatus.IDLE -> {}
    }
}
