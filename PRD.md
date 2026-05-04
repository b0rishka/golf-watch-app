# Golf Watch App — Product Requirements Document (v1)

**Status:** Draft
**Target platform:** Wear OS 5 (Galaxy Watch 7) + Android companion phone app
**Region:** Sweden (launch market)
**Document owner:** Boris

---

## 1. Vision

A free, no-bullshit golf GPS app for Wear OS that gives Swedish golfers the three things they actually look at during a round — distance to the green, an illustrated hole map, and wind — without subscriptions, account walls, or course unlock fees.

Built on open data (OpenStreetMap + SMHI), so coverage grows with the community rather than with our wallet.

---

## 2. Why this exists

Existing options for Swedish golfers fall into two camps:

- **Premium hardware (Garmin, Bushnell):** great experience, $300–500 entry price, locked to their devices.
- **Subscription apps (Hole19, 18Birdies, Golfshot):** good coverage but paywall the features people most want (point-to-play, illustrated maps, multiple courses).

There's a gap for: free, watch-first, illustrated map, Swedish-course coverage. OSM has surprisingly good coverage of Swedish courses, and SMHI provides best-in-class Nordic weather data for free. The data is there; nobody has bothered to wrap it in a clean Wear OS app.

---

## 3. Target user

A Swedish recreational golfer with handicap roughly 10–36, who:

- Owns a Wear OS watch (Galaxy Watch, Pixel Watch, TicWatch)
- Plays 10–40 rounds per season at 2–5 different courses
- Currently uses either a Garmin device, the SGF Min Golf app for booking only, or just paces it off
- Doesn't want to pay $50–100/year for a feature they use 20 times

Not the target: tournament players, statistics-obsessed golfers, simulator users, or anyone who needs strokes-gained analytics.

---

## 4. v1 scope — what ships

### 4.1 Phone companion app

- **Course discovery:** browse/search all golf courses in Sweden that exist in OSM (~600 courses, give or take)
- **Course download:** tap a course → fetch its full geometry from Overpass → cache locally → push to watch via Wearable Data Layer
- **Round start:** "Start round at [course]" → activates watch app, syncs hole data
- **Settings:** units (meters default for Sweden), watch sync status, cached course management

### 4.2 Watch app

- **Auto-detect current hole** based on GPS proximity to tee boxes
- **Distance display:** front / center / back of green in meters, large readable digits
- **Illustrated hole map:** vector-rendered fairway, green, bunkers, water, tees, in golf-style colors (not satellite). Hole-up orientation (tee at bottom, green at top).
- **Wind indicator:** arrow showing wind direction relative to hole bearing, plus speed in m/s. Pulled from SMHI on round start, refreshed every 15 min.
- **Manual hole switching:** swipe left/right if auto-detect picks the wrong hole

### 4.3 Data layer

- **Course geometry:** OSM via Overpass API, fetched on-demand per course
- **Course list:** pre-built nightly snapshot of all `leisure=golf_course` ways in Sweden, hosted on our backend
- **Weather:** SMHI open data API, no key required
- **No accounts. No backend user data. No analytics beyond crash reporting.**

---

## 5. Out of scope for v1

These are real features, just not now. Listed so we don't get tempted:

- Scorecard / score tracking
- Stat tracking, strokes gained, shot tracking
- Green slope / heatmap (requires paid data — defer to v2 minimum)
- Point-to-play tap-to-measure on the watch (defer to v2 — phone-first if at all)
- Multiple courses cached simultaneously on watch (one active round at a time)
- Other countries (Sweden first; expansion is a v2 conversation)
- Course reviews, social features, tee-time booking
- Garmin / Apple Watch support
- Offline maps for areas without cell signal (best-effort caching only)
- Handicap calculation, GIT integration

---

## 6. Success criteria for v1

We're not optimizing for revenue or scale. v1 is successful if:

1. **It works on a real round.** Boris plays 18 holes at a Swedish course, the watch shows correct distance and a recognizable hole map on every hole, without crashing or draining the battery before hole 14.
2. **It works on 3 different courses** with different OSM mapping quality (one well-mapped, one minimally mapped, one in between).
3. **Battery cost is < 25%** of Galaxy Watch 7 capacity for a 4-hour round with screen-on-during-shot usage.
4. **GPS accuracy is good enough** that distance to green is within ±3 meters of a laser rangefinder reading.
5. **It looks like a real app** — illustrated map is visually clean enough that a stranger wouldn't immediately peg it as a hobby project.

---

## 7. Architecture

### 7.1 Repo structure

```
golf-watch-app/
├── mobile/            # Phone companion app
├── wear/              # Watch app
├── shared/            # Shared data models, projection math
├── backend/           # Tiny service for nightly OSM course-list snapshots
├── PRD.md             # This file
└── CLAUDE.md          # Context for Claude Code sessions
```

### 7.2 Tech stack

| Layer | Choice | Why |
|---|---|---|
| Phone app | Kotlin + Jetpack Compose | Standard, current, what Android Studio scaffolds |
| Watch app | Kotlin + Compose for Wear OS | Same |
| Shared models | Kotlin Multiplatform module (or plain Kotlin) | Avoid serializing twice |
| Phone ↔ watch sync | Wearable Data Layer API | The Google-blessed way |
| Maps rendering | Compose Canvas, custom drawing | We're rendering polygons, not tiles |
| Course data | OSM Overpass API | Free, comprehensive enough for Sweden |
| Weather | SMHI Öppna data | Free, accurate, no key |
| Backend | Single small service (Cloudflare Worker or similar) hosting nightly OSM snapshots | Avoid hammering Overpass from clients |
| Local DB on phone | Room | Standard |
| Local DB on watch | DataStore (small, no SQLite needed for one active course) | Lightweight |

### 7.3 Data flow

1. **Once per night (backend):** scrape Overpass for all Swedish `leisure=golf_course` ways, store as a JSON list (id, name, centroid, bbox).
2. **Phone app on launch:** sync course list from backend (cache locally).
3. **User picks a course:** phone fetches detailed Overpass query for that course's bbox, gets all `golf=*` features as GeoJSON, caches in Room.
4. **User starts round:** phone packages course data into a single payload, sends to watch via Data Layer.
5. **Watch during round:** uses local payload, polls GPS via FusedLocationProviderClient, computes distances and renders map. No network needed.
6. **Wind:** phone fetches SMHI on round start using course centroid, sends to watch. Refreshes every 15 min if connected.

---

## 8. Risks and mitigations

| Risk | Mitigation |
|---|---|
| OSM coverage too sparse for some Swedish courses | Show clear "this course isn't fully mapped, distance to green center only" state. Don't pretend data exists. Possibly add a "help map this course" link to OSM editor in v1.5. |
| Battery drain from continuous GPS | Use `FusedLocationProviderClient` with PRIORITY_HIGH_ACCURACY only when screen on; PRIORITY_BALANCED otherwise. Test on real watch, not emulator. |
| Wearable Data Layer payload size limits (100KB) | Most single-course payloads are <50KB. If a course's geometry exceeds, simplify polygons (Douglas-Peucker) before sending. |
| Auto-hole-detection picks wrong hole on parallel fairways | Fallback: manual swipe. Use direction-of-travel + last-hole context, not just nearest tee. |
| OSM tagging inconsistencies (some courses use relations, some ways, some both) | Normalize on the backend during nightly snapshot. Document edge cases. |
| Overpass rate-limiting if many users hit it | Backend-only Overpass access; clients hit our service. |

---

## 9. Open questions

- Does Boris want to publish this on the Play Store eventually, or stay personal/sideloaded? Affects whether we care about the Play Console process now.
- Is "free forever" the actual long-term goal, or "free at launch, maybe premium tier later"? Not a v1 decision but shapes architecture (e.g., do we ever need accounts?).
- Should we add a fallback paid course-data provider for courses where OSM is empty? Adds complexity but solves the coverage gap. v1: no. v2: maybe.

---

## 10. Build order (rough sequence for vibe-coding sessions)

1. Project scaffold in Android Studio (Wear OS + companion phone app template).
2. Backend: nightly Overpass snapshot of Swedish courses → static JSON file.
3. Phone: course list screen, pulls from backend.
4. Phone: course detail screen, fetches per-course Overpass geometry, caches.
5. Shared: GeoJSON parsing + lat/lon to screen projection.
6. Phone: render hole map preview (sanity check the rendering before tackling watch).
7. Watch: Data Layer receiver, accepts course payload.
8. Watch: GPS hookup, distance to green center.
9. Watch: hole map rendering on Compose Canvas.
10. Watch: front/center/back of green calculation.
11. Phone: SMHI integration, wind on watch.
12. Watch: auto-hole detection.
13. Real-round testing on a Swedish course. Iterate.

Each of these is roughly one focused session with Claude Code. Don't combine them.
