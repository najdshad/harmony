# Harmony Music Player Development Roadmap

## Overview

This roadmap outlines the phased development of the Harmony Music Player Android application, based on the technical specification in SPECFILE.md. The project follows Android best practices with Jetpack Compose, Media3, Room, and Hilt, targeting a high-performance music player with gapless playback, Material 3 UI, and efficient data management.

Each phase builds incrementally on the previous one, ensuring stability and allowing for iterative testing. The roadmap is designed for a senior Android development team, with clear deliverables, dependencies, and testing requirements.

### Key Principles
- **Incremental Development**: Each phase delivers a working, testable milestone
- **Performance Focus**: Maintain benchmarks (e.g., <100MB RAM, 120Hz scrolling)
- **Code Quality**: Follow AGENTS.md guidelines (lint, tests, logging)
- **Architecture Adherence**: MVVM, Repository pattern, Hilt DI

---

## Phase 0: Foundation Setup
**Duration**: 1-2 weeks  
**Goal**: Establish core architecture and infrastructure  
**Dependencies**: None  
**Deliverables**: Buildable project with DI, error handling, and basic logging

### Tasks
1. **Configure Hilt Dependency Injection**
   - Add Hilt plugin to build.gradle.kts
   - Annotate Application class with @HiltAndroidApp
   - Create base modules for context, coroutines, etc.
   - Verify injection works in test activities

2. **Implement Result<T> Error Handling**
   - Create sealed classes for Result<Success, Error>
   - Add extension functions for Result mapping
   - Implement specific error types (NetworkError, DatabaseError, etc.)
   - Create utility functions for handling common scenarios

3. **Setup Logging Utility**
   - Create Logger class with Android Log integration
   - Add contextual logging (track info, state, actions)
   - Implement manual .txt logging as per AGENTS.md
   - Add debug/release build variants for logging levels

4. **Define Repository Interfaces**
   - Create interfaces for TrackRepository, AlbumRepository, etc.
   - Define data models (Track, Album, Artist, Folder)
   - Add Flow/StateFlow return types for reactive data
   - Include error handling in interface contracts

5. **Configure App Startup**
   - Add App Startup library to dependencies
   - Create Initializer classes for DataStore and Room
   - Ensure async, non-blocking initialization
   - Test cold start performance

### Testing
- Unit tests for Result<T> utilities
- Integration tests for Hilt modules
- Manual verification of app startup timing

### Milestones
- Gradle build succeeds without errors
- Basic app launches without crashes
- Dependency injection verified via test injection

---

## Phase 1: Data Layer Implementation
**Duration**: 2-3 weeks  
**Goal**: Implement Room database with FTS5 search and repository layer  
**Dependencies**: Phase 0 foundation  
**Deliverables**: Fully functional data persistence with search capabilities

### Tasks
1. **Define Room Entities**
   - Track entity: id (primary key), title, artistId, albumId, folderId, duration, filePath, uri, trackNumber, year
   - Album, Artist, Folder entities with relationships
   - Playlist and PlaylistTrack entities for playlist management
   - PlaybackQueue entity for persistent queue state

2. **Implement DAOs with FTS5**
   - Create TrackDao with FTS5 virtual table for search
   - Add paging support (50 items per page) to all DAOs
   - Implement CRUD operations for all entities
   - Add complex queries (by album, artist, folder, playlist)

3. **Setup Room Database**
   - Create HarmonyDatabase class with all entities
   - Configure FTS5 tokenizer for efficient search
   - Implement proper Room migrations (incremental versions)
   - Add database export/import for debugging

4. **Implement Repositories**
   - Concrete implementations of repository interfaces
   - Add Flow-based reactive data streams
   - Implement error handling with Result<T>
   - Add MediaStore integration for initial sync

5. **Database Migration Strategy**
   - Create migration tests for schema changes
   - Implement fallback to full rescan on migration failure
   - Version database appropriately (start at version 1)

### Testing
- Room migration tests
- DAO unit tests with in-memory database
- Repository integration tests
- FTS5 search performance (<10ms queries)

### Milestones
- Database creation and migration successful
- Full library scan completes in <3 seconds for 4,000 tracks
- Search queries return results instantly

---

## Phase 2: Playback Engine
**Duration**: 3-4 weeks  
**Goal**: Implement core playback functionality with Media3  
**Dependencies**: Phase 1 data layer  
**Deliverables**: Functional music playback with queue management

### Tasks
1. **Configure MediaSessionService**
   - Create MediaSessionService extending MediaSessionService
   - Implement foreground service for background playback
   - Add proper lifecycle management (start/stop based on playback state)

2. **Implement MediaController Bridge**
   - Create MediaController in UI layer
   - Bridge playback commands to MediaSessionService
   - Expose playback state via StateFlow

3. **Add Queue Management**
   - Implement queue persistence in Room
   - Add queue manipulation (add, remove, reorder)
   - Track queue metadata (source album/playlist/folder)
   - Implement drag-and-drop reordering

4. **Implement Playback Modes**
   - Shuffle mode with randomization logic
   - Repeat modes (off, all, one)
   - Gapless playback with ConcatenatingMediaSource2
   - Audio focus management with ducking/bypass

5. **Add Media Controls**
   - MediaStyle notification with play/pause/skip
   - Lock screen controls integration
   - Quick Settings tile via MediaSession
   - Seek functionality with timeline display

6. **Audio Processing**
   - Integrate 10-band equalizer into Media3 pipeline
   - Implement custom LoadControl for large RAM buffers
   - Add support for high-resolution audio (24-bit/192kHz)

### Testing
- Unit tests for queue management logic
- Instrumentation tests for MediaSessionService
- Playback integration tests (gapless transitions <10ms)
- Audio focus testing with system interruptions

### Milestones
- Basic playback (play/pause/seek) works
- Queue persists across app restarts
- Gapless playback verified with test tracks
- Media notification appears and controls work

---

## Phase 3: UI Foundation
**Duration**: 3-4 weeks  
**Goal**: Build core UI screens and navigation  
**Dependencies**: Phase 2 playback engine  
**Deliverables**: Navigable app with basic library browsing

### Tasks
1. **Setup Compose Navigation**
   - Configure NavController with Compose
   - Implement navigation graph (Library, Album Detail, Artist Detail, etc.)
   - Add mini-player persistent across screens

2. **Create Material 3 Theme**
   - Implement dynamic color from Material You
   - Setup typography and spacing per Material 3 guidelines
   - Configure hardware bitmap support in Coil
   - Add high-quality caching for album artwork

3. **Build Basic Screens**
   - Library screen with paginated track/album/artist lists
   - Album detail screen with track list
   - Artist detail screen with albums
   - Folder view screen prioritizing file hierarchy

4. **Implement Empty States**
   - No music found state with permission grant option
   - Empty playlist state
   - No search results state
   - Follow Material 3 empty state patterns

5. **Add Loading States**
   - Initial library scan overlay with progress
   - Skeleton loaders for >300ms operations
   - Coil crossfade for album art loading

6. **Now Playing Foundation**
   - Basic Now Playing screen layout
   - Playback controls integration
   - Mini-player implementation

### Testing
- Compose UI tests for navigation flows
- Screenshot tests for Material 3 compliance
- Performance tests for 120Hz scrolling
- Empty state UI tests

### Milestones
- All core screens render correctly
- Navigation between screens works smoothly
- Material You dynamic color applied throughout
- Mini-player visible on all screens except Now Playing

---

## Phase 4: Advanced Features
**Duration**: 4-5 weeks  
**Goal**: Implement premium features and system integration  
**Dependencies**: Phase 3 UI foundation  
**Deliverables**: Feature-complete app with playlists, EQ, and background sync

### Tasks
1. **10-Band Equalizer**
   - Integrate equalizer into Media3 processing pipeline
   - Add preset and custom EQ settings
   - Persist EQ state in DataStore

2. **GPU-Accelerated Effects**
   - Implement blur effects for Now Playing screen
   - Use Modifier.graphicsLayer with renderEffect
   - Ensure 120Hz smoothness on supported devices

3. **Playlist Management**
   - Create/edit/delete playlists functionality
   - Add/remove tracks from playlists
   - Implement M3U export/import
   - Room-based playlist storage

4. **Background Sync with WorkManager**
   - Setup WorkManager for library sync
   - Implement ContentObserver for file changes
   - Add manual rescan trigger in settings

5. **Enhanced Mini-Player**
   - Persistent mini-player with expand/collapse animation
   - Now Playing screen collapses to mini-player on navigation
   - Smooth transitions between states

6. **Bluetooth and System Integration**
   - LDAC optimization for high-bitrate Bluetooth
   - "Pause on Disconnect" listener
   - Audio focus bypass toggle
   - High-resolution audio support

7. **Settings Implementation**
   - Audio settings (gapless, focus bypass, EQ, high-res)
   - Display settings (blur toggle)
   - Data management (rescan, clear cache, playlist export)

### Testing
- Playlist CRUD operations tests
- WorkManager integration tests
- Bluetooth playback tests
- Settings persistence tests

### Milestones
- Playlists fully functional with export/import
- Equalizer affects audio output
- Background sync updates library automatically
- Settings screen complete with all options

---

## Phase 5: Polish and Optimization
**Duration**: 2-3 weeks  
**Goal**: Final polish, search, and comprehensive testing  
**Dependencies**: Phase 4 advanced features  
**Deliverables**: Production-ready app meeting all performance benchmarks

### Tasks
1. **Implement Search Functionality**
   - FTS5-powered search with three sections (albums, artists, tracks)
   - Real-time search-as-you-type
   - Highlight matched text in results
   - Navigate to detail screens from search results

2. **Folder-First Navigation**
   - Prioritize physical file hierarchy
   - Implement folder browsing with track counts
   - Add folder-based playback options

3. **Performance Optimization**
   - Profile and eliminate frame drops during scrolling
   - Optimize memory usage (<100MB target)
   - Ensure cold start optimization
   - Battery efficiency testing (<2% drain/hour)

4. **Comprehensive Testing**
   - Unit tests for all ViewModels and repositories
   - Integration tests for database operations and playlists
   - UI tests for key navigation and playback flows
   - Instrumentation tests for Media3 and service lifecycle

5. **Error Handling and Edge Cases**
   - Robust permission handling (READ_MEDIA_AUDIO, etc.)
   - Corrupted file handling with skip-to-next
   - Database error recovery
   - Network failure handling (if applicable)

6. **Final Code Review and Refactoring**
   - Apply ktlint formatting
   - Remove unused imports and code
   - Ensure adherence to AGENTS.md guidelines
   - Documentation updates

### Testing
- Full test suite passes
- Performance benchmarks met
- Edge case testing (empty library, corrupted files, permissions denied)
- Beta testing with real devices

### Milestones
- Search functionality fully implemented and fast
- All performance benchmarks achieved
- Test coverage >80%
- App ready for release

---

## Risk Mitigation
- **Dependencies**: Use exact versions from SPECFILE.md to avoid compatibility issues
- **Testing**: Implement TDD where possible, especially for data and playback layers
- **Performance**: Regular profiling with Android Studio Profiler
- **Compatibility**: Test on minimum SDK (33) and target SDK (36) devices

## Success Criteria
- App launches in <2 seconds cold start
- Smooth 120Hz scrolling with 4,000+ items
- Gapless playback with <10ms latency
- Memory usage <100MB
- All features from SPECFILE.md implemented
- Comprehensive test coverage
- Material 3 compliance and visual polish

## Timeline Estimation
- Phase 0: Weeks 1-2
- Phase 1: Weeks 3-5
- Phase 2: Weeks 6-9
- Phase 3: Weeks 10-13
- Phase 4: Weeks 14-18
- Phase 5: Weeks 19-21

Total estimated duration: 5-6 months for a 2-3 person team.