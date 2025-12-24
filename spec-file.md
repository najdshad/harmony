# Technical Specification: harmony Music Player

## 1. Vision & Core Philosophy

**harmony** is built for the listener who values audio integrity, visual elegance, and instant responsiveness. It treats a 4,000+ track library as a lightweight collection, ensuring the hardware is dedicated to one thing: **reproducing music perfectly.**

* **Opinionated Defaults:** Gapless playback is mandatory; Audio focus is strict; UI is "flat" for speed.
* **Performance:** 120Hz scrolling, <500ms cold start, and minimal CPU/Battery footprint.
* **Aesthetic:** Pure Material 3 with "Material You" dynamic coloring driven by album art.

---

## 2. Audio Engine (The Audiophile Core)

The playback engine is built on **Jetpack Media3 (ExoPlayer)**, configured for bit-perfect delivery.

* **Gapless Playback:** * Uses `ConcatenatingMediaSource2` for zero-latency transitions.
* Pre-buffers the next track when the current track reaches 90% completion.

* **High-Resolution Support:** * Direct `AudioTrack` routing for 24-bit/192kHz FLAC, ALAC, and WAV.
* Automatic bypass of the Android system mixer (where hardware allows) to prevent resampling.

* **Buffer Management:** * Custom `LoadControl` to keep a larger chunk of the current track in RAM, reducing disk spin-up and battery drain.

---

## 3. Large-Scale Library Management (4,000+ Tracks)

To handle massive libraries without "jank," harmony uses a reactive data pipeline.

### A. Data Architecture

* **Hybrid Media Store:** Uses a **Room Database** to mirror `MediaStore`. Room stores custom metadata (Play counts, Favorites, Audiophile tags) while keeping a fast index of file paths.
* **Paging 3 Integration:** Songs are loaded in "pages" of 50. This prevents the app from trying to allocate memory for 4,000 objects at once.
* **Fast-Index Scrolling:** A custom Material 3 scrollbar that shows alphabetical headers (A...Z) for instant jumping through large lists.

### B. Search Engine

* **In-Memory Indexing:** A lightweight search index is built on launch, allowing for sub-10ms "search-as-you-type" results.

---

## 4. UI/UX & Visual Identity

The design follows the **Material 3 (M3)** spec, emphasizing depth, motion, and personalization.

### A. Material You & Dynamic Color

* **Content-Based Theming:** The entire UI palette (buttons, backgrounds, sliders) dynamically shifts to match the **dominant color** of the current album art.
* **Glassmorphism:** The "Now Playing" screen uses a multi-layered approach with a high-quality blurred version of the album art as the base layer.

### B. High-Efficiency Imagery

* **Hardware Bitmaps:** Using **Coil** with `Bitmap.Config.HARDWARE` to offload image rendering to the GPU, keeping the JVM heap clean.
* **Layered Caching:** 1.  Memory Cache (Active views)

1. Disk Cache (Downsampled thumbnails)
2. Source (Original high-res art)

---

## 5. Technical Stack

| Component | Technology | Purpose |
| --- | --- | --- |
| **Language** | Kotlin | Modern, safe, and expressive. |
| **UI Framework** | Jetpack Compose | Declarative UI for smooth transitions and M3 support. |
| **Media Library** | Media3 + ExoPlayer | Standard for modern Android media handling. |
| **Persistence** | Room DB | Metadata caching and playlist management. |
| **DI** | Hilt | Dependency injection for clean, testable code. |
| **Image Loading** | Coil | Fast, lightweight, and hardware-accelerated. |
| **Async** | Coroutines & Flow | Non-blocking I/O and reactive UI updates. |

---

## 6. Implementation Strategy (Roadmap)

### Phase 1: The High-Res Foundation

* Configure `MediaSessionService` and `ExoPlayer`.
* Implement custom `RenderersFactory` for high-bitrate audio.
* Enable gapless logic and `AudioTrack` optimizations.

### Phase 2: The Data Pipeline

* Build the `MediaStore` synchronizer with **Room**.
* Implement **Paging 3** to feed the `LazyColumn` in the Library view.
* Optimize the Fast-Scroll behavior.

### Phase 3: The harmony Visuals

* Build the "Now Playing" screen with M3 Dynamic Color extraction.
* Implement the "Mini-player" with swipe-to-skip gestures.
* Apply hardware-accelerated image loading.

---

## 7. Performance Benchmarks (Success Metrics)

* **Frame Drops:** Zero drops during rapid scrolling of 4,000+ items.
* **Gapless Latency:** < 10ms transition between tracks.
* **Memory Usage:** Stay under 100MB RAM even with high-res artwork displayed.

---
