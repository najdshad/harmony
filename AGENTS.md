# harmony Music Player - Agent Guidelines

## Environment & Network

**Setup:** Java 17 at `/usr/lib/jvm/java-17-openjdk/`, Android SDK at `/home/najdu/android-sdk`
**Proxy:** All network access must use `http://127.0.0.1:10808`

## Build & Test Commands

```bash
# Build & test
./gradlew build
./gradlew test                          # All unit tests
./gradlew test --tests "com.harmony.player.TestClassName"
./gradlew test --tests "com.harmony.player.TestClassName.testMethodName"
./gradlew connectedAndroidTest          # Instrumentation tests
./gradlew lint                          # Android lint

# Build & install
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew installDebug
./gradlew clean
```

## Code Style - Kotlin Basics

**Imports:** Group as stdlib → AndroidX → third-party → project (alphabetically). Use `*` only for 5+ imports or tests. No unused imports.

**Formatting:** 4-space indent, 120 char max line, trailing commas in Compose.

**Types:** `val` over `var`, data classes for values, `sealed` for hierarchies. Avoid `!!` - use safe calls `?`. Use `Flow` for streams, `suspend` for coroutines.

**Naming:** Functions: `camelCase` verbs (`playTrack()`, `loadAlbums()`). Variables: `camelCase`. Constants: `UPPER_SNAKE_CASE`. Classes: `PascalCase`. Composables: `PascalCase`. Private backing: `_propertyName`.

**Error Handling:** Use `Result<T>` for fallible ops. Never catch generic `Exception` - catch specific types. Log with Android Log using package/class tags. Show user-friendly messages, no stack traces. Wrap Media3 ops in try/catch.

**Architecture:** MVVM with ViewModels, Repository pattern, Hilt DI. State: `remember{}` in Composables, `StateFlow` in ViewModels. Single source of truth. Keep composables pure - no business logic in UI.

## Code Style Guidelines

### Compose-Specific

- Prefer `Modifier` chaining at end of parameters
- Use `remember{ }` for expensive calculations, `derivedStateOf{ }` for computed state
- Extract child composables to avoid deep nesting
- `LaunchedEffect` for side effects on composition, `SideEffect` for non-suspend

## Key Libraries & Standards

**Versions:** Kotlin 2.1.0, Gradle 8.13, AGP 8.13.2, Min SDK 33, Target SDK 35

**Audio (Media3 1.4.0):** Use `MediaSessionService` for background playback. Audio focus with ducking. Support MP3, AAC, FLAC, ALAC, WAV, OGG, OPUS, M4A, WMA. Pause on Bluetooth disconnect.

**Database (Room 2.6.1):** FTS5 tables for search (<10ms). Sync with MediaStore via WorkManager + ContentObserver. KSP for annotation processing.

**UI (Material 3 1.2.1, Compose):** Dynamic color from Material You. Hardware bitmaps via Coil 3.3.0. Edge-to-edge UI required. Persistent mini-player on all screens.

**DI (Hilt 2.51.1):** `@HiltViewModel` for ViewModels, `@AndroidEntryPoint` for Activities/Fragments/Services, `@Inject` for constructor injection. Use `@Module` + `@InstallIn` for dependency modules.

**Bluetooth:** "Pause on Disconnect" listener, graceful state handling. Request `BLUETOOTH_CONNECT` at runtime.

**Permissions:** `READ_MEDIA_AUDIO` (Android 13+), `POST_NOTIFICATIONS` (Android 13+), `BLUETOOTH_CONNECT`. Handle partial access gracefully.

**Settings/Init:** DataStore 1.1.2 for persistence, App Startup 1.2.0 for deferred init (DataStore, Room). Never block main thread with DB/I/O. Lazy MediaSessionService init.

**Logging:** Manual `.txt` logging (no cloud). Include context: track info, state, action. Keep localized.

**Build:** Namespace in build.gradle.kts, R8 full mode for release. ProGuard rules for Media3/Room/Hilt.

## Testing

Unit: JUnit 4 + MockK for Repository/ViewModel. Coroutines with kotlinx-coroutines-test. Room DB ops with room-testing artifact.
UI: Compose Testing for navigation/playback.
Instrumentation: Espresso for Media3 playback/service lifecycle.
Test DataStore persistence and M3U playlist parsing.

## Performance Requirements

- Smooth scrolling for 10k+ items - `LazyColumn` with keys
- Fast cold start - defer non-critical init with App Startup
- Efficient memory usage - `Bitmap.Config.HARDWARE` images via Coil
- Gapless playback - `ConcatenatingMediaSource2` with 90% pre-buffer
