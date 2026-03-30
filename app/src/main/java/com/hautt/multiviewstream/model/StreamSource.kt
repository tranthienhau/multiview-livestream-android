package com.hautt.multiviewstream.model

import java.util.UUID

data class StreamSource(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val description: String = ""
)
