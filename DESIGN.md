# DESIGN.md

Visual design specification for the golf watch app. This is the source of truth for all UI work — colors, typography, spacing, component patterns, and per-screen layouts. When implementing in Compose, pull tokens from here, not from Material defaults.

Read alongside `PRD.md` (scope) and `CLAUDE.md` (architecture).

---

## 1. Design philosophy

The app should feel like a confident, premium tool — closer to a Garmin watch face or Apple Workout than a typical Android app. Three principles:

**Muted over saturated.** The course is the bright thing — the app is the calm thing. Sage greens, slate blues, neutral sands. Bright accents are reserved for actionable elements (the white Start button, your position dot).

**Hierarchy through scale, not color.** The most important number on a screen is the biggest. Secondary information is smaller and grayer, not differently colored. Color is structural, not decorative.

**Consistent palette across phone and watch.** The same `#5a7a55` is "green" on both surfaces. The same `#a89a82` is "bunker." Phone and watch are visually one product.

---

## 2. Color tokens

All colors are defined as raw hex; in Compose these become `Color(0xFF...)` constants in a `GolfTheme.kt` file.

### Surfaces

| Token | Hex | Usage |
|---|---|---|
| `surfaceBlack` | `#000000` | Watch screen background only (true black for OLED) |
| `surfaceBase` | `#0f1410` | Phone screen background, watch map "rough" |
| `surfaceRaised` | `#1a201c` | Phone cards, search bar, list items |
| `surfaceHighlight` | `rgba(255,255,255,0.06)` | Watch list current-item highlight |

### Course geometry (used on watch maps and phone preview)

| Token | Hex | Maps to |
|---|---|---|
| `fairway` | `#3d5440` | OSM `golf=fairway` |
| `green` | `#5a7a55` | OSM `golf=green` |
| `bunker` | `#a89a82` | OSM `golf=bunker` |
| `water` | `#3a5566` | OSM `golf=water_hazard`, `golf=lateral_water_hazard` |
| `tee` | `#888888` | OSM `golf=tee` (small marker) |

### Text

| Token | Hex | Usage |
|---|---|---|
| `textPrimary` | `#ffffff` | Primary numbers, headlines, button labels on dark |
| `textSecondary` | `#d4d4d4` | Secondary numbers (front/back distance), inactive list items |
| `textMuted` | `#8a8a8a` | Body labels, metadata, status text |
| `textCaption` | `#6a6a6a` | Small caps section labels, timestamps |
| `textDisabled` | `#5a5a5a` | Bottom-of-screen interaction hints |

### Accents

| Token | Hex | Usage |
|---|---|---|
| `accentWind` | `#7a9bb3` | Wind indicator (arrow + speed text) |
| `accentPlayer` | `#ffffff` | Your GPS position dot |
| `actionPrimary` | `#ffffff` | Primary CTA button background ("Start round") |
| `actionPrimaryText` | `#0f1410` | Text on primary CTA |

### Status indicators

| Token | Hex | Meaning |
|---|---|---|
| `statusDetailed` | `#5a7a55` | Course has full OSM geometry (uses `green` token) |
| `statusPartial` | `#a89a82` | Course has only outline / minimal geometry (uses `bunker` token) |
| `statusMissing` | `#5a5a5a` | Course not mapped (uses `textDisabled` token) |

Reusing geometry tokens for status is intentional — the green dot in a list literally represents "we have green polygons for this course."

---

## 3. Typography

### Family

System sans-serif everywhere. On Wear OS this means Roboto; on Android phone this means Roboto. We don't ship custom fonts — they cost battery on the watch and add bundle size for no real gain.

### Watch type scale

| Style | Size | Weight | Usage |
|---|---|---|---|
| `displayHero` | 56px | 300 | Center distance on primary hole screen |
| `displayLarge` | 36px | 300 | Distance on map screen (offset left) |
| `displayMedium` | 26px | 400 | Front/back distances |
| `bodyLarge` | 24px | 400 | Current hole number in picker |
| `bodyMedium` | 20px | 400 | Adjacent hole numbers in picker |
| `bodySmall` | 18px | 400 | Far holes in picker (faded) |
| `labelMedium` | 13px | 500 | Wind speed text |
| `labelSmall` | 11px | 500 | F/C/B letters, "PAR 4" labels |
| `caption` | 10px | 500 | "HOLE 7 · PAR 4" header, "METERS", "TO CENTER" |
| `captionTiny` | 9px | 500 | Map screen "CENTER" label, footer hints |

### Phone type scale

| Style | Size | Weight | Usage |
|---|---|---|---|
| `headlineLarge` | 26px | 400 | Screen title ("Courses") |
| `headlineMedium` | 22px | 400 | Course name on detail screen |
| `displayStat` | 22px | 400 | Stat numbers (holes, par, length) |
| `bodyLarge` | 15px | 500 | Course names in list, button labels |
| `bodyMedium` | 13px | 400 | Search placeholder, course subtitle |
| `bodySmall` | 12px | 500 | Status row labels |
| `labelSmall` | 11px | 400/500 | Card metadata, hint text |
| `caption` | 10–11px | 500 | Section headers (NEARBY, RECENTLY PLAYED) |

### Letter-spacing rules

- Small caps labels (any text in `caption` or `labelSmall` that's UPPERCASE): `letter-spacing: 1.5px`
- Section headers on phone: `letter-spacing: 1.5px`
- All other text: default tracking

### Weight discipline

We use **only 300, 400, 500**. No 600 or 700 anywhere — they look heavy and aggressive against the muted palette.

- 300 (light): only for the largest distance numbers — gives them an elegant, precise feel
- 400 (regular): default body text, secondary numbers
- 500 (medium): labels, course names, button text — anything that needs slight emphasis

### Tabular numerals

Every number that updates frequently or sits next to other numbers must use tabular numerals so digits don't shift width. In Compose: `fontFeatureSettings = "tnum"`. Applies to: all distances, hole numbers, hole lengths, wind speed, time, par, course statistics.

---

## 4. Spacing scale

Consistent across phone and watch:

| Token | Value | Usage |
|---|---|---|
| `space-xs` | 2px | Adjacent label/value pairs |
| `space-sm` | 6px | Inside small components (label above number) |
| `space-md` | 8px | Between cards in a tight group |
| `space-lg` | 12px | Card internal gap, button internal padding |
| `space-xl` | 16px | Card padding, section internal spacing |
| `space-2xl` | 20–24px | Screen edge padding, section-to-section |
| `space-3xl` | 28–32px | Major section breaks |

### Border radius

| Token | Value | Usage |
|---|---|---|
| `radius-sm` | 8px | Small pills, inline tags |
| `radius-md` | 12px | List item pills, status rows, search bar |
| `radius-lg` | 14px | Cards on phone |
| `radius-xl` | 16px | Primary CTA button |

Watch components mostly don't use border radius — the screen is round, content sits naturally inside it.

---

## 5. Watch screens

The Galaxy Watch 7 reference screen is **432×432px**. All measurements below assume this canvas. Wear OS Compose handles scaling for other watch sizes automatically as long as we use `dp` units.

### 5.1 Primary hole screen (distance view)

Layout, top to bottom:

- Top center: `caption` text "HOLE 7 · PAR 4" in `textCaption`
- Center: F / C / B distances stacked
  - Front line: small "F" label in `green` (left, fixed-width 28dp), then `displayMedium` 26px white-ish (`textSecondary`)
  - Center line: small "C" label in lighter green (`#7fb86a`, slight tint up), then `displayHero` 56px pure white
  - Back line: small "B" label in `green`, then `displayMedium` 26px in `textSecondary`
- Below stack: `caption` "METERS" in `textCaption`, centered
- Bottom center: wind indicator
  - Wind arrow SVG (rotated to wind direction relative to hole bearing), 11dp, in `accentWind`
  - 5dp gap
  - Wind speed in `labelMedium` 13px, `accentWind`, suffix "m/s"
- Top edge of screen: hole-progress dot strip (1 dot per hole, 18 total). Played holes are filled `green`, current hole is a small green pill, future holes are `#2a2a2a`. Dots are 3dp; current pill is 5×3dp.

Background: `surfaceBlack`.

### 5.2 Map view

Layout:

- Top center: `caption` "HOLE 7 · PAR 4" only (no distance here — distance is on the left)
- Left side, vertically centered: distance group offset 26dp from screen edge
  - Tiny `captionTiny` 9px "CENTER" label in `textCaption`, 2dp gap
  - `displayLarge` 36px white "156"
  - 2dp gap, `caption` 10px "METERS" in `textMuted`
- Center of remaining space: the illustrated hole map
  - Hole-up orientation (tee at bottom, green at top)
  - Polygon fills using course geometry tokens
  - Player position: 5dp white circle, with 9dp outer ring at 30% opacity
  - Pin: 2dp white dot with 1dp white line above (no flag, no red)
  - Distance line from player to pin: 0.5dp white dashed line at 25% opacity
- Bottom center: wind indicator (same as primary screen, no backdrop pill)

Background: `surfaceBlack` (the rough fills behind the map use `surfaceBase` `#0f1410`).

### 5.3 Hole picker

Reached by long-press from any in-round screen, or via menu.

Layout:

- Top center: `caption` "SELECT HOLE"
- Center: vertical list of holes with the current hole highlighted
  - 5 visible items: current ± 2
  - Items at distance ±2: 40% opacity, smaller text
  - Items at distance ±1: 70% opacity, medium text
  - Current item: 100% opacity, larger text, soft `surfaceHighlight` pill background, 12px padding, `radius-md` (12px) corners
  - Each row: hole number (left, fixed-width 22dp), `labelSmall` "PAR X" 14dp gap, distance to hole (right) in `labelSmall` `textMuted`
- Bottom center: `captionTiny` 9px "SCROLL · TAP TO PLAY" hint in `textDisabled`

Scrolls smoothly with a rotating-bezel-friendly velocity curve. List wraps visually at top/bottom of screen via opacity fade, not hard clipping.

### 5.4 Interaction model summary

- **Distance ↔ Map view:** physical button (single press) toggles
- **Hole change:** swipe left (next hole) / swipe right (previous hole)
- **Hole picker:** long-press anywhere on screen, or menu item
- **Menu:** swipe down from top edge

### 5.5 Always-on / ambient mode

Tap-to-wake. Ambient mode is a black screen with no content (battery saver). When the user raises the wrist or taps, fade in to the last-active screen. No always-on display in v1.

---

## 6. Phone screens

### 6.1 Course list (home)

Layout, top to bottom:

- System status bar (24dp height)
- Header block, 16dp top / 24dp horizontal padding:
  - `caption` "SWEDEN" in `textCaption`
  - 4dp gap
  - `headlineLarge` "Courses" in `textPrimary`
- Search bar, 16dp top / 24dp horizontal margin, 11dp internal padding:
  - `surfaceRaised` background, `radius-md` (12dp)
  - Search icon (14dp, `textCaption` color), 10dp gap, `bodyMedium` placeholder "Search courses" in `textCaption`
- Section header "NEARBY" in `caption` `textCaption`, 24dp horizontal, 10dp bottom margin
- Course cards, 16dp horizontal screen padding, 6dp gap between cards:
  - Each card: `surfaceRaised` background, `radius-lg` (14dp), 14dp vertical / 16dp horizontal padding
  - Layout: course info on left (flex-grow), chevron icon on right (14dp, 40% opacity)
  - Course info:
    - Course name in `bodyLarge` 15dp `textPrimary`, 2dp bottom margin
    - Metadata row in `labelSmall` 11dp `textMuted`: location, distance, status indicator
    - Metadata separator: ` · ` in `textDisabled`
    - Status indicator: 5dp colored dot + space + status word ("Detailed", "Partial")
- Section header "RECENTLY PLAYED", 24dp top padding before this section
- Recently played card uses same template, but metadata shows "Played X days ago" instead of distance/status

Background: `surfaceBase`.

### 6.2 Course detail

Layout, top to bottom:

- Status bar
- Back button: 20dp chevron-left icon, 8dp vertical / 20dp horizontal padding
- Course header, 16dp top / 24dp horizontal padding:
  - `caption` location · distance, e.g. "STALLARHOLMEN · 12 KM"
  - 4dp gap
  - `headlineMedium` 22dp course name
  - 4dp gap, optional course variant subtitle in `bodyMedium` 13dp `textMuted` (e.g. "Stadium Course")
- Course preview map, 20dp top / 24dp horizontal margin, 140dp height:
  - `surfaceRaised` background, `radius-lg` (14dp)
  - Abstract overview: scattered fairway curves in `fairway`, small green dots in `green`, optional water hint in `water`
  - Not geographically precise — purely a visual confirmation
- Stats grid, 24dp horizontal padding, 3 columns, 8dp gap:
  - Each stat card: `surfaceRaised`, `radius-md` (12dp), 12dp padding
  - Top: `captionTiny` 9dp label in `textCaption` ("HOLES", "PAR", "LENGTH")
  - 4dp gap
  - Bottom: `displayStat` 22dp number in `textPrimary`, tabular numerals
- Map quality status row, 20dp top / 24dp horizontal margin, 12dp padding:
  - `surfaceRaised` background, `radius-md` (12dp)
  - 7dp colored dot (status color), 10dp gap
  - `bodySmall` 12dp white title ("Detailed mapping", "Partial mapping", "Outline only")
  - `labelSmall` 11dp `textMuted` description
- **Start round button**, fixed to bottom of screen, 28dp from bottom, 24dp horizontal:
  - `actionPrimary` (white) background, `radius-xl` (16dp), 16dp padding
  - Centered: small play icon (14dp circle outline + dot), 8dp gap, `bodyLarge` 15dp "Start round" in `actionPrimaryText`
  - Below button, 8dp gap, centered `labelSmall` 11dp helper "Sends course to Galaxy Watch 7" in `textCaption`

Background: `surfaceBase`.

### 6.3 Common phone patterns

**Section headers** are always uppercase `caption` 10–11dp `textCaption` with 1.5px letter-spacing, with 24dp horizontal padding and 10dp bottom margin before the section content begins.

**Cards** always use `surfaceRaised` background with `radius-lg` (14dp). Internal padding is 14dp vertical / 16dp horizontal for list-style cards, 12dp all sides for tight metric cards.

**Chevron affordance** on tappable list items: 14dp chevron-right at 40% opacity in `textPrimary`. We accepted this slightly non-Material pattern because card metadata is dense.

**No bottom navigation bar in v1.** Course list is the only "tab." Settings and other screens reach via a small icon in the top-right of the home screen (to be designed in v2 — out of scope for now).

---

## 7. Iconography

Custom SVG icons drawn at 14–20dp, 1.3–1.5dp stroke, no fill (outline style). All single-color, inherit `currentColor`. Examples:

- Search: circle + diagonal line
- Chevron: 3-segment polyline
- Wind arrow: vertical line with arrowhead
- Play: circle + center dot

Avoid emoji. Avoid Material icon font (too generic, doesn't match the palette weight). Hand-drawn icons in the same line weight feel custom without being expensive.

---

## 8. Motion and feedback

### Watch

- Hole change (swipe): horizontal slide, 300ms, ease-out
- View change (button press): cross-fade, 200ms
- Distance updates: no animation — numbers change instantly. Animation on a number that updates every second is nausea-inducing.
- Tap feedback: subtle 50ms scale down to 0.98, no ripple

### Phone

- Card tap: standard Material ripple at 30% opacity in `textMuted`
- Screen transitions: standard slide horizontal, 250ms
- Start round button: scale to 0.97 on press, full press triggers a brief progress state ("Sending to watch...") before navigating

---

## 9. States we have to design but haven't yet

These will need design when we hit them; placeholders here so we don't forget:

- **No GPS signal on watch:** the distance number is replaced with `--` and a small subdued "searching" indicator. Map fades to 60% until signal returns.
- **Course not yet downloaded on watch:** brief "Loading course..." state when the user starts a round before the Data Layer transfer completes.
- **Course has only partial OSM data:** map view shows what we have (green polygon as a colored circle, no fairway) with a subtle "limited mapping" indicator.
- **Wind data unavailable:** wind indicator hides entirely. We don't show a "wind unavailable" message on the watch — silence is better than noise.
- **Empty course list:** unlikely (we have 600+ Swedish courses) but should show a friendly message if filters return nothing.

---

## 10. Implementation notes for Compose

When translating this spec to Compose:

- Define all color tokens in a single `GolfColors` object, not scattered in screen files
- Define typography in a `GolfTypography` object that returns Compose `TextStyle` instances per token name
- Use `dp` for all sizing — the values above are in dp unless noted otherwise
- For the watch's circular layout, use `CurvedText` for content along the bezel (hole-progress dots eventually, possibly), and standard `Box` / `Column` with `Modifier.padding` for centered content
- The watch screens should never assume rectangular bounds — always lay out from center using `Modifier.align(Alignment.Center)` and offsets
- Avoid Material 3's default `Card` composable; build cards directly with `Box` + `Modifier.background(GolfColors.surfaceRaised, GolfShapes.lg)` because the default has a built-in elevation/shadow we don't want
- For the illustrated map: render polygons via `Canvas` and `drawPath`. Fill with `Color(...)` from the geometry tokens. Project lat/lon to canvas coordinates inside a `Composable` that accepts the hole's GeoJSON and current GPS position.

---

## 11. What this spec deliberately doesn't cover

- Light mode: this is a dark-mode-only app. Golf is played outdoors, often in bright light, on screens that benefit from high contrast and OLED black. A light mode would either compromise the design or double the work for v1.
- Localization: all UI strings are English in v1. Swedish localization is straightforward to add later (the strings are mostly numbers and very short labels).
- Accessibility audit: TalkBack / Wear screen reader support needs a pass before public release. Out of scope for v1 personal/sideload build.
- Tablet / large phone layouts: targeted at standard phone sizes (5.5"–6.7"). Tablets ignored.

---

## 12. Decision log

Choices we explicitly made and the alternatives we considered, so future-us doesn't relitigate:

| Decision | Chose | Considered | Why |
|---|---|---|---|
| Watch aesthetic | Premium / Garmin-like dark | Minimalist, sporty bright | Best fits a tool you stare at 100x per round |
| Primary hole content | F/C/B stacked | Single distance, map-first | More glanceable info without map's complexity |
| Always-on display | Tap to wake | Always on, hybrid dim | Battery cost of always-on isn't worth it on Watch 7 |
| Map distance position | Left side | Center pill, top center | Doesn't obscure terrain, reads as a complication |
| Map color saturation | Muted Apple-like | Vivid Garmin-like | Calmer, more confident, lets bright UI elements pop |
| Hole picker layout | Vertical list with fade | Grid of 18 numbers | Scrolling feels native to round screens |
| Phone bottom nav | None | Tab bar with 3+ tabs | App is too small in v1 to justify |
| Pin marker | White line only | Red flag | Removed last "obvious golf" cue for confidence |
| Font family | System Roboto | Custom font | No bundle cost, no battery cost, looks correct |
