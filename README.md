# harmony Music Player

A high-performance Android music player built with Kotlin and Jetpack Compose, designed for audio integrity and visual elegance.

## Features

- **Gapless Playback**: Zero-latency transitions between tracks using Media3 ExoPlayer
- **High-Resolution Audio**: Support for 24-bit/192kHz FLAC, ALAC, and WAV
- **Fast Library Management**: Handles 4,000+ tracks with Room FTS5 search
- **Material You Design**: Dynamic color integration with system themes
- **Bluetooth Optimization**: LDAC codec support at highest bitrate
- **Performance**: 120Hz scrolling, <500ms cold start, <100MB memory usage

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Media Engine**: Jetpack Media3 (ExoPlayer)
- **Database**: Room with FTS5
- **Async**: Coroutines + Flow
- **Image Loading**: Coil with hardware bitmaps
- **Pagination**: Paging 3

## Requirements

- Android SDK 24+
- Gradle 8.5+
- JDK 17

## Building

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run lint checks
./gradlew lint

# Format code
./gradlew ktlintFormat

# Clean build artifacts
./gradlew clean
```

## Development

This project uses a proxy for network access. If you encounter issues with dependency downloads, ensure your proxy settings match the configured values in `gradle.properties`.

### Running Tests

```bash
# Run specific test class
./gradlew test --tests "com.harmony.player.TestClassName"

# Run specific test method
./gradlew test --tests "com.harmony.player.TestClassName.testMethodName"
```

## Architecture

The app follows MVVM pattern with:
- **MediaPlaybackService**: Background playback using MediaSessionService
- **Repository Pattern**: Data access layer with Room + MediaStore sync
- **Compose UI**: Reactive state management with StateFlow

## License

[Add your license here]
