# Multi-View Live Stream Viewer (Android)

A native Android POC demonstrating multi-stream live video playback, inspired by Klic.gg. Built with Jetpack Compose + Media3 ExoPlayer.

## Features

- **Multi-stream playback** - 4 simultaneous HLS streams in a 2x2 grid
- **Dynamic layouts** - Grid (2x2), Primary + Thumbnails, Side-by-Side
- **Spring animations** - Smooth tile transitions between layouts
- **Tap-to-promote** - Tap any tile to make it the primary stream
- **Fullscreen mode** - Tap primary stream for immersive viewing
- **Adaptive bitrate** - 2 Mbps for primary, 500 kbps for thumbnails
- **Performance monitoring** - Real-time FPS (Choreographer) and memory overlay
- **Memory management** - Auto-pause streams when memory exceeds 400 MB

## Architecture

```
MVVM + Jetpack Compose

model/
  StreamSource        Stream metadata (URL, title)
  StreamLayout        Layout enum with geometry calculations
  StreamStatus        Player status enum

service/
  StreamProvider      Interface for stream source abstraction
  HLSStreamProvider   Apple HLS test stream URLs
  StreamPlayerManager Core: manages multiple ExoPlayer instances
  PerformanceMonitor  Choreographer FPS + Debug memory

ui/
  home/
    HomeScreen        Main Compose screen with grid + controls
    HomeViewModel     MVVM ViewModel with state flows
  grid/
    MultiStreamGridView  Compose layout with animated positioning
  components/
    VideoPlayerView      AndroidView wrapping ExoPlayer SurfaceView
    StreamTileView       Video tile with overlay (title, LIVE badge)
    PerformanceOverlayView  Color-coded FPS + memory display
  fullscreen/
    FullscreenPlayerScreen  Immersive single-stream viewer
```

## Requirements

- Android SDK 26+ (Android 8.0)
- Android Studio Hedgehog or later
- Kotlin 1.9+

## Build & Run

```bash
# Open in Android Studio
open -a "Android Studio" poc_next/multiview-livestream-android

# Or build from command line
cd poc_next/multiview-livestream-android
./gradlew assembleDebug

# Run unit tests
./gradlew test
```

## No Setup Required

- Uses free public Apple HLS test streams
- No API keys or accounts needed
- Just build and run on emulator or device

## Test Streams

| Stream | Context |
|--------|---------|
| Main Stage | Primary concert view |
| Backstage Cam | Behind the scenes |
| Fan Cam | Crowd perspective |
| Interview Room | Artist interviews |

## Key Dependencies

| Library | Purpose |
|---------|---------|
| Jetpack Compose | Declarative UI |
| Media3 ExoPlayer | HLS video playback |
| Material3 | Dark theme, icons |
| Kotlin Coroutines | Async state management |
