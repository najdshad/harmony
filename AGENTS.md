# harmony Music Player - Agent Guidelines

## Development Environment

**Confirmed Working Setup:**
- Java 17 installed at `/usr/lib/jvm/java-17-openjdk/`
- Android SDK 36 (API 16) available at `/home/najdu/android-sdk`
- Build system configured and tested successfully
- APK generation working

**Java Configuration:**
- Project uses Java 17 toolchain (default for Kotlin 2.2.20)
- Kotlin compiles to Java 17 bytecode
- Android runtime (ART) compatible with Java 17
- No additional configuration needed - Gradle auto-detects Java 17

## Network Configuration

**All network access must use proxy:** `http://127.0.0.1:10808`

When running commands that require network access (gradle sync, dependency downloads, etc.), ensure this proxy is configured.

## Engineering Standards

**Act as a senior software engineer throughout all tasks.** Prioritize writing high-quality, production-ready code with thorough testing, proper error handling, and well-architected solutions. Never take shortcuts that compromise code quality, maintainability, or performance. All implementations must be complete, robust, and follow established best practices.

## Build & Test Commands

### Gradle Commands

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.harmony.player.TestClassName"

# Run specific test method
./gradlew test --tests "com.harmony.player.TestClassName.testMethodName"

# Run instrumentation tests (connected to device/emulator)
./gradlew connectedAndroidTest

# Run lint checks
./gradlew lint

# Format code with ktlint
./gradlew ktlintFormat

# Check code style with ktlint
./gradlew ktlintCheck

# Clean build artifacts
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device
./gradlew installDebug
```

## Technology Stack

**Versions (December 2025):**

- **Kotlin**: 2.2.20
- **Gradle**: 8.13
- **Android Gradle Plugin**: 8.13.2
- **Compile SDK**: 36 (Android 16)
- **Target SDK**: 36 (Android 16)
- **Min SDK**: 33 (Android 13)
- **Compose BOM**: 2024.10.01
- **Compose Compiler**: 1.5.8
- **Material 3**: 1.2.1
- **Media3**: 1.4.0
- **Room**: 2.6.1
- **Hilt**: 2.51.1
- **KSP**: 2.2.20-2.0.4
- **Coil**: 3.3.0
- **Paging 3**: 3.3.1
- **Work Manager**: 2.9.0

## Code Style Guidelines

### Kotlin & Jetpack Compose

**Imports:**

- Group imports: standard library, Android/AndroidX, third-party, project (alphabetically within groups)
- Use `*` imports only for test files or when importing 5+ items from same package
- No unused imports - run `./gradlew ktlintCheck` to verify

**Formatting:**

- Use 4-space indentation (Kotlin default)
- Max line length: 120 characters
- Use trailing commas in Compose functions for better diffs
- Prefer explicit `@Composable` annotations

**Types:**

- Use `val` for immutable references, `var` only when necessary
- Prefer data classes for value objects
- Use `sealed` classes/interfaces for limited type hierarchies
- Avoid `!!` operator - use safe calls (`?.`) and explicit null checks
- Use `Flow` for async streams, `suspend` functions for coroutines

**Naming Conventions:**

- Functions/methods: `camelCase` with verbs (`playTrack()`, `loadAlbums()`)
- Variables: `camelCase`
- Constants: `UPPER_SNAKE_CASE` at top level or in companion objects
- Classes: `PascalCase`
- Composables: `PascalCase` with descriptive names (`NowPlayingScreen`, `TrackItem`)
- Private properties: `camelCase` with underscore prefix if backing property (`_currentTrack`)

**Error Handling:**

- Use `Result<T>` for operations that can fail
- Never catch generic `Exception` - catch specific exceptions
- Log errors with Android Log using package/class-based tags
- Show user-friendly error messages in UI (no stack traces)
- Use `try/catch` around Media3 operations and handle gracefully

**Architecture Patterns:**

- Follow MVVM pattern with ViewModels
- Use Repository pattern for data access
- Use Hilt for dependency injection
- State management with `remember{ }` in Composables or `StateFlow` in ViewModels
- Single source of truth for UI state
- Keep composables pure - no business logic in UI layer

**Compose-Specific:**

- Prefer `Modifier` chaining at the end of parameters
- Use `remember{ }` for expensive calculations
- Use `derivedStateOf{ }` for computed state
- Avoid deep nesting - extract child composables
- Use `LaunchedEffect` for side effects on composition
- Use `SideEffect` for non-suspend side effects

**Performance Requirements:**

- 120Hz smooth scrolling required - use `LazyColumn`/`LazyRow` with key parameters
- Cold start < 500ms - defer non-critical initialization
- Memory under 100MB - use `Bitmap.Config.HARDWARE` for images via Coil
- Gapless playback < 10ms - use `ConcatenatingMediaSource2` with 90% pre-buffer

**Audio Engine (Media3 1.9.0):**

- Always use `MediaSessionService` for background playback
- Configure `LoadControl` for large RAM buffer chunks
- Implement audio focus with ducking and bypass toggle
- Prefer high-bitrate LDAC for Bluetooth
- Support 24-bit/192kHz via direct `AudioTrack` routing
- Use `media3-ui-compose-material3` for Compose UI components

**Database (Room 2.7.2):**

- Use FTS5 tables for search (sub-10ms queries)
- Implement pagination with Paging 3 (page size: 50)
- Sync Room DB with MediaStore using WorkManager + ContentObserver
- Folder-first view prioritization
- Use KSP for annotation processing

**UI Guidelines (Material 3 1.5.0):**

- Use dynamic color tokens from Material You (system theme)
- Apply GPU-accelerated blurs with `Modifier.graphicsLayer { renderEffect }`
- Hardware bitmaps for artwork via Coil 3.3.0
- Follow Material 3 spacing and typography guidelines
- Implement edge-to-edge UI (required for Android 16)

**Dependency Injection (Hilt 2.55):**

- Use `@HiltViewModel` for ViewModels
- Use `@AndroidEntryPoint` for Activities, Fragments, and Services
- Use `@Inject` for constructor injection
- Use `@Module` and `@InstallIn` for dependency modules
- Use Hilt Worker factory for WorkManager

**Bluetooth:**

- Implement "Pause on Disconnect" listener
- Prefer LDAC codec at highest bitrate
- Handle Bluetooth state changes gracefully
- Request `BLUETOOTH_CONNECT` permission at runtime

**Logging:**

- Use manual `.txt` logging for diagnostics (no cloud crash reporting)
- Include context: track info, state, action being performed
- Keep logs localized for privacy

**Permissions:**

- Handle `READ_MEDIA_AUDIO` for Android 13+
- Gracefully handle partial media access
- Request permissions at appropriate UX moments
- Handle `POST_NOTIFICATIONS` for Android 13+

**Build Configuration:**

- Use namespace in build.gradle.kts instead of manifest package
- Enable R8 full mode for release builds
- Configure ProGuard rules for Media3, Room, and Hilt
- Use buildConfigField for different configurations

**Testing:**

- Write unit tests with JUnit 4 and MockK
- Write Compose UI tests with Compose Testing
- Write instrumentation tests with Espresso
- Test Room database with room-testing artifact
- Test coroutines with kotlinx-coroutines-test
