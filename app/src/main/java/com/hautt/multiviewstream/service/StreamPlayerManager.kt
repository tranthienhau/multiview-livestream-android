package com.hautt.multiviewstream.service

import android.content.Context
import android.os.Debug
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import com.hautt.multiviewstream.model.StreamSource
import com.hautt.multiviewstream.model.StreamStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@OptIn(UnstableApi::class)
class StreamPlayerManager(private val context: Context) {

    companion object {
        const val MAX_CONCURRENT_STREAMS = 4
        const val THUMBNAIL_BITRATE = 500_000   // 500 kbps
        const val PRIMARY_BITRATE = 2_000_000   // 2 Mbps
        const val THUMBNAIL_BUFFER_MS = 2_000   // 2 seconds
        const val MEMORY_WARNING_THRESHOLD = 400L * 1024 * 1024  // 400 MB
    }

    private val _players = mutableMapOf<String, ExoPlayer>()
    val players: Map<String, ExoPlayer> get() = _players

    private val _statuses = MutableStateFlow<Map<String, StreamStatus>>(emptyMap())
    val statuses: StateFlow<Map<String, StreamStatus>> = _statuses.asStateFlow()

    private val _sources = MutableStateFlow<List<StreamSource>>(emptyList())
    val sources: StateFlow<List<StreamSource>> = _sources.asStateFlow()

    private val _primaryStreamId = MutableStateFlow<String?>(null)
    val primaryStreamId: StateFlow<String?> = _primaryStreamId.asStateFlow()

    fun loadStreams(provider: StreamProvider) {
        val streams = provider.availableStreams().take(MAX_CONCURRENT_STREAMS)
        _sources.value = streams
        _primaryStreamId.value = streams.firstOrNull()?.id
        _statuses.value = streams.associate { it.id to StreamStatus.IDLE }
    }

    fun startStream(source: StreamSource, isPrimary: Boolean = false) {
        if (_players.containsKey(source.id)) return

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                if (isPrimary) DefaultLoadControl.DEFAULT_MIN_BUFFER_MS else THUMBNAIL_BUFFER_MS,
                if (isPrimary) DefaultLoadControl.DEFAULT_MAX_BUFFER_MS else THUMBNAIL_BUFFER_MS * 2,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()

        val player = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()

        player.volume = 0f
        player.repeatMode = Player.REPEAT_MODE_ALL

        val trackParams = player.trackSelectionParameters.buildUpon()
            .setMaxVideoBitrate(if (isPrimary) PRIMARY_BITRATE else THUMBNAIL_BITRATE)
            .setMaxVideoSize(
                if (isPrimary) Int.MAX_VALUE else 640,
                if (isPrimary) Int.MAX_VALUE else 360
            )
            .build()
        player.trackSelectionParameters = trackParams

        player.setMediaItem(MediaItem.fromUri(source.url))

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                val status = when (state) {
                    Player.STATE_BUFFERING -> StreamStatus.BUFFERING
                    Player.STATE_READY -> if (player.isPlaying) StreamStatus.PLAYING else StreamStatus.PAUSED
                    Player.STATE_ENDED -> StreamStatus.PAUSED
                    Player.STATE_IDLE -> StreamStatus.IDLE
                    else -> StreamStatus.IDLE
                }
                updateStatus(source.id, status)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateStatus(source.id, if (isPlaying) StreamStatus.PLAYING else StreamStatus.PAUSED)
            }

            override fun onPlayerError(error: PlaybackException) {
                updateStatus(source.id, StreamStatus.ERROR)
            }
        })

        updateStatus(source.id, StreamStatus.LOADING)
        player.prepare()
        player.play()

        _players[source.id] = player
    }

    fun stopStream(id: String) {
        _players[id]?.release()
        _players.remove(id)
        updateStatus(id, StreamStatus.IDLE)
    }

    fun stopAll() {
        _players.values.forEach { it.release() }
        _players.clear()
        _sources.value = emptyList()
        _statuses.value = emptyMap()
    }

    fun startAllStreams() {
        val primary = _primaryStreamId.value
        _sources.value.forEach { source ->
            val isPrimary = source.id == primary
            startStream(source, isPrimary)
            _players[source.id]?.volume = if (isPrimary) 1f else 0f
        }
    }

    fun promoteToPrimary(id: String) {
        _primaryStreamId.value = id

        _sources.value.forEach { source ->
            val player = _players[source.id] ?: return@forEach
            val isPrimary = source.id == id

            player.volume = if (isPrimary) 1f else 0f

            val trackParams = player.trackSelectionParameters.buildUpon()
                .setMaxVideoBitrate(if (isPrimary) PRIMARY_BITRATE else THUMBNAIL_BITRATE)
                .setMaxVideoSize(
                    if (isPrimary) Int.MAX_VALUE else 640,
                    if (isPrimary) Int.MAX_VALUE else 360
                )
                .build()
            player.trackSelectionParameters = trackParams
        }
    }

    fun unmuteOnly(id: String) {
        _players.forEach { (streamId, player) ->
            player.volume = if (streamId == id) 1f else 0f
        }
    }

    fun currentMemoryUsageMB(): Double {
        val nativeHeap = Debug.getNativeHeapAllocatedSize()
        val runtime = Runtime.getRuntime()
        val javaHeap = runtime.totalMemory() - runtime.freeMemory()
        return (nativeHeap + javaHeap).toDouble() / (1024 * 1024)
    }

    fun enforceMemoryLimit() {
        val usageBytes = (currentMemoryUsageMB() * 1024 * 1024).toLong()
        if (usageBytes <= MEMORY_WARNING_THRESHOLD) return

        val primary = _primaryStreamId.value
        _sources.value.lastOrNull { it.id != primary }?.let { source ->
            _players[source.id]?.pause()
            updateStatus(source.id, StreamStatus.PAUSED)
        }
    }

    private fun updateStatus(id: String, status: StreamStatus) {
        _statuses.update { current -> current + (id to status) }
    }
}
