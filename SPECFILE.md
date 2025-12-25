---

# Technical Specification: harmony Music Player (Revised)

## 1. Vision & Core Philosophy

**harmony** is designed for listeners who value audio integrity, visual elegance, and instant responsiveness. It treats massive libraries as lightweight collections, ensuring hardware is dedicated to reproducing music perfectly.

* **Opinionated Defaults:** Gapless playback is mandatory; UI is "flat" for speed.
* **Performance:** 120Hz scrolling, <500ms cold start, and minimal battery footprint.
* **Aesthetic:** Pure Material 3 utilizing system-wide **Material You** dynamic coloring.

---

## 2. Audio Engine (ExoPlayer Foundation)

The engine is built on **Jetpack Media3 (ExoPlayer)**.

### A. Playback & Focus

* **Gapless Playback:** Uses `ConcatenatingMediaSource2` for zero-latency transitions and pre-buffers the next track at 90% completion.
* **Audio Focus Logic:** * **Ducking:** Automatically lowers volume during notifications.
* **Focus Bypass:** A user-toggleable option to ignore focus loss, preventing other apps from interrupting the stream.

* **High-Resolution Support:** Direct `AudioTrack` routing for 24-bit/192kHz FLAC, ALAC, and WAV.

### B. Signal Processing

* **10-Band Equalizer:** A high-precision digital EQ integrated into the Media3 processing pipeline.
* **Buffer Management:** Custom `LoadControl` to keep large chunks in RAM, reducing disk spin-up.

---

## 3. Library & Data Management

Optimized for 4,000+ tracks using a reactive data pipeline.

### A. Data Architecture

* **Hybrid Media Store:** A **Room Database** mirrors `MediaStore`.
* **Room FTS5:** Implements Full-Text Search (FTS5) for sub-10ms "search-as-you-type" across the entire library.
* **Paging 3:** Songs and folders are loaded in blocks of 50 to maintain low memory overhead.

### B. Navigation & Sync

* **Folder-First View:** Prioritizes physical file hierarchy over metadata-only views.
* **File Watcher:** Uses **WorkManager** and `ContentObserver` to detect new files added via PC/file manager and sync them to the Room DB automatically.
* **Permissions:** Robust handling of `READ_MEDIA_AUDIO` for Android 13+ and partial media access.

---

## 4. UI/UX & Visual Identity

The UI follows the **Material 3 (M3)** spec with a focus on system integration.

### A. System-Driven Design

* **Material You:** The app uses system-wide dynamic color tokens (set by the user's wallpaper) for the entire UI palette.
* **Hardware Bitmaps:** Coil uses `Bitmap.Config.HARDWARE` to offload image rendering to the GPU.

### B. GPU-Accelerated Effects

* **Native Glassmorphism:** The "Now Playing" screen uses `Modifier.graphicsLayer { renderEffect = ... }` for GPU-accelerated blurs (Android 12+), ensuring 120Hz smoothness.

---

## 5. Technical Stack

| Component | Technology | Purpose |
| --- | --- | --- |
| **Language** | Kotlin | Modern, safe, and expressive. |
| **UI Framework** | Jetpack Compose | Declarative UI for smooth transitions. |
| **Media Library** | Media3 + ExoPlayer | Modern Android media handling. |
| **Persistence** | Room DB + FTS5 | Metadata caching and lightning-fast search. |
| **Connectivity** | Bluetooth Stack | Optimized for high-bitrate **LDAC** codec. |
| **Diagnostics** | Manual `.txt` Logging | Localized crash logs for privacy and simplicity. |
| **Media Control** | MediaSessionService | System and background playback control. |

---

## 6. Connectivity & System Integration

* **LDAC Optimization:** Configured to prefer the highest possible bitrate for Bluetooth LDAC connections.
* **Bluetooth Listeners:** Implements "Pause on Disconnect" to prevent accidental speaker playback.
* **No Scrobbling:** External scrobbling (Last.fm, etc.) is explicitly excluded to keep the stack lean.

---

## 7. Implementation Roadmap

* **Phase 1:** Configure `MediaSessionService` and implement the 10-band EQ pipeline.
* **Phase 2:** Build the Room FTS5 index and the Folder-View navigation using Paging 3.
* **Phase 3:** Finalize M3 Dynamic Color integration and the GPU-accelerated blur effects.

---

## 8. Performance Benchmarks (Success Metrics)

* **Frame Drops:** Zero drops during rapid scrolling of 4,000+ items.
* **Gapless Latency:** < 10ms transition between tracks.
* **Memory Usage:** Under 100MB RAM even with hardware-accelerated artwork.
* **Audio Buffering:** Zero underruns during background-to-foreground transitions.
* **Battery Efficiency:** < 2% drain per hour of screen-off playback.
* **Index Speed:** Full library rebuild (4,000 tracks) in < 3 seconds.

---
