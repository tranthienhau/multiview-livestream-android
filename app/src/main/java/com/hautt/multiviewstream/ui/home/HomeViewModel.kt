package com.hautt.multiviewstream.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hautt.multiviewstream.model.StreamLayout
import com.hautt.multiviewstream.model.StreamStatus
import com.hautt.multiviewstream.service.HLSStreamProvider
import com.hautt.multiviewstream.service.PerformanceMonitor
import com.hautt.multiviewstream.service.StreamPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val playerManager = StreamPlayerManager(application.applicationContext)
    val performanceMonitor = PerformanceMonitor()

    private val _currentLayout = MutableStateFlow(StreamLayout.GRID_2X2)
    val currentLayout: StateFlow<StreamLayout> = _currentLayout.asStateFlow()

    private val _primaryIndex = MutableStateFlow(0)
    val primaryIndex: StateFlow<Int> = _primaryIndex.asStateFlow()

    private val _showPerformanceOverlay = MutableStateFlow(false)
    val showPerformanceOverlay: StateFlow<Boolean> = _showPerformanceOverlay.asStateFlow()

    private val _showFullscreen = MutableStateFlow(false)
    val showFullscreen: StateFlow<Boolean> = _showFullscreen.asStateFlow()

    private val _fullscreenIndex = MutableStateFlow(0)
    val fullscreenIndex: StateFlow<Int> = _fullscreenIndex.asStateFlow()

    fun initialize() {
        playerManager.loadStreams(HLSStreamProvider())
        playerManager.startAllStreams()
    }

    fun setLayout(layout: StreamLayout) {
        _currentLayout.value = layout
    }

    fun togglePerformanceOverlay() {
        val newValue = !_showPerformanceOverlay.value
        _showPerformanceOverlay.value = newValue
        if (newValue) performanceMonitor.start() else performanceMonitor.stop()
    }

    fun handleTileTap(index: Int) {
        val sources = playerManager.sources.value
        if (index >= sources.size) return

        when (_currentLayout.value) {
            StreamLayout.GRID_2X2 -> {
                _primaryIndex.value = index
                _currentLayout.value = StreamLayout.PRIMARY_WITH_THUMBNAILS
                playerManager.promoteToPrimary(sources[index].id)
                playerManager.unmuteOnly(sources[index].id)
            }
            StreamLayout.PRIMARY_WITH_THUMBNAILS -> {
                if (index == _primaryIndex.value) {
                    _fullscreenIndex.value = index
                    _showFullscreen.value = true
                } else {
                    _primaryIndex.value = index
                    playerManager.promoteToPrimary(sources[index].id)
                    playerManager.unmuteOnly(sources[index].id)
                }
            }
            StreamLayout.SIDE_BY_SIDE -> {
                _fullscreenIndex.value = index
                _showFullscreen.value = true
            }
        }
    }

    fun dismissFullscreen() {
        _showFullscreen.value = false
    }

    fun cleanup() {
        playerManager.stopAll()
        performanceMonitor.stop()
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }
}
