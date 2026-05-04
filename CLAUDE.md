# CLAUDE.md

Context for Claude Code sessions on this project. Read `PRD.md` alongside this file for full scope.

---

## What we're building

A free golf GPS app for Wear OS (Galaxy Watch 7 is the primary target) with an Android companion phone app. Launch market is Sweden. Data comes from open sources — OpenStreetMap for course geometry, SMHI for weather. No paid course APIs, no subscriptions, no accounts in v1.

v1 features: illustrated hole map, distance to front/center/back of green, wind, auto-hole detection. See `PRD.md` for the full scope and what's out.

---

## Critical architectural rules

### The watch must use its own GPS

This is non-negotiable. Golfers leave the phone in the bag or cart. The watch app must work standalone during a round:

- Use the watch's built-in GPS via `FusedLocationProviderClient` running on the watch itself
- Phone is only used for course discovery, downloading geometry, and pushing it to the watch before the round starts
- Once a round is active, the watch should function with the phone out of Bluetooth range
- Wind data is fetched by the phone on round start and pushed to the watch; if the phone is unreachable mid-round, the last cached wind reading is fine
- Never make the watch depend on phone-relayed GPS. That's a battery and reliability disaster.

This means: declare `android.hardware.location.gps` as required in the wear module manifest, request `ACCESS_FINE_LOCATION` on the watch, and verify GPS works on the Galaxy Watch 7 specifically (it does — Watch 7 has standalone GPS).

### Phone-watch sync uses Wearable Data Layer API

- Phone packages course data → sends via `DataClient` to watch
- Watch receives via `DataClient.OnDataChangedListener`
- Payloads must stay under 100KB; simplify polygons if needed before sending
- Use `MessageClient` for small ephemeral signals (e.g., "round started"), `DataClient` for the course payload

### Backend is minimal

A single small service that runs nightly to snapshot all Swedish `leisure=golf_course` ways from Overpass into a static JSON file. Clients fetch from us, never from Overpass directly. Keeps Overpass happy and gives us a stable course list. Cloudflare Worker + R2 or similar — no database needed.

---

## Tech stack (don't deviate without discussing)

| Layer | Choice |
|---|---|
| Language | Kotlin everywhere |
| Phone UI | Jetpack Compose |
| Watch UI | Compose for Wear OS |
| Min SDK | 30 (phone), 30 (Wear OS 3+) |
| Target SDK | 35 |
| Phone DB | Room |
| Watch DB | DataStore (Proto preferred) |
| Phone-watch sync | Wearable Data Layer API |
| Maps | Compose Canvas, custom drawing — no Google Maps, no MapBox, no tile servers |
| Networking | Ktor client (consistent with Kotlin Multiplatform if we go that route) |
| Serialization | kotlinx.serialization |
| Course data | OSM via Overpass API (through our backend) |
| Weather | SMHI Öppna data API |
| DI | Hilt on phone, manual on watch (keep watch lean) |

---

## Repo structure

```
golf-watch-app/
├── mobile/            # Phone companion app (Android)
├── wear/              # Watch app (Wear OS)
├── shared/            # Shared Kotlin module: data models, projection math, GeoJSON parsing
├── backend/           # Nightly OSM snapshot service
├── PRD.md             # Product requirements
└── CLAUDE.md          # This file
```

---

## Conventions

### Code style
- Standard Kotlin conventions, ktlint defaults
- One class per file unless tightly coupled
- Prefer data classes for models; sealed classes for state
- Coroutines + Flow for async, no RxJava
- No Java code

### Naming
- Functions: `camelCase`
- Composables: `PascalCase` (per Compose convention)
- Constants: `UPPER_SNAKE_CASE`
- Test files: `[ClassName]Test.kt`

### Compose patterns
- State hoisted out of composables; composables take state + callbacks
- Use `remember` and `rememberSaveable` correctly — saveable for anything that should survive process death
- Previews for every screen-level composable
- Watch composables go in `wear/`, phone composables in `mobile/`. Don't share UI code between them; the form factors are too different.

### Watch-specific
- Battery is sacred. No background work that doesn't have to be on the watch.
- Screen-on time during a round is the dominant cost — favor "tap to wake" interactions over always-on
- Use `AmbientModeSupport` for round-active screen
- All distance text in meters by default (Sweden); units setting lives on phone

### Git
- Branch per feature, descriptive names (`feat/watch-distance-display`, `fix/overpass-timeout`)
- Commit messages: imperative mood, present tense ("Add green polygon parsing", not "Added")
- No commits to `main` directly
- Rebase before merging, no merge commits

---

## Things that should make Claude pause

- "Let's add Google Maps" — no, we render polygons ourselves
- "Let's add a quick login" — no accounts in v1
- "Let's cache courses on the watch for offline" — one active course at a time on the watch in v1
- "Let's pull from GolfBert/GolfAPI" — paid course data is explicitly out of scope; if OSM doesn't have it, we don't have it
- "Let's add scoring" — out of scope for v1, see PRD
- Anything that makes the watch dependent on the phone during a round — see "Critical architectural rules"

If a user request implies any of the above, stop and confirm before proceeding.

---

## Build order

Working through the steps in `PRD.md` section 10. Each step is roughly one session. Don't combine steps. Don't jump ahead.

Current step: TBD — update this line as we progress.

---

## Testing approach

- Unit tests for projection math, GeoJSON parsing, distance calculations — these are pure functions, easy wins
- Instrumented tests only where we genuinely need them (Data Layer sync, GPS handling)
- Real-watch testing is the actual quality bar. Emulator testing for GPS scenarios is unreliable.
- Galaxy Watch 7 is the reference device. If it works there, ship.

---

## Useful references

- OSM golf tagging: https://wiki.openstreetmap.org/wiki/Tag:leisure%3Dgolf_course
- Overpass API: https://overpass-api.de/
- SMHI open data: https://opendata.smhi.se/apidocs/
- Wearable Data Layer: https://developer.android.com/training/wearables/data/data-layer
- Wear OS Compose: https://developer.android.com/jetpack/compose/wear

---

## Notes for Claude

- Be honest about uncertainty. If OSM data for a course is missing, say so — don't fabricate.
- Resist scope creep aggressively. The PRD is the source of truth.
- Prefer small, reviewable changes. After completing a step, stop and let Boris review before moving on.
- When in doubt about a Wear OS API or behavior, search the docs rather than guessing — the platform has changed a lot in recent versions.
