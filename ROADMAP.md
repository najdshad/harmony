# harmony Music Player - Development Roadmap (Revised)

## Overview

This roadmap provides a deterministic, step-by-step implementation plan for the harmony Music Player. Each phase builds upon the previous one, with clear deliverables and validation criteria.

---

## Phase 0: Foundation & Infrastructure

**Goal:** Establish core infrastructure that all features depend on.

### 0.1 Dependency Injection Setup ✅
- [x] Set up Hilt for dependency injection
  - [x] Configure application class with DI container
  - [x] Define modules for services, repositories, viewmodels
  - [x] Test DI configuration
- [ ] Create Repository interfaces
  - `TrackRepository`, `AlbumRepository`, `ArtistRepository`, `FolderRepository`
  - Define contracts without implementations

### 0.2 Basic UI Structure ✅
- [x] Create MainActivity with Compose
- [x] Create basic placeholder screens
- [x] Setup Material 3 theming
- [x] Configure edge-to-edge UI
- [x] Verify Java 17 toolchain working
- [x] Confirm successful build generation

### 0.2 Core Utilities & Error Handling
- [ ] Create `Result<T>` wrapper for operations that can fail
  - Generic success/error type
  - Extension functions for common operations
- [ ] Implement error handling utilities
  - Specific exception types (MediaStoreException, PlaybackException, etc.)
  - Error logging with context
  - User-friendly error message formatter
- [ ] Create logging utility (early version)
  - Simple log levels (ERROR, WARN, INFO, DEBUG)
  - Write to logcat initially (will enhance in Phase 7)

**Phase 0 Deliverable:** Foundation infrastructure ready for feature implementation.

---

## Phase 1: Settings & Persistence Layer

**Goal:** Build settings infrastructure FIRST, as it's needed by audio and UI features.

### 1.1 DataStore Setup
- [ ] Create `SettingsDataStore`
  - Use `DataStore<Preferences>` for type-safe storage
  - Define all preference keys upfront
  - Implement generic get/set helpers
- [ ] Define settings categories
  - Audio: EQ presets, LDAC preference, audio focus bypass
  - Playback: Gapless, shuffle, repeat, pause on disconnect
  - Library: Sort options, folder view preference
  - UI: Dynamic color toggle
  - Advanced: Buffer size, logging level
- [ ] Test DataStore persistence
  - Write/read all setting types
  - Verify data survives app restart

### 1.2 Database Schema Design
- [ ] Define Room entities
  - `Track`: id, title, artist, album, duration, path, albumArtUri, fileModifiedTime
  - `Album`: id, name, artist, albumArtUri, trackCount
  - `Artist`: id, name, albumCount
  - `Folder`: id, path, name, parentFolderId
- [ ] Design FTS5 search architecture
  - `TrackFts` virtual table with triggers
  - FTS5 content includes title, artist, album
  - Enable `portable=1` and `unicode61` tokenizer
  - Implement triggers to sync Track ↔ TrackFts
- [ ] Define DAOs
  - TrackDao: CRUD, search queries, folder queries
  - AlbumDao: CRUD, artist filtering
  - ArtistDao: CRUD
  - FolderDao: efficient tree traversal (use CTE for recursive queries)
- [ ] Set up Room database
  - Configure FTS5 support
  - Add migration strategies
  - Enable multi-instance invalidation
- [ ] Write unit tests for DAOs
  - Test CRUD operations
  - Test FTS5 search queries
  - Test folder tree traversal performance

**Phase 1 Deliverable:** Complete persistence layer with Room and DataStore.

---

## Phase 2: UI Foundation & Theme

**Goal:** Build UI theme, components, and navigation infrastructure BEFORE creating screens.

### 2.1 Material 3 Theme Setup
- [ ] Configure dynamic color
  - Integrate `MaterialYou` color tokens
  - Create `Color.kt` with dynamic color scheme
  - Handle Android < S with fallback colors
- [ ] Set up typography
  - Define `Type.kt` with Material 3 text styles
  - Use appropriate scales for headers, body, captions
- [ ] Configure shapes and elevation
  - Use Material 3 corner radii
  - Apply proper elevation for layers

### 2.2 Image Loading with Coil
- [ ] Configure Coil for hardware bitmaps
  - Set `ImageLoader` with `Bitmap.Config.HARDWARE`
  - Enable memory and disk caching
  - Configure cache sizes appropriately
  - Implement placeholder and error drawables
- [ ] Create reusable image composables
  - `AlbumArtImage`: loads artwork with fade-in animation
  - `CircularAvatar`: for artist images
  - Fallback to default icon if missing
  - Support corner radius matching Material 3
- [ ] Test image performance early
  - Load many images rapidly
  - Monitor memory usage

### 2.3 GPU-Accelerated Effects
- [ ] Implement glassmorphism blur modifier
  - Create `BlurModifier` using `Modifier.graphicsLayer { renderEffect }`
  - Use `RenderEffect.createBlurEffect()` (Android 12+)
  - Fallback to alpha overlay for older versions
- [ ] Create reusable blur composables
  - `BlurSurface`: applies blur to any content
  - `BlurredBackground`: for album art backgrounds

### 2.4 Navigation Architecture
- [ ] Configure Jetpack Navigation Compose
  - Create `NavHost` with all destinations
  - Define routes with type-safe arguments
  - Set up navigation actions
- [ ] Create navigation utilities
  - Type-safe navigation extension functions
  - Deep linking support
  - Navigation state restoration

**Phase 2 Deliverable:** UI foundation with theme, image loading, effects, and navigation ready.

---

## Phase 3: Data Layer & MediaStore Integration

**Goal:** Build reactive data pipeline with MediaStore sync and repositories.

### 3.1 MediaStore Scanner
- [ ] Implement `MediaStoreScanner`
  - Query `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI`
  - Filter for supported formats (MP3, FLAC, ALAC, WAV, OGG, M4A)
  - Extract metadata using `MediaMetadataRetriever`
  - Map to Room entities efficiently (batch inserts)
- [ ] Handle permissions
  - Request `READ_MEDIA_AUDIO` permission with proper UI flow
  - Handle partial media access gracefully
  - Fallback strategies if access denied
- [ ] Implement incremental sync strategy
  - Use ContentObserver for change detection (more efficient than polling)
  - Batch updates to minimize DB transactions
  - Handle file deletions (track via ContentObserver, not modification time)
- [ ] Write unit tests for scanner logic
  - Test metadata extraction
  - Test incremental sync detection

### 3.2 Repository Implementations
- [ ] Implement `TrackRepository`
  - Expose `Flow<PagingData<Track>>` for lists
  - Search methods using FTS5
  - Folder query methods
- [ ] Implement `AlbumRepository`
  - CRUD operations
  - Artist filtering
  - `Flow<PagingData<Album>>` exposure
- [ ] Implement `ArtistRepository`
  - CRUD operations
  - `Flow<PagingData<Artist>>` exposure
- [ ] Implement `FolderRepository`
  - Tree traversal queries (use SQLite CTE for efficiency)
  - Folder counting
  - Track listing by folder
- [ ] Write repository integration tests
  - Test query performance with large datasets

### 3.3 WorkManager Sync
- [ ] Implement `InitialScanWorker`
  - One-time work for first launch
  - Run on constraints: charging, unmetered network, idle
  - Handle large libraries with chunking
- [ ] Implement `PeriodicSyncWorker`
  - Daily health checks
  - Detect orphaned records
  - Cleanup deleted files
- [ ] Test sync workers
  - Verify initial scan completes
  - Test periodic sync execution
  - Test sync cancellation

**Phase 3 Deliverable:** Complete data layer with MediaStore sync and repository pattern.

---

## Phase 4: Core Audio Infrastructure

**Goal:** Build Media3 playback service with gapless support.

### 4.1 MediaSessionService Setup
- [ ] Extend `MediaSessionService` in `MediaPlaybackService.kt`
  - Create notification channel for playback controls
  - Configure ExoPlayer with `AudioAttributes` for music
  - Implement `onGetSession()` to return `MediaSession` instance
  - Handle service lifecycle with proper cleanup
- [ ] Configure foreground service notification
  - Implement notification with MediaStyle
  - Add play/pause/skip actions
  - Support system media controls across Android versions
  - Handle notification dismissal behavior
- [ ] Create playback controller
  - Wrap ExoPlayer in `PlaybackController` class
  - Expose StateFlow for player state
  - Handle playback events and errors
- [ ] Test basic playback
  - Verify foreground service starts
  - Test notification controls
  - Test play/pause functionality

### 4.2 Gapless Playback
- [ ] Implement `ConcatenatingMediaSource2` playlist system
  - Create `PlaylistManager` for queue management
  - Pre-buffer next track at 90% completion
  - Handle track transitions with zero latency
  - Support shuffle and repeat modes
- [ ] Implement audio focus handling
  - Request audio focus with `AUDIOFOCUS_GAIN`
  - Handle `AUDIOFOCUS_LOSS` (pause playback)
  - Handle `AUDIOFOCUS_LOSS_TRANSIENT` (pause briefly)
  - Handle `AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK` (duck volume)
  - Integrate with settings for focus bypass toggle
- [ ] Test gapless playback
  - Verify no audible gaps
  - Measure transition latency (< 10ms target)
  - Test audio focus scenarios

### 4.3 Custom LoadControl & Buffer Management
- [ ] Implement custom `LoadControl`
  - Allocate sufficient RAM buffers
  - Configure min/max buffer sizes
  - Tune for local file playback (not streaming)
- [ ] Test buffer performance
  - Verify no audio underruns
  - Test background-to-foreground transitions
  - Integrate with settings for buffer size adjustment

### 4.4 High-Resolution Audio
- [ ] Configure ExoPlayer for hi-res output
  - Enable direct `AudioTrack` routing
  - Support 24-bit/192kHz FLAC, ALAC, WAV
  - Set appropriate audio session parameters
- [ ] Test with high-res files
  - Verify output format matches source
  - Check for sample rate conversion artifacts

**Phase 4 Deliverable:** Functional playback service with gapless, audio focus, and hi-res support.

---

## Phase 5: Library Screens & Navigation Integration

**Goal:** Build UI screens that consume data from repositories.

### 5.1 Library Screen (Root)
- [ ] Create `LibraryScreenViewModel`
  - State management for tabs
  - Search state with debouncing
  - Integration with repositories
- [ ] Create `LibraryScreen` composable
  - Tab navigation (Folders, Albums, Artists, All Tracks)
  - Search bar with FTS5 integration
  - Smooth tab switching with saved state
- [ ] Test library screen
  - Verify tab switching
  - Test search functionality
  - Test state restoration

### 5.2 Folder Browser Screen
- [ ] Create `FolderBrowserViewModel`
  - Track current folder path
  - Load folder contents with Paging 3
  - Handle folder navigation
- [ ] Create `FolderBrowserScreen`
  - Display folder hierarchy
  - Breadcrumb navigation
  - Track count per folder
  - Drill down/up navigation
- [ ] Create `TrackListScreen`
  - Display tracks in selected context
  - Show metadata (title, artist, duration)
  - Long-press context menu
  - Swipe actions (play next, add to queue)
- [ ] Test folder navigation
  - Deep hierarchy navigation
  - Verify smooth transitions
  - Test back button behavior

### 5.3 Albums Screen
- [ ] Create `AlbumsViewModel`
  - Load albums with Paging 3
  - Sorting options (name, year, artist)
- [ ] Create `AlbumsScreen`
  - Grid view with album art
  - Group by artist
  - Album detail view
- [ ] Test albums screen
  - Grid scrolling performance
  - Sort option changes
  - Album detail navigation

### 5.4 Artists Screen
- [ ] Create `ArtistsViewModel`
  - Load artists with Paging 3
- [ ] Create `ArtistsScreen`
  - List view with artist info
  - Album count per artist
  - Artist detail view
- [ ] Test artists screen
  - List scrolling performance
  - Artist detail navigation

**Phase 5 Deliverable:** Complete library screens with navigation and data integration.

---

## Phase 6: Now Playing Screen

**Goal:** Build the main playback UI with GPU-accelerated effects.

### 6.1 Now Playing Screen UI
- [ ] Create `NowPlayingViewModel`
  - Subscribe to player state from PlaybackController
  - Track current playlist position
  - Expose shuffle/repeat state
- [ ] Create `NowPlayingScreen` composable
  - Large album art with blur background (using BlurModifier)
  - Track info: title, artist, album
  - Playback controls: prev, play/pause, next
  - Progress bar with scrubbing
  - Seek forward/backward buttons
  - Shuffle/repeat toggles
  - Smooth animations for state changes
- [ ] Integrate with MediaSession
  - Real-time UI updates from player state
  - Handle metadata changes
  - Update progress bar smoothly
- [ ] Test playback controls
  - Verify all buttons work
  - Test scrubbing
  - Test background playback persistence
- [ ] Test blur performance
  - Verify 120Hz on supported devices
  - Check GPU utilization

**Phase 6 Deliverable:** Polished Now Playing screen with GPU-accelerated blur.

---

## Phase 7: Advanced Audio Features

**Goal:** Implement EQ, Bluetooth optimization, and audio enhancements.

### 7.1 10-Band Equalizer
- [ ] Create EQ bands configuration
  - Define 10 ISO bands: 31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz
  - Range: -12dB to +12dB in 1dB steps
- [ ] Integrate EQ with Media3
  - Use `Equalizer` API from `android.media.audiofx`
  - Apply to ExoPlayer audio session
  - Handle EQ unavailability gracefully (not all devices support it)
- [ ] Create EQ UI
  - Vertical sliders for each band
  - Presets: Flat, Bass Boost, Treble Boost, Vocal, Classical, Rock, Jazz
  - Custom preset with user adjustments
  - Smooth slider animations
- [ ] Persist EQ settings to DataStore (already set up in Phase 1)
  - Load on app startup
- [ ] Test EQ
  - Verify each band affects sound
  - Test preset switching
  - Check for audio distortion at extremes

### 7.2 Bluetooth LDAC Optimization
- [ ] Configure Bluetooth codec preferences
  - Set LDAC as preferred codec (if supported)
  - Request highest bitrate (990kbps)
  - Use adaptive bitrate fallback
  - Note: Requires system-level permissions, may not work on all devices
- [ ] Implement codec info display
  - Show current codec in settings
  - Display bitrate when available
  - Show fallback if LDAC unavailable
- [ ] Test Bluetooth audio
  - Connect to LDAC-capable device
  - Verify codec negotiation
  - Graceful degradation for non-LDAC devices

### 7.3 Bluetooth Disconnect Handling
- [ ] Implement Bluetooth state listener
  - Register `BluetoothAdapter` state callback
  - Detect audio device disconnections
  - Differentiate between device disconnect and adapter off
- [ ] Add "Pause on Disconnect" feature
  - Pause playback when audio device disconnects
  - Show notification to user
  - Persist toggle setting (already in Phase 1)
- [ ] Test disconnect scenarios
  - Disconnect headphones → pause
  - Disable toggle → continue on speaker

### 7.4 Advanced Buffer Tuning
- [ ] Optimize `LoadControl` parameters (from Phase 4)
  - Tune for different device capabilities
  - Integrate with settings for user adjustment
- [ ] Implement adaptive buffering
  - Monitor playback quality metrics
  - Adjust buffers dynamically
- [ ] Test buffer performance
  - Various playback scenarios
  - Verify smooth playback

**Phase 7 Deliverable:** Complete audio enhancement suite with EQ and Bluetooth optimization.

---

## Phase 8: Settings Screen

**Goal:** Build settings UI that leverages DataStore from Phase 1.

### 8.1 Settings Screen UI
- [ ] Create `SettingsScreenViewModel`
  - Load all settings from DataStore
  - Handle setting updates
- [ ] Create `SettingsScreen` composable
  - LazyColumn with categorized sections
  - Toggle switches for booleans
  - Dropdowns for selections
  - Sliders for numeric values (EQ bands, buffer size)
  - Material 3 settings components
- [ ] Implement settings navigation
  - Add to library tabs
  - Add quick access from now playing
- [ ] Test settings persistence
  - Change setting, restart app → verify saved
  - Test all setting types

**Phase 8 Deliverable:** Complete settings system with UI.

---

## Phase 9: Enhanced Logging & Diagnostics

**Goal:** Implement production-ready logging system.

### 9.1 Enhanced Logging System
- [ ] Upgrade `Logger` utility from Phase 0
  - Write to app-specific directory `.txt` files
  - Rotate logs by date/size (max 5MB per file, keep 10 files)
  - Log levels: ERROR, WARN, INFO, DEBUG
  - Async logging to avoid blocking main thread
- [ ] Enhance log format
  - Timestamp, level, tag, message
  - Include context: track info, state, action
  - Stack traces for exceptions
- [ ] Add logging throughout codebase
  - Playback start/stop/errors
  - MediaStore sync operations
  - Permission requests
  - Audio focus changes
  - Bluetooth events
  - Performance metrics

### 9.2 Log Viewer UI
- [ ] Create `LogsScreen`
  - Display recent logs with LazyColumn
  - Filter by level
  - Search functionality
  - Share/clear options
  - Syntax highlighting for log levels
- [ ] Add to settings
  - Toggle logging on/off
  - Set log level
  - View logs button
- [ ] Test logging
  - Trigger various events
  - Verify logs capture details
  - Test log rotation

**Phase 9 Deliverable:** Production-ready logging system with viewer.

---

## Phase 10: Testing & Performance Optimization

**Goal:** Comprehensive testing and meeting all performance benchmarks.

### 10.1 Performance Testing
- [ ] Frame rate testing
  - Test rapid scrolling through 4,000+ tracks
  - Verify zero frame drops at 120Hz target
  - Profile with GPU/CPU analyzers
  - Optimize images and animations as needed
- [ ] Cold start optimization
  - Measure time to first render (target < 500ms)
  - Defer non-critical initialization
  - Optimize startup tasks (App.onCreate, first composition)
  - Use lazy initialization where possible
- [ ] Memory optimization
  - Profile with heap analyzer
  - Verify under 100MB during playback
  - Check for memory leaks (activity, service, composable references)
  - Optimize image caching strategies
- [ ] Audio performance
  - Measure gapless latency (< 10ms)
  - Test for underruns during app switching
  - Verify battery drain (< 2%/hr screen-off)

### 10.2 Comprehensive Testing
- [ ] Unit tests (write continuously throughout development)
  - All DAOs (done in Phase 1)
  - Repository implementations (done in Phase 3)
  - ViewModel logic
  - Utility functions
- [ ] Instrumentation tests
  - MediaSessionService lifecycle
  - Navigation flows
  - Permission handling
  - DataStore persistence
- [ ] UI tests (optional, if time permits)
  - Critical user flows
  - Accessibility checks
- [ ] Manual testing matrix
  - Multiple Android versions (API 33-36)
  - Various screen sizes (phone, tablet, foldable)
  - Large libraries (4,000+ tracks)
  - Bluetooth devices (various codecs)
  - Poor network conditions (if streaming added later)
  - Low-memory devices
  - Corrupted/malformed media files

### 10.3 Code Quality
- [ ] Run ktlint and fix issues
- [ ] Run Android Lint and fix warnings
- [ ] Review all code for AGENTS.md compliance
- [ ] Document complex logic
- [ ] Final code review

**Phase 10 Deliverable:** Production-ready code meeting all benchmarks.

---

## Phase 11: Final Polish & Release Preparation

**Goal:** Prepare for production deployment.

### 11.1 Final Polish
- [ ] Verify all Material 3 guidelines
- [ ] Test edge cases
  - Empty library
  - Corrupted files
  - Permission denied
  - No storage space
  - Bluetooth connection failures
- [ ] Optimize app icon and launcher graphics
- [ ] Add translations if needed
- [ ] Accessibility audit

### 11.2 Build Configuration
- [ ] Configure release signing
- [ ] Enable ProGuard/R8 optimization
  - Add ProGuard rules for Coil, Media3, Room
  - Test R8-optimized build thoroughly
- [ ] Set version code and name
- [ ] Configure build variants (debug, release)

### 11.3 Documentation
- [ ] Update README with features
- [ ] Create user guide
- [ ] Document architecture decisions
- [ ] Prepare changelog

### 11.4 Final Validation
- [ ] Complete end-to-end testing
- [ ] Verify all performance metrics
- [ ] Test on production devices
- [ ] Security review
  - Check for hardcoded secrets
  - Verify network security (if remote features added)
  - Review data storage permissions

**Phase 11 Deliverable:** Release-ready APK/AAB.

---

## Dependencies & Prerequisites

- Android Studio Otter (2025.2.2) or later
- Kotlin 2.2.20
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 36 (Android 16)
- Compile SDK: 36 (Android 16)
- Java 17

---

## Critical Considerations & Potential Pitfalls

### Memory Management
- **Risk:** Large image caching can exceed 100MB limit
- **Mitigation:** Use hardware bitmaps, aggressive memory cache limits, and clear caches on trim memory events

### Folder Tree Performance
- **Risk:** Recursive folder queries can be slow with deep hierarchies
- **Mitigation:** Use SQLite CTE for efficient tree traversal, consider flattening for display

### MediaStore Change Detection
- **Risk:** File modification time tracking is unreliable
- **Mitigation:** Use ContentObserver for change signals, batch updates to minimize DB writes

### Bluetooth Codec Limitations
- **Risk:** LDAC preference requires system-level permissions not available to apps
- **Mitigation:** Implement as "best effort", show codec info to user, provide fallback

### Gapless Playback
- **Risk:** ConcatenatingMediaSource2 requires careful playlist management
- **Mitigation:** Thorough testing with various track transitions, handle edge cases (last track, single track)

### FTS5 Search Performance
- **Risk:** Large libraries (>10K tracks) may exceed 10ms query target
- **Mitigation:** Optimize FTS5 configuration, add search result limit, consider search index size

### Thread Safety
- **Risk:** ExoPlayer must be accessed from single thread
- **Mitigation:** Use MainThread or dedicated player thread, enforce with thread enforcement

### Notification Compatibility
- **Risk:** MediaStyle behavior varies across Android versions
- **Mitigation:** Test on API 24, 28, 30, 33, 34, 35, 36, add version checks where needed
- **Edge-to-Edge**: Required for Android 16 (API 36), cannot opt-out

### Background Playback
- **Risk:** Service may be killed in background
- **Mitigation:** Proper foreground service management, handle service restart in AndroidManifest

---

## Success Metrics Validation

Each phase must pass validation before proceeding:

| Metric | Target | Validation Method | Phase |
|--------|--------|-------------------|-------|
| Cold Start | < 500ms | App startup timing | Phase 10 |
| FTS5 Query | < 10ms | Database query profiling | Phase 3 |
| Gapless Latency | < 10ms | Audio capture analysis | Phase 4 |
| Frame Drops | Zero (120Hz) | GPU profiling | Phase 10 |
| Memory Usage | < 100MB | Android Profiler | Phase 10 |
| Audio Underruns | Zero | Extended playback test | Phase 4 |
| Battery Drain | < 2%/hr | Battery Historian | Phase 10 |
| Index Speed | < 3s (4K tracks) | Performance timing | Phase 3 |

---

## Technology Stack

- **Kotlin**: 2.2.20
- **Gradle**: 8.13
- **Android Gradle Plugin**: 8.13.2
- **Jetpack Compose**: BOM 2024.10.01
- **Compose Compiler**: 1.5.8
- **Material 3**: 1.2.1
- **Media3**: 1.4.0
- **Room**: 2.6.1
- **Hilt**: 2.51.1
- **KSP**: 2.2.20-2.0.4
- **Coil**: 3.3.0
- **Paging 3**: 3.3.1
- **Work Manager**: 2.9.0

## Notes

- Follow AGENTS.md for all code style and engineering standards
- Run `./gradlew ktlintCheck` and `./gradlew lint` before committing
- Write tests for all business logic (unit tests during development, integration tests in Phase 10)
- Use deterministic order for database queries
- All UI updates must use proper state management (StateFlow/remember)
- No shortcuts - every feature must be production quality
- Continuous testing: Don't wait until Phase 10 to test core functionality
- Memory-first approach: Keep 100MB limit in mind throughout development
- Early performance profiling: Profile as you build, not just at the end
- All network access uses proxy: http://127.0.0.1:10808 (configured in gradle.properties)
