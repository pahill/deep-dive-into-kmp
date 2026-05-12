# Plan: "Where is Kodee" App

## Overview

Transform the existing Kotlin Multiplatform project (currently iOS-only, with a placeholder UI) into a
fully featured app called **"Where is Kodee"**. The iOS app will be written in Swift with a SwiftUI UI.
The shared Kotlin module will provide the data layer, exposing a `suspend` function and a `Flow` for
the two main features.

---

## Current State

- `shared/` module: produces a static `Shared.framework` for iOS; contains only `Greeting` and `Platform`
  placeholder code.
- `iosApp/` directory: a single `ContentView.swift` with a demo button that calls `Greeting().greet()`.
- No coroutines or Flow dependencies in the shared module.
- No navigation, no feature screens.

---

## Target Architecture

```
┌─────────────────────────────────────────────────────┐
│                    iOS App (Swift)                  │
│                                                     │
│  iOSApp.swift  ──►  NavigationStack                 │
│                         │                           │
│                    MainView.swift                   │
│                   ┌─────┴──────┐                    │
│           LocateKodeeView   FollowKodeeView          │
│           + ViewModel       + ViewModel             │
└──────────────────────┬──────────────────────────────┘
                       │  imports Shared.framework
┌──────────────────────┴──────────────────────────────┐
│               Shared KMP Module (Kotlin)            │
│                                                     │
│  KodeeLocation (data class)                         │
│  KodeeRepository (interface)                        │
│    ├── suspend fun locateKodee(): KodeeLocation      │
│    └── fun locationHistory(): Flow<List<KodeeLocation>>│
│  KodeeRepositoryImpl (fake/stub implementation)     │
└─────────────────────────────────────────────────────┘
```

---

## Phase 1 — Shared Module

### 1.1 Add Dependencies

Update `shared/build.gradle.kts`:

- Add `kotlinx-coroutines-core` to `commonMain` dependencies. This enables `suspend` functions and
  `Flow` in common code.
- No additional bridging plugin is used. Instead, hand-written Kotlin wrapper classes in the `iosMain`
  source set expose callback-based APIs that Swift can call directly (see section 1.5).

### 1.2 Define the Data Model

Create `shared/src/commonMain/kotlin/.../KodeeLocation.kt`:

```kotlin
data class KodeeLocation(
    val city: String,
    val country: String,
    val venueName: String,
    val imageName: String,  // name of a local asset in the iOS app's Assets.xcassets
    val timestamp: String   // ISO-8601 string; format on the UI layer
)
```

### 1.3 Define the Repository Interface

Create `shared/src/commonMain/kotlin/.../KodeeRepository.kt`:

```kotlin
interface KodeeRepository {
    /** Returns the current location of Kodee. Performs async work. */
    suspend fun locateKodee(): KodeeLocation

    /** Emits the running list of observed locations, newest first. */
    fun locationHistory(): Flow<List<KodeeLocation>>
}
```

### 1.4 Provide a Stub Implementation

Create `shared/src/commonMain/kotlin/.../KodeeRepositoryImpl.kt`:

- Implement `KodeeRepository` with fake/hardcoded data and a simulated network delay
  (`delay(...)`) inside `locateKodee()`.
- `locationHistory()` returns a `StateFlow` (or `MutableStateFlow`) seeded with a fixed list of
  `KodeeLocation` objects so the Follow screen has data to display.
- This stub is intentionally simple — a real network layer can replace it later without touching
  the Swift side.

Use the following sample locations (Munich tourist attractions) as the hardcoded data:

```kotlin
private val sampleLocations = listOf(
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Marienplatz",
        imageName = "kodee_waving",           // Kodee waving hello to tourists
        timestamp = "2024-06-01T10:00:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Englischer Garten",
        imageName = "kodee_sleeping",          // Kodee napping on the grass
        timestamp = "2024-06-01T11:30:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Nymphenburg Palace",
        imageName = "kodee_reading",           // Kodee reading a book in the palace gardens
        timestamp = "2024-06-01T13:00:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Deutsches Museum",
        imageName = "kodee_tinkering",         // Kodee tinkering with a gadget
        timestamp = "2024-06-01T14:45:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Hofbräuhaus",
        imageName = "kodee_eating",            // Kodee enjoying a giant pretzel
        timestamp = "2024-06-01T16:00:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Viktualienmarkt",
        imageName = "kodee_shopping",          // Kodee carrying a market basket
        timestamp = "2024-06-01T17:15:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "BMW Museum",
        imageName = "kodee_driving",           // Kodee sitting behind a steering wheel
        timestamp = "2024-06-01T18:30:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Olympiapark",
        imageName = "kodee_running",           // Kodee jogging around the park
        timestamp = "2024-06-01T19:45:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Munich Residenz",
        imageName = "kodee_painting",          // Kodee painting a royal portrait
        timestamp = "2024-06-02T09:00:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Alte Pinakothek",
        imageName = "kodee_admiring",          // Kodee admiring a painting
        timestamp = "2024-06-02T10:30:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Frauenkirche",
        imageName = "kodee_sightseeing",       // Kodee looking through binoculars at the towers
        timestamp = "2024-06-02T12:00:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Tierpark Hellabrunn",
        imageName = "kodee_feeding_animals",   // Kodee feeding a penguin
        timestamp = "2024-06-02T13:30:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Allianz Arena",
        imageName = "kodee_cheering",          // Kodee cheering with a football scarf
        timestamp = "2024-06-02T15:00:00Z"
    ),
    KodeeLocation(
        city = "Munich",
        country = "Germany",
        venueName = "Schloss Schleissheim",
        imageName = "kodee_cycling",           // Kodee cycling through the palace gardens
        timestamp = "2024-06-02T16:30:00Z"
    ),
)

// locateKodee() picks a random entry from sampleLocations
// locationHistory() seeds the StateFlow with the full list, newest first
```

### 1.5 iOS Coroutine Wrappers

Because `suspend` functions and `Flow` are not directly callable from Swift, two small helper classes
are added to the `iosMain` source set. They own a `CoroutineScope` tied to the main dispatcher and
expose callback-based APIs.

**`SuspendWrapper.kt`** (wraps any `suspend` function):

```kotlin
// iosMain
class SuspendWrapper<T>(
    private val scope: CoroutineScope = MainScope(),
    private val suspendFunction: suspend () -> T
) {
    fun subscribe(
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ): Job = scope.launch {
        try {
            onSuccess(suspendFunction())
        } catch (e: Exception) {
            onError(e)
        }
    }
}
```

**`FlowWrapper.kt`** (wraps any `Flow`):

```kotlin
// iosMain
class FlowWrapper<T>(
    private val scope: CoroutineScope = MainScope(),
    private val flow: Flow<T>
) {
    fun subscribe(
        onEach: (T) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ): Job = scope.launch {
        try {
            flow.collect { onEach(it) }
            onComplete()
        } catch (e: Exception) {
            onError(e)
        }
    }
}
```

Add two convenience factory functions to `KodeeRepositoryImpl` (or a separate `KodeeSDK` façade) so
Swift never constructs wrappers directly:

```kotlin
// iosMain
fun KodeeRepository.locateKodeeWrapper() =
    SuspendWrapper { locateKodee() }

fun KodeeRepository.locationHistoryWrapper() =
    FlowWrapper(flow = locationHistory())
```

### 1.6 Remove Placeholder Code

Delete (or empty out) `Greeting.kt`. `Platform.kt` / `Platform.ios.kt` may be left as-is or also
removed — they are unused by the new feature code.

---

## Phase 2 — iOS App

### 2.1 Update App Entry Point

**File:** `iosApp/iosApp/iOSApp.swift`

- Keep the `@main` struct.
- Hold a `NavigationPath` in `@State` and pass it as a binding to `NavigationStack`. This gives
  programmatic control over the stack and avoids the lazy-loading overhead of `NavigationLink`.
- Register all destinations with `navigationDestination(for: AppRoute.self)` at the root so the
  entire app's routing is declared in one place.

```swift
@main
struct iOSApp: App {
    @State private var path = NavigationPath()

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $path) {
                MainView(path: $path)
                    .navigationDestination(for: AppRoute.self) { route in
                        switch route {
                        case .locateKodee: LocateKodeeView()
                        case .followKodee: FollowKodeeView()
                        }
                    }
            }
        }
    }
}
```

### 2.2 Main Screen

**File:** `iosApp/iosApp/MainView.swift` (replaces `ContentView.swift`)

Define a `Hashable` route enum in a dedicated file:

```swift
// AppRoute.swift
enum AppRoute: Hashable {
    case locateKodee
    case followKodee
}
```

`MainView` receives the `NavigationPath` binding from `iOSApp` and uses plain `Button`s to append
routes to it. This avoids any `NavigationLink` coupling and keeps destination logic in `iOSApp`.

Layout: a centered `VStack` with the app title "Where is Kodee" and two buttons:

```swift
struct MainView: View {
    @Binding var path: NavigationPath

    var body: some View {
        VStack {
            Text("Where is Kodee").font(.largeTitle)
            Button("Locate Kodee") { path.append(AppRoute.locateKodee) }
            Button("Follow Kodee") { path.append(AppRoute.followKodee) }
        }
    }
}
```

No ViewModel is needed for this screen; it holds no state beyond the shared path binding.

### 2.3 Locate Kodee Screen

#### 2.3.1 View — `LocateKodeeView.swift`

Two mutually exclusive UI states driven by `@StateObject var viewModel: LocateKodeeViewModel`:

**Loading state** (while the async call is in-flight):

- A `ProgressView` (spinner) centered on screen.
- Optionally a "Searching for Kodee…" label beneath it.

**Result state** (once data is available):

- `Image(location.imageName)` loading the illustration from the local asset catalogue.
- `Text` fields for `venueName`, `city`, `country`, and `timestamp`.
- A **"Locate again"** `Button` that triggers a fresh call to `viewModel.locate()`,
  resetting back to the loading state.

A `navigationTitle("Locate Kodee")` and default back button (provided free by `NavigationStack`)
handle navigation back to the main screen.

**State enum inside the ViewModel (or view):**
```swift
enum LocateState {
    case loading
    case result(KodeeLocation)
}
```

#### 2.3.2 ViewModel — `LocateKodeeViewModel.swift`

```swift
@MainActor
class LocateKodeeViewModel: ObservableObject {
    @Published var state: LocateState = .loading
    private let repository: KodeeRepository

    init(repository: KodeeRepository = KodeeRepositoryImpl())

    func locate() {
        state = .loading
        repository.locateKodeeWrapper().subscribe(
            onSuccess: { [weak self] location in
                self?.state = .result(location)
            },
            onError: { _ in
                // handle error state if needed
            }
        )
    }
}
```

- `locate()` is called once in `.task { }` on the view's appearance, and again when "Locate again"
  is tapped.
- `locateKodeeWrapper()` is the `SuspendWrapper` extension defined in `iosMain`; it launches the
  coroutine on the main dispatcher and delivers the result via the `onSuccess` callback.

### 2.4 Follow Kodee Screen

#### 2.4.1 View — `FollowKodeeView.swift`

- A `List` where each row shows:
  - `Text` for `venueName` (primary label)
  - `Text` for `city`, `country` (secondary label)
  - `Text` for `timestamp` (trailing/caption label)
- Data comes from `@StateObject var viewModel: FollowKodeeViewModel` via a `@Published` array.
- The list updates reactively as the Flow emits new lists.
- A `navigationTitle("Follow Kodee")` and default back button handle navigation.

#### 2.4.2 ViewModel — `FollowKodeeViewModel.swift`

```swift
@MainActor
class FollowKodeeViewModel: ObservableObject {
    @Published var locations: [KodeeLocation] = []
    private let repository: KodeeRepository

    init(repository: KodeeRepository = KodeeRepositoryImpl())

    func startObserving() {
        repository.locationHistoryWrapper().subscribe(
            onEach: { [weak self] locationList in
                self?.locations = locationList
            },
            onComplete: { },
            onError: { _ in }
        )
    }
}
```

- `startObserving()` is called from `.onAppear` (or `.task { }`) on the view's appearance.
- `locationHistoryWrapper()` is the `FlowWrapper` extension defined in `iosMain`; it collects the
  Flow on the main dispatcher and fires `onEach` on every emission.

---

## Phase 3 — App Naming & Assets

- Update `Config.xcconfig`: set `PRODUCT_NAME = WhereIsKodee`.
- Update `PRODUCT_BUNDLE_IDENTIFIER` to `com.jetbrains.whereiskodee`.
- Replace the default app icon in `Assets.xcassets/AppIcon.appiconset/` with a Kodee-themed icon
  (placeholder asset at minimum).
- Add 14 image assets to `Assets.xcassets`, one per location. Each image is an illustration of
  Kodee at the matching venue, doing a different activity:

  | Asset name                  | Kodee activity                          |
  |-----------------------------|-----------------------------------------|
  | `kodee_waving`              | Waving hello to tourists                |
  | `kodee_sleeping`            | Napping on the grass                    |
  | `kodee_reading`             | Reading a book in the palace gardens    |
  | `kodee_tinkering`           | Tinkering with a gadget                 |
  | `kodee_eating`              | Enjoying a giant pretzel                |
  | `kodee_shopping`            | Carrying a market basket                |
  | `kodee_driving`             | Sitting behind a steering wheel         |
  | `kodee_running`             | Jogging around the park                 |
  | `kodee_painting`            | Painting a royal portrait               |
  | `kodee_admiring`            | Admiring a painting in a gallery        |
  | `kodee_sightseeing`         | Looking through binoculars at the towers|
  | `kodee_feeding_animals`     | Feeding a penguin at the zoo            |
  | `kodee_cheering`            | Cheering with a football scarf          |
  | `kodee_cycling`             | Cycling through the palace gardens      |

  Each asset should be provided at 1×, 2×, and 3× resolutions in its `.imageset` folder.
  Use `Image("kodee_waving")` etc. in SwiftUI — no `AsyncImage` needed since all images are local.

---

## File Change Summary

| Action | File |
|--------|------|
| Modify | `shared/build.gradle.kts` — add `kotlinx-coroutines-core` |
| Create | `shared/src/iosMain/.../SuspendWrapper.kt` |
| Create | `shared/src/iosMain/.../FlowWrapper.kt` |
| Create | `shared/src/commonMain/.../KodeeLocation.kt` |
| Create | `shared/src/commonMain/.../KodeeRepository.kt` |
| Create | `shared/src/commonMain/.../KodeeRepositoryImpl.kt` |
| Delete | `shared/src/commonMain/.../Greeting.kt` |
| Modify | `iosApp/iosApp/iOSApp.swift` — add `NavigationStack` with `NavigationPath` and `navigationDestination` |
| Create | `iosApp/iosApp/AppRoute.swift` — `Hashable` route enum |
| Replace | `iosApp/iosApp/ContentView.swift` → `MainView.swift` |
| Create | `iosApp/iosApp/LocateKodeeView.swift` |
| Create | `iosApp/iosApp/LocateKodeeViewModel.swift` |
| Create | `iosApp/iosApp/FollowKodeeView.swift` |
| Create | `iosApp/iosApp/FollowKodeeViewModel.swift` |
| Modify | `iosApp/Configuration/Config.xcconfig` — rename app |
| Modify | `iosApp/iosApp.xcodeproj/project.pbxproj` — add new Swift files |

---

## Key Decisions & Trade-offs

| Decision | Choice                                                                                            | Notes |
|----------|---------------------------------------------------------------------------------------------------|-------|
| Flow → Swift bridge | **`FlowWrapper` (manual, `iosMain`)** — callback-based, no plugin required                        | Delivers emissions via `onEach` lambda on the main dispatcher |
| Suspend → Swift bridge | **`SuspendWrapper` (manual, `iosMain`)** — callback-based, no plugin required                     | Delivers result via `onSuccess`/`onError` lambdas |
| Repository injection | Pass `KodeeRepositoryImpl()` as default parameter in ViewModel                                    | Use a simple service locator or SwiftUI `@EnvironmentObject` |
| Navigation | `NavigationStack` + `NavigationPath` (iOS 16+) — programmatic, single `navigationDestination` at root | `NavigationView` with `NavigationLink` for broader iOS compatibility |
| Timestamp formatting | Store as ISO-8601 string in shared; format with `DateFormatter` in Swift                          | Format in shared code using `kotlinx-datetime` |