# harmony Music Player

A high-performance Android music player built with Kotlin and Jetpack Compose, designed for audio integrity and visual elegance.

## Features

- **Gapless Playback**: Zero-latency transitions between tracks using Media3 ExoPlayer
- **Wide Audio Format Support**: All common formats (MP3, AAC, FLAC, ALAC, WAV, OGG, OPUS, M4A, WMA)
- **Fast Library Management**: Handles 10k+ tracks with Room FTS5 search
- **Material You Design**: Dynamic color integration with system themes
- **Playlist Support**: Create, edit, delete, export/import (M3U format)
- **Persistent Mini-Player**: Always-visible bottom bar on all screens
- **Smart Search**: Albums, artists, and tracks with instant results
- **Bluetooth Pause**: Automatically pauses on disconnect
- **Performance**: Smooth scrolling, optimized cold start, efficient memory usage

## Tech Stack

- **Language**: Kotlin 2.1.0
- **UI Framework**: Jetpack Compose (Material 3 1.2.1)
 - **Media Engine**: Jetpack Media3 1.4.0
 - **Database**: Room 2.6.1 with FTS5
 - **Settings**: DataStore 1.1.2
 - **App Startup**: Startup Runtime 1.2.0
 - **Dependency Injection**: Hilt 2.51.1
 - **Async**: Coroutines + Flow
 - **Image Loading**: Coil 3.3.0
 - **Background Sync**: WorkManager 2.9.0
 - **Navigation**: Navigation Compose 2.8.4
 - **Build System**: Gradle 8.13, AGP 8.13.2

## Requirements

- **minSdk**: 33 (Android 13)
- **targetSdk**: 35 (Android 15)
- **Java**: 17 (auto-detected from system)

## Development Environment

This project uses stable versions and has been tested to build successfully. The development environment includes:

- **Java 17**: Required for Android development, auto-detected from `/usr/lib/jvm/java-17-openjdk/`
- **Android SDK**: API level 35 (Android 15) available at `/home/najdu/android-sdk`
- **Gradle 8.13**: Build system configured and working
- **Network proxy**: Configured in `gradle.properties` for dependency downloads

### First-Time Setup

If you're setting up a fresh environment:

1. Ensure Java 17 is installed (Arch Linux: `sudo pacman -S jdk17-openjdk`)
2. Configure Android SDK path (already set in this project)
3. Install Android Studio (optional, for IDE development)

## Building

```bash
# Build project
./gradlew build

# Run all tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run lint checks
./gradlew lint

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
- **Hilt**: Dependency injection throughout app
- **LazyColumn**: Efficient loading of large music libraries with keys

## License

This project is licensed under the GNU General Public License v3.0. See [LICENSE](LICENSE) for the full text.
