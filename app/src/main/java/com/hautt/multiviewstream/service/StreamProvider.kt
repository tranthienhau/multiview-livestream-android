package com.hautt.multiviewstream.service

import com.hautt.multiviewstream.model.StreamSource

interface StreamProvider {
    fun availableStreams(): List<StreamSource>
}

class HLSStreamProvider : StreamProvider {
    override fun availableStreams(): List<StreamSource> = listOf(
        StreamSource(
            title = "Main Stage",
            url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
            description = "Primary concert view"
        ),
        StreamSource(
            title = "Backstage Cam",
            url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_adv_example_hevc/master.m3u8",
            description = "Behind the scenes"
        ),
        StreamSource(
            title = "Fan Cam",
            url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/adv_dv_atmos/main.m3u8",
            description = "Crowd perspective"
        ),
        StreamSource(
            title = "Interview Room",
            url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8",
            description = "Artist interviews"
        )
    )
}
