---

# Technical Specification: harmony Music Player

## 1. Vision & Core Philosophy

**harmony** is designed for listeners who value audio integrity, visual elegance, and instant responsiveness. It treats massive libraries as lightweight collections, ensuring hardware is dedicated to reproducing music perfectly.

* **Opinionated Defaults:** Gapless playback is mandatory; UI is "flat" for speed.
* **Performance:** 120Hz scrolling, <500ms cold start, and minimal battery footprint.
* **Aesthetic:** Pure Material 3 utilizing system-wide **Material You** dynamic coloring.

---

## 2. Technology Stack

| Component | Technology | Version | Purpose |
| --- | --- | --- | --- |
| **Language** | Kotlin | 2.2.20 | Modern, safe, and expressive |
| **UI Framework** | Jetpack Compose | BOM 2024.10.01 | Declarative UI for smooth transitions |
| **Material 3** | Compose Material3 | 1.2.1 | Material Design 3 components |
| **Media Library** | Media3 + ExoPlayer | 1.4.0 | Modern Android media handling |
| **Persistence** | Room DB + FTS5 | 2.6.1 | Metadata caching and lightning-fast search |
| **DI Framework** | Hilt | 2.51.1 | Dependency injection |
| **Coroutines** | kotlinx.coroutines | 1.8.0 | Asynchronous programming |
| **Image Loading** | Coil | 3.3.0 | Async image loading with hardware bitmaps |
| **Pagination** | Paging 3 | 3.3.1 | Efficient list loading |
| **Work Manager** | androidx.work | 2.9.0 | Background task scheduling |
| **Build System** | Gradle | 8.13 | Build automation |
| **AGP** | Android Gradle Plugin | 8.13.2 | Android-specific Gradle tasks |
| **KSP** | Kotlin Symbol Processing | 2.2.20-2.0.4 | Annotation processing |

---

## 3. Android Configuration

| Configuration | Value |
| --- | --- |
| **minSdk** | 33 (Android 13 Tiramisu) |
| **targetSdk** | 36 (Android 16 Baklava) |
| **compileSdk** | 36 (Android 16 Baklava) |
| **JVM Target** | Java 17 |
| **Namespace** | com.harmony.player |

---

## 4. Audio Engine (Media3 ExoPlayer Foundation)

The engine is built on **Jetpack Media3 (ExoPlayer) 1.9.0**.

### A. Playback & Focus

* **Gapless Playback:** Uses `ConcatenatingMediaSource2` for zero-latency transitions and pre-buffers the next track at 90% completion.
* **Audio Focus Logic:** 
  - **Ducking:** Automatically lowers volume during notifications.
  - **Focus Bypass:** A user-toggleable option to ignore focus loss, preventing other apps from interrupting the stream.
* **High-Resolution Support:** Direct `AudioTrack` routing for 24-bit/192kHz FLAC, ALAC, and WAV.

### B. Signal Processing

* **10-Band Equalizer:** A high-precision digital EQ integrated into the Media3 processing pipeline.
* **Buffer Management:** Custom `LoadControl` to keep large chunks in RAM, reducing disk spin-up.
* **MediaSessionService:** Background playback with Media3 session service integration.

---

## 5. Library & Data Management

Optimized for 4,000+ tracks using a reactive data pipeline.

### A. Data Architecture

* **Hybrid Media Store:** A **Room Database 2.7.2** mirrors `MediaStore`.
* **Room FTS5:** Implements Full-Text Search (FTS5) for sub-10ms "search-as-you-type" across the entire library.
* **Paging 3:** Songs and folders are loaded in blocks of 50 to maintain low memory overhead.

### B. Navigation & Sync

* **Folder-First View:** Prioritizes physical file hierarchy over metadata-only views.
* **File Watcher:** Uses **WorkManager 2.10.0** and `ContentObserver` to detect new files added via PC/file manager and sync them to the Room DB automatically.
* **Permissions:** Robust handling of `READ_MEDIA_AUDIO` for Android 13+ and partial media access.

---

## 6. UI/UX & Visual Identity

The UI follows the **Material 3 (M3) 1.5.0** spec with a focus on system integration.

### A. System-Driven Design

* **Material You:** The app uses system-wide dynamic color tokens (set by the user's wallpaper) for the entire UI palette.
* **Hardware Bitmaps:** Coil uses `Bitmap.Config.HARDWARE` to offload image rendering to the GPU.

### B. GPU-Accelerated Effects

* **Native Glassmorphism:** The "Now Playing" screen uses `Modifier.graphicsLayer { renderEffect = ... }` for GPU-accelerated blurs (Android 12+), ensuring 120Hz smoothness.
* **Edge-to-Edge:** Full edge-to-edge implementation as required by Android 16.

---

## 7. Connectivity & System Integration

* **LDAC Optimization:** Configured to prefer the highest possible bitrate for Bluetooth LDAC connections.
* **Bluetooth Listeners:** Implements "Pause on Disconnect" to prevent accidental speaker playback.
* **No Scrobbling:** External scrobbling (Last.fm, etc.) is explicitly excluded to keep the stack lean.

---

## 8. Architecture Patterns

* **MVVM Pattern:** Model-View-ViewModel with Compose UI
* **Repository Pattern:** Data access abstraction layer
* **Dependency Injection:** Hilt 2.55 for constructor injection
* **Reactive Programming:** StateFlow and Flow for data streams
* **Single Source of Truth:** Repository pattern ensures data consistency

---

## 9. Implementation Roadmap

* **Phase 1:** Configure `MediaSessionService` and implement the 10-band EQ pipeline with Media3 1.9.0.
* **Phase 2:** Build the Room FTS5 index and the Folder-View navigation using Paging 3.
* **Phase 3:** Finalize M3 Dynamic Color integration with Compose Material3 1.5.0 and the GPU-accelerated blur effects.
* **Phase 4:** Integrate Hilt for dependency injection and setup WorkManager for background sync.

---

## 10. Performance Benchmarks (Success Metrics)

* **Frame Drops:** Zero drops during rapid scrolling of 4,000+ items.
* **Gapless Latency:** < 10ms transition between tracks.
* **Memory Usage:** Under 100MB RAM even with hardware-accelerated artwork.
* **Audio Buffering:** Zero underruns during background-to-foreground transitions.
* **Battery Efficiency:** < 2% drain per hour of screen-off playback.
* **Index Speed:** Full library rebuild (4,000 tracks) in < 3 seconds.
* **Cold Start:** < 500ms from launch to playback-ready state.

---

## 11. Permissions Handling

* **READ_MEDIA_AUDIO** (Android 13+): Granular audio media access
* **FOREGROUND_SERVICE**: Background media playback
* **FOREGROUND_SERVICE_MEDIA_PLAYBACK**: Media-specific foreground service type
* **WAKE_LOCK**: Keep CPU awake during playback
* **BLUETOOTH_CONNECT**: Bluetooth audio device management

---

## 12. Network Configuration

All Gradle operations use proxy: `http://127.0.0.1:10808`

This is configured in `gradle.properties` for dependency downloads and sync operations.

---
