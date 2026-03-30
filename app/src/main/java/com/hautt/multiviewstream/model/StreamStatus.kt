package com.hautt.multiviewstream.model

enum class StreamStatus(val displayText: String) {
    IDLE("Idle"),
    LOADING("Loading..."),
    BUFFERING("Buffering..."),
    PLAYING("Live"),
    PAUSED("Paused"),
    ERROR("Error");

    val isActive: Boolean
        get() = this == PLAYING || this == BUFFERING
}
