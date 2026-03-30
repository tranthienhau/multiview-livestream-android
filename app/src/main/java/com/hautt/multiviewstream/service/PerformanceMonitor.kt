package com.hautt.multiviewstream.service

import android.os.Debug
import android.view.Choreographer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerformanceMonitor : Choreographer.FrameCallback {

    private val _fps = MutableStateFlow(0.0)
    val fps: StateFlow<Double> = _fps.asStateFlow()

    private val _memoryMB = MutableStateFlow(0.0)
    val memoryMB: StateFlow<Double> = _memoryMB.asStateFlow()

    private var lastFrameTimeNanos = 0L
    private var frameCount = 0
    private var isRunning = false

    fun start() {
        if (isRunning) return
        isRunning = true
        lastFrameTimeNanos = 0L
        frameCount = 0
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun stop() {
        isRunning = false
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) return

        if (lastFrameTimeNanos == 0L) {
            lastFrameTimeNanos = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
            return
        }

        frameCount++
        val elapsedSeconds = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000.0

        if (elapsedSeconds >= 1.0) {
            _fps.value = frameCount / elapsedSeconds
            frameCount = 0
            lastFrameTimeNanos = frameTimeNanos
            updateMemory()
        }

        Choreographer.getInstance().postFrameCallback(this)
    }

    private fun updateMemory() {
        val nativeHeap = Debug.getNativeHeapAllocatedSize()
        val runtime = Runtime.getRuntime()
        val javaHeap = runtime.totalMemory() - runtime.freeMemory()
        _memoryMB.value = (nativeHeap + javaHeap).toDouble() / (1024 * 1024)
    }
}
