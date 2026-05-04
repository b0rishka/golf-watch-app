#!/usr/bin/env python3
"""
Nightly snapshot of all Swedish golf courses from OpenStreetMap via Overpass API.
Writes backend/data/courses.json, which is committed to the repo by the
GitHub Actions workflow and served via raw.githubusercontent.com.
"""

import json
import sys
import urllib.request
import urllib.parse
from datetime import datetime, timezone
from pathlib import Path

OVERPASS_URL = "https://overpass-api.de/api/interpreter"
OUTPUT_PATH = Path(__file__).parent / "data" / "courses.json"

# Fetch ways and relations tagged leisure=golf_course within Sweden.
# `out center bb tags` gives us: synthesised centroid, bounding box, and tags.
OVERPASS_QUERY = """
[out:json][timeout:120][bbox:55.0,10.5,69.5,24.5];
(
  way["leisure"="golf_course"];
  relation["leisure"="golf_course"];
);
out center bb tags;
""".strip()


def fetch_overpass(query: str) -> dict:
    payload = urllib.parse.urlencode({"data": query}).encode()
    req = urllib.request.Request(
        OVERPASS_URL,
        data=payload,
        headers={
            "Content-Type": "application/x-www-form-urlencoded",
            "User-Agent": "golf-watch-app/1.0 (https://github.com/b0rishka/golf-watch-app)",
        },
    )
    with urllib.request.urlopen(req, timeout=150) as resp:
        if resp.status != 200:
            raise RuntimeError(f"Overpass returned HTTP {resp.status}")
        return json.loads(resp.read())


def approximate_bbox(lat: float, lon: float) -> dict:
    """Fallback ~500 m bbox when Overpass omits bounds."""
    delta = 0.005
    return {"minLat": lat - delta, "minLon": lon - delta,
            "maxLat": lat + delta, "maxLon": lon + delta}


def element_to_course(el: dict) -> dict | None:
    center = el.get("center")
    if not center:
        return None

    tags = el.get("tags", {})
    name = tags.get("name") or tags.get("name:sv")
    if not name:
        return None

    bounds = el.get("bounds")
    bbox = (
        {
            "minLat": bounds["minlat"],
            "minLon": bounds["minlon"],
            "maxLat": bounds["maxlat"],
            "maxLon": bounds["maxlon"],
        }
        if bounds
        else approximate_bbox(center["lat"], center["lon"])
    )

    return {
        "id": el["id"],
        "osmType": el["type"],
        "name": name,
        "lat": center["lat"],
        "lon": center["lon"],
        "bbox": bbox,
    }


def main() -> None:
    print("Querying Overpass...", flush=True)
    try:
        raw = fetch_overpass(OVERPASS_QUERY)
    except Exception as e:
        print(f"ERROR: Overpass query failed: {e}", file=sys.stderr)
        sys.exit(1)

    elements = raw.get("elements", [])
    print(f"Overpass returned {len(elements)} elements", flush=True)
    if elements:
        print(f"First element: {json.dumps(elements[0])}", flush=True)

    courses = [
        course
        for el in elements
        if (course := element_to_course(el)) is not None
    ]

    print(f"After filtering: {len(courses)} named courses with center", flush=True)
    if not courses:
        print("ERROR: 0 courses after filtering — not overwriting existing file", file=sys.stderr)
        sys.exit(1)

    courses.sort(key=lambda c: c["name"].casefold())

    output = {
        "generated": datetime.now(timezone.utc).isoformat(),
        "count": len(courses),
        "courses": courses,
    }

    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(json.dumps(output, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Done — {len(courses)} courses written to {OUTPUT_PATH}", flush=True)


if __name__ == "__main__":
    main()
