---

# Technical Specification: harmony Music Player

## 1. Vision & Core Philosophy

**harmony** is designed for listeners who value audio integrity, visual elegance, and instant responsiveness. It treats massive libraries as lightweight collections, ensuring hardware is dedicated to reproducing music perfectly.

* **Opinionated Defaults:** Gapless playback is mandatory; UI is "flat" for speed.
* **Performance:** 120Hz scrolling, optimized cold start, and minimal battery footprint.
* **Aesthetic:** Pure Material 3 utilizing system-wide **Material You** dynamic coloring.

---

## 2. Technology Stack

| Component | Technology | Version | Purpose |
| --- | --- | --- | --- |
| **Language** | Kotlin | 2.1.0 | Modern, safe, and expressive |
| **UI Framework** | Jetpack Compose | BOM 2024.10.01 | Declarative UI for smooth transitions |
| **Material 3** | Compose Material3 | 1.2.1 | Material Design 3 components |
| **Media Library** | Media3 + ExoPlayer | 1.4.0 | Modern Android media handling |
| **Persistence** | Room DB + FTS5 | 2.6.1 | Metadata caching and lightning-fast search |
| **Settings** | DataStore | 1.1.2 | Type-safe key-value storage |
| **DI Framework** | Hilt | 2.51.1 | Dependency injection |
| **App Startup** | Startup Runtime | 1.2.0 | Efficient initialization |
| **Coroutines** | kotlinx.coroutines | 1.8.0 | Asynchronous programming |
| **Image Loading** | Coil | 3.3.0 | Async image loading with hardware bitmaps |
| **Work Manager** | androidx.work | 2.9.0 | Background task scheduling |
| **Navigation** | Navigation Compose | 2.8.4 | Screen navigation |
| **Build System** | Gradle | 8.13 | Build automation |
| **AGP** | Android Gradle Plugin | 8.13.2 | Android-specific Gradle tasks |
| **KSP** | Kotlin Symbol Processing | 2.1.0-1.0.29 | Annotation processing |

---

## 3. Android Configuration

| Configuration | Value |
| --- | --- |
| **minSdk** | 33 (Android 13 Tiramisu) |
| **targetSdk** | 35 (Android 15) |
| **compileSdk** | 35 (Android 15) |
| **JVM Target** | Java 17 |
| **Namespace** | com.harmony.player |

---

## 4. Audio Engine (Media3 ExoPlayer Foundation)

The engine is built on **Jetpack Media3 (ExoPlayer) 1.4.0**.

### A. Playback & Focus

* **Gapless Playback:** Uses `ConcatenatingMediaSource2` for zero-latency transitions and pre-buffers the next track at 90% completion.
* **Audio Focus Logic:** Automatically lowers volume during notifications (ducking).
* **Supported Formats:** All common audio formats - MP3, AAC, FLAC, ALAC, WAV, OGG, OPUS, M4A, WMA
* **Bluetooth Audio:** Pause on disconnect when audio is playing.
* **MediaSessionService:** Background playback with Media3 session service integration.

---

## 5. Library & Data Management

Optimized for 10k+ tracks using a reactive data pipeline.

### A. Data Architecture

* **Room Database 2.6.1:** Mirrors MediaStore for fast metadata access.
* **Room FTS5:** Implements Full-Text Search (FTS5) for sub-10ms "search-as-you-type" across the entire library.
* **Queue Storage:** Playback queue persisted in Room database.

### B. Navigation & Sync

* **File Watcher:** Uses **WorkManager 2.9.0** and `ContentObserver` to detect new files added via PC/file manager and sync them to the Room DB automatically.
* **Permissions:** Robust handling of `READ_MEDIA_AUDIO` for Android 13+ and partial media access.

---

## 6. UI/UX & Visual Identity

The UI follows the **Material 3 (M3) 1.2.1** spec with a focus on system integration.

### A. System-Driven Design

* **Material You:** The app uses system-wide dynamic color tokens (set by the user's wallpaper) for the entire UI palette.
* **Hardware Bitmaps:** Coil uses `Bitmap.Config.HARDWARE` to offload image rendering to the GPU.
* **High-Quality Caching:** Coil disk cache with aggressive caching strategy for album artwork.

### B. Edge-to-Edge

* **Edge-to-Edge:** Full edge-to-edge implementation as required by Android 16.

### C. Mini-Player

* **Persistent Bottom Bar:** Mini-player visible on all screens including Now Playing
* **Now Playing Behavior:** Now Playing screen collapses into the mini-player when navigating away
* **Mini-Player Actions:** Play/pause, skip, expand to full Now Playing screen

---

## 7. Connectivity & System Integration

* **Bluetooth Listeners:** Implements "Pause on Disconnect" to prevent accidental speaker playback.
* **No Scrobbling:** External scrobbling (Last.fm, etc.) is explicitly excluded to keep the stack lean.

---

## 8. Architecture Patterns

* **MVVM Pattern:** Model-View-ViewModel with Compose UI
* **Repository Pattern:** Data access abstraction layer
* **Dependency Injection:** Hilt 2.51.1 for constructor injection
* **Reactive Programming:** StateFlow and Flow for data streams
* **Single Source of Truth:** Repository pattern ensures data consistency

---

## 9. MediaSession Architecture

### A. Service-UI Communication

* **MediaSessionService:** Runs as a foreground service for background playback
* **MediaController:** UI layer uses MediaController to communicate with the service
* **StateFlow Bridge:** Service exposes playback state via StateFlow for Compose UI
* **Lifecycle Management:** Service starts when playback begins, stops when queue is empty and UI is destroyed

### B. Media Controls

* **MediaStyle Notification:** System notification with play/pause/skip controls
* **Lock Screen Controls:** Standard MediaSession integration
* **Quick Settings:** Media control tile integration via MediaSession
* **System Media Browser:** Exposes library to system (optional)

---

## 10. Playback Controls

### A. Queue Management

* **Queue Persistence:** Queue state saved to Room database
* **Queue Manipulation:** Drag-and-drop reordering, remove from queue, add to queue
* **Current Position Tracking:** Seekable timeline with time display
* **Queue Visibility:** Full queue list view with ability to jump to tracks

### B. Playback Modes

* **Shuffle Mode:** Randomize next track selection
* **Repeat Modes:** Off, All (loop entire queue), One (loop current track)
* **Play All Album:** Play all tracks from album starting at selected track
* **Play Single Track:** Play selected track then stop

---

## 11. Playlist Support

### A. Playlist Features

* **Create Playlists:** User can create custom playlists
* **Edit Playlists:** Add/remove tracks, rename playlists
* **Delete Playlists:** Remove playlists (does not delete tracks from library)
* **Playlist Playback:** Play all tracks in playlist with shuffle/repeat support
* **Export/Import:** Backup and restore playlists in M3U format

### B. Playlist Storage

* **Room Entities:** Separate playlist and playlist_track tables
* **Track References:** Playlists store track IDs, not metadata copies
* **Sync with Library:** Playlists remain valid after library rescan

---

## 12. Navigation Graph

### A. Screen Hierarchy (Simple)

```
Library Screen (Root) [with Mini-Player]
├── Album Detail Screen
│   └── Track List
│       └── [Click track] → Play single
│       └── [Play All] → Now Playing Screen
├── Artist Detail Screen
│   └── Albums List → Album Detail → Track List
├── Search Screen
├── Playlists Screen
│   └── Playlist Detail Screen
└── Settings Screen

Now Playing Screen (Modal/Bottom Sheet) [with Mini-Player - collapses to it]
```

### B. Navigation Behavior

* **Back Stack:** Standard Compose Navigation back stack
* **Playback State:** Persists across navigation (doesn't reset when navigating)
* **Now Playing Access:** Persistent mini-player on all screens, expands to full screen
* **Album → Now Playing:** Click track to play, "Play All" to queue entire album

---

## 13. Search Functionality

### A. Search Structure

* **Three Sections:** Albums, Artists, Tracks displayed in separate tabs/sections
* **Real-Time Results:** FTS5 provides instant search-as-you-type
* **Result Actions:**
  * Album click → Navigate to Album Detail Screen
  * Artist click → Navigate to Artist Detail Screen
  * Track click → Play track immediately

### B. Search Behavior

* No loading UI needed (FTS5 is <10ms)
* Highlight matched text in results
* Empty state with "No results found" message

---

## 14. Error Handling

### A. Error Scenarios

* **Corrupted Files:** Skip to next track, log error, show error message in toast
* **Missing Artwork:** Use default album art (Material You colored placeholder)
* **Permission Denial:** Show explanation dialog, guide user to settings to grant permission
* **Database Errors:** Retry operation, show error message, fallback to MediaStore if needed
* **Playback Failures:** Expose error state to UI, provide retry option

### B. Error Handling Pattern

* **Result<T> Wrapper:** All operations return Result<T> for explicit error handling
* **Specific Exceptions:** Catch specific exceptions (IOException, SecurityException, etc.)
* **User-Friendly Messages:** Never show stack traces, display contextual error messages
* **Graceful Degradation:** App continues to function even with partial failures

---

## 15. Settings Structure

### A. Data Management

* **Rescan Library:** Manual trigger to rebuild database
* **Clear Cache:** Clear image cache
* **Export/Import Playlists:** Backup and restore playlists in M3U format

---

## 16. Empty States & Loading States

### A. Empty States (Material 3 Pattern)

```
Empty Library (No tracks found):
┌─────────────────────────────┐
│     Icon (Musical Note)      │
│                              │
│   "No music found"           │
│                              │
│   "Grant storage access or   │
│    add music to your device" │
│                              │
│   [Grant Permission]         │
│   [Rescan Library]           │
└─────────────────────────────┘

Empty Playlist (No tracks):
┌─────────────────────────────┐
│     Icon (Playlist)          │
│                              │
│   "This playlist is empty"   │
│                              │
│   "Add tracks from your      │
│    library to get started"   │
│                              │
│   [Browse Library]           │
└─────────────────────────────┘

Empty Search Results:
┌─────────────────────────────┐
│     Icon (Search)           │
│                              │
│   "No results found"         │
│                              │
│   "Try a different search   │
│    term or check spelling"   │
│                              │
│   [Clear Search]             │
└─────────────────────────────┘
```

### B. Loading States

* **Initial Library Scan:** Full-screen overlay with spinner, track count, cancel button
* **Search Queries:** No loading UI (FTS5 is instant)
* **Album Art Loading:** Immediate placeholder, fade in actual image (Coil Crossfade)
* **Screen Navigation:** No loading UI normally, skeleton loader if >300ms

---

## 17. Startup Sequence

### A. Initialization Order

1. **Application.onCreate() [Immediate, Main Thread]**
   * Hilt initializes automatically

2. **App Startup ContentProvider [Early, Background]**
   * DataStore.initialize() (async, non-blocking)
   * Room database.build() (async, creates tables)

3. **First Screen Composition [When needed]**
   * ViewModel initializes repository
   * Repository queries Room (first query creates connection)

4. **Playback Trigger [On user action]**
   * Start MediaSessionService
   * Load queue from Room
   * Start playback

### B. Key Principles

* Never block main thread with DB or I/O
* Defer non-critical initialization (EQ, sync workers)
* Lazy initialization of components until actually needed

---

## 18. Performance Monitoring

### A. Development Phase

* **Android Studio Profiler:** CPU profiler for janky frames, Memory profiler for leaks
* **Layout Inspector:** Verify no overdraw
* **Logcat:** Watch for dropped frames and warnings

### B. Runtime (Production)

* **Manual log.txt** (as per AGENTS.md):
  * Cold start timestamp
  * Playback latency logs
  * Memory warnings (OOM, etc.)
  * Error logs with context

### C. What NOT to Do

* No Firebase/Analytics (privacy + complexity)
* No performance SDKs (overhead)
* No automatic benchmarking (not needed)

---

## 19. Testing Strategy

### A. Unit Tests

* Repository layer with MockK
* ViewModels with coroutine testing
* Result<T> wrapper and error handling

### B. Integration Tests

* Room database operations
* DataStore persistence
* Playlist export/import (M3U parsing)

### C. UI Tests

* Key navigation flows (Library → Album → Now Playing)
* Playback controls (play, pause, skip, seek)
* Queue manipulation

### D. Instrumentation Tests

* Media3 playback engine
* MediaSessionService lifecycle
* Permission handling

---

## 20. Database Migration Strategy

### A. Approach

* Use proper Room migrations (not destroy & recreate)
* Version database with incrementing version numbers
* Test migrations before release

### B. Migration Handling

* Add new fields with ALTER TABLE
* Handle schema changes gracefully
* Provide fallback to rescan library if migration fails

---

## 21. Implementation Roadmap

* **Phase 0: Foundation**
  * Setup Hilt dependency injection
  * Create Result<T> wrapper and error handling utilities
  * Create logging utility
  * Define Repository interfaces
  * Setup App Startup for DataStore and Room initialization

* **Phase 1: Data Layer**
  * Define Room entities (Track, Album, Artist, Playlist, PlaybackQueue)
  * Track entity: id, title, artistId, albumId, duration, filePath, uri, trackNumber, year
  * Create DAOs with FTS5 support
  * Setup Room database with proper migrations
  * Implement repositories

* **Phase 2: Playback Engine**
  * Configure MediaSessionService
  * Implement MediaController bridge
  * Add queue management with Room persistence
  * Implement shuffle/repeat modes
  * Add MediaStyle notification
  * Implement Bluetooth pause on disconnect

* **Phase 3: UI Foundation**
  * Setup Compose Navigation with persistent mini-player
  * Create Material 3 theme with dynamic color
  * Configure Coil for hardware bitmaps with high-quality caching
  * Build basic screens (Library, Album Detail, Artist Detail, Playlists)
  * Implement empty states and loading states

* **Phase 4: Advanced Features**
  * Build playlist management system (create, edit, delete, export/import M3U)
  * Setup WorkManager for background sync
  * Add search with FTS5 integration (3 sections: albums, artists, tracks)
  * Implement queue management UI

* **Phase 5: Polish**
  * Write unit tests, integration tests, and UI tests
  * Performance validation

---

## 22. Performance Benchmarks (Guidelines)

* **Goal:** Write optimized code, track patterns, don't obsess over exact numbers
* **Frame Drops:** Target smooth scrolling of 10k+ items with LazyColumn keys
* **Memory Usage:** Keep memory reasonable with Coil hardware bitmap caching
* **Cold Start:** Fast app launch with deferred initialization

---

## 23. Permissions Handling

* **READ_MEDIA_AUDIO** (Android 13+): Granular audio media access
* **FOREGROUND_SERVICE**: Background media playback
* **FOREGROUND_SERVICE_MEDIA_PLAYBACK**: Media-specific foreground service type
* **WAKE_LOCK**: Keep CPU awake during playback
* **BLUETOOTH_CONNECT**: Bluetooth audio device management
* **POST_NOTIFICATIONS** (Android 13+): Show playback notification

---
