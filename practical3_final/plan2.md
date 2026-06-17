# Plan 2: Live Timestamps and Growing Location History

## Overview

Two changes to `KodeeRepositoryImpl` in the shared module:

1. **Live timestamps** — remove hardcoded date strings from `sampleLocations`; stamp the real
   current time onto each `KodeeLocation` at the moment it is returned by `locateKodee()` or
   emitted by `locationHistory()`.

2. **Growing history** — rewrite `locationHistory()` so the `Flow` builds the list up one entry
   at a time, inserting a delay between each emission, so the UI list visibly grows.

No Swift/iOS changes are required; the `KodeeLocation.timestamp` field stays a `String` and the
`FlowWrapper` / `KodeeSDK` interfaces are unchanged.

---

## Change 1 — Current-time helper

### 1.1 Add `kotlinx-datetime` dependency

`kotlinx-datetime` is the standard KMP library for platform-independent date/time. Add it to the
version catalog and the shared module so `commonMain` code can call `Clock.System.now()`.

**`gradle/libs.versions.toml`** — add:
```toml
[versions]
kotlinxDatetime = "0.6.1"

[libraries]
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinxDatetime" }
```

**`shared/build.gradle.kts`** — add to `commonMain.dependencies`:
```kotlin
implementation(libs.kotlinx.datetime)
```

### 1.2 Add `currentTimestamp()` helper

Add a private top-level (or companion) function inside `KodeeRepositoryImpl` (or in a small shared
utility file) that returns the current instant as an ISO-8601 string:

```kotlin
import kotlinx.datetime.Clock

private fun currentTimestamp(): String = Clock.System.now().toString()
// example output: "2024-06-01T10:00:00.000000000Z"
```

---

## Change 2 — Remove hardcoded timestamps from `sampleLocations`

The `sampleLocations` list exists only to hold static venue data. Timestamps in that list are
meaningless because they will always be overwritten at call time.

Replace the `timestamp` argument in every `KodeeLocation(...)` constructor call in `sampleLocations`
with an empty string `""`:

```kotlin
private val sampleLocations = listOf(
    KodeeLocation("Munich", "Germany", "Marienplatz",          "kodee_waving",          ""),
    KodeeLocation("Munich", "Germany", "Englischer Garten",    "kodee_sleeping",        ""),
    KodeeLocation("Munich", "Germany", "Nymphenburg Palace",   "kodee_reading",         ""),
    // … all 14 entries with "" for timestamp
)
```

The `""` value will never reach the UI — every code path that returns a `KodeeLocation` to the
caller will call `.copy(timestamp = currentTimestamp())` before emitting.

---

## Change 3 — Stamp current time in `locateKodee()`

After picking a random entry, copy it with the live timestamp before returning:

```kotlin
override suspend fun locateKodee(): KodeeLocation {
    delay(1500L)
    return sampleLocations[Random.nextInt(sampleLocations.size)]
        .copy(timestamp = currentTimestamp())
}
```

---

## Change 4 — Rewrite `locationHistory()` as a growing Flow

### Current behaviour
`locationHistory()` returns a `MutableStateFlow` pre-seeded with all 14 locations reversed.
The full list is emitted once, immediately.

### New behaviour
`locationHistory()` returns a cold `flow { }` that:
1. Starts with an empty accumulator list.
2. Loops through `sampleLocations` in order.
3. Waits a random delay between **1 000 ms and 5 000 ms** before each entry using
   `Random.nextLong(1_000L, 5_001L)`.
4. Stamps the current time onto the next location with `.copy(timestamp = currentTimestamp())`.
5. Prepends the new entry to the accumulator (newest first).
6. Emits the updated list.

The subscriber sees the list grow by one entry at irregular intervals, each between 1 and 5 seconds.

```kotlin
override fun locationHistory(): Flow<List<KodeeLocation>> = flow {
    val accumulated = mutableListOf<KodeeLocation>()
    for (sample in sampleLocations) {
        delay(Random.nextLong(1_000L, 5_001L))
        accumulated.add(0, sample.copy(timestamp = currentTimestamp()))
        emit(accumulated.toList())
    }
}
```

### Consequences for the iOS side
- `FollowKodeeViewModel.locations` starts empty and gains one row every 2 seconds — no Swift
  changes needed; `FlowWrapper.subscribe(onEach:…)` already forwards every emission.
- The `List` in `FollowKodeeView` will animate new rows in automatically because SwiftUI diffs
  `@Published` array changes.

### Remove the now-unused `MutableStateFlow` field
Delete the line:
```kotlin
private val _locationHistory = MutableStateFlow(sampleLocations.reversed())
```
It is no longer referenced.

---

## File Change Summary

| Action | File |
|--------|------|
| Modify | `gradle/libs.versions.toml` — add `kotlinxDatetime` version + library entry |
| Modify | `shared/build.gradle.kts` — add `kotlinx.datetime` to `commonMain` |
| Modify | `shared/src/commonMain/.../KodeeRepositoryImpl.kt` — all four changes above |
