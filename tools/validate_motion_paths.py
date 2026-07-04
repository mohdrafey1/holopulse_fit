#!/usr/bin/env python3
"""Validate HoloPulse Fit motion path JSON files.

Helper tool only. It is not part of the Android app (TRD section 10). It checks both the simplified
sample format (sample-motion-paths.json) and the canonical exported format described in
schema.md section 3, reporting structural or range problems so reviewers can trust the data.

Usage:
    python validate_motion_paths.py path/to/file.json [more.json ...]
    python validate_motion_paths.py            # validates the bundled sample files
"""

import argparse
import json
import os
import sys

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DEFAULT_FILES = [
    os.path.join(REPO_ROOT, "06_sample_data", "sample-motion-paths.json"),
]

CANONICAL_JOINTS = {
    "left_shoulder", "right_shoulder", "left_elbow", "right_elbow",
    "left_wrist", "right_wrist", "left_hip", "right_hip",
    "left_knee", "right_knee", "left_ankle", "right_ankle",
}


def _in_unit_range(value):
    return isinstance(value, (int, float)) and 0.0 <= value <= 1.0


def validate_simplified(data, errors):
    for key in ("motion_path_id", "exercise_name", "landmark_series"):
        if key not in data:
            errors.append(f"missing key: {key}")
    series = data.get("landmark_series", [])
    if not isinstance(series, list) or not series:
        errors.append("landmark_series must be a non empty list")
        return
    last_ts = -1
    for i, frame in enumerate(series):
        ts = frame.get("timestamp_ms")
        if not isinstance(ts, (int, float)):
            errors.append(f"frame {i}: timestamp_ms missing or not numeric")
        elif ts < last_ts:
            errors.append(f"frame {i}: timestamp_ms not monotonic")
        else:
            last_ts = ts
        for axis in ("hip_y", "knee_y", "shoulder_y"):
            if axis in frame and not _in_unit_range(frame[axis]):
                errors.append(f"frame {i}: {axis} out of 0..1 range")


def validate_canonical(series, errors):
    if not isinstance(series, list) or not series:
        errors.append("landmarkSeries must be a non empty list")
        return
    last_ts = -1
    for i, frame in enumerate(series):
        ts = frame.get("timestamp_ms")
        if not isinstance(ts, (int, float)):
            errors.append(f"frame {i}: timestamp_ms missing or not numeric")
        elif ts < last_ts:
            errors.append(f"frame {i}: timestamp_ms not monotonic")
        else:
            last_ts = ts
        joints = frame.get("joints", {})
        if not isinstance(joints, dict) or not joints:
            errors.append(f"frame {i}: joints missing or empty")
            continue
        unknown = set(joints) - CANONICAL_JOINTS
        if unknown:
            errors.append(f"frame {i}: unknown joints {sorted(unknown)}")
        for name, point in joints.items():
            if not (isinstance(point, dict) and _in_unit_range(point.get("x")) and _in_unit_range(point.get("y"))):
                errors.append(f"frame {i}: joint {name} has invalid x or y")


def validate_file(path):
    errors = []
    try:
        with open(path, "r", encoding="utf-8") as handle:
            data = json.load(handle)
    except FileNotFoundError:
        return [f"file not found: {path}"]
    except json.JSONDecodeError as exc:
        return [f"invalid JSON: {exc}"]

    if isinstance(data, dict) and "landmark_series" in data:
        validate_simplified(data, errors)
    elif isinstance(data, list):
        validate_canonical(data, errors)
    elif isinstance(data, dict) and "landmarkSeries" in data:
        parsed = data["landmarkSeries"]
        if isinstance(parsed, str):
            parsed = json.loads(parsed)
        validate_canonical(parsed, errors)
    else:
        errors.append("unrecognized motion path format")
    return errors


def main():
    parser = argparse.ArgumentParser(description="Validate HoloPulse Fit motion path JSON.")
    parser.add_argument("files", nargs="*", help="motion path JSON files")
    args = parser.parse_args()

    files = args.files or DEFAULT_FILES
    exit_code = 0
    for path in files:
        errors = validate_file(path)
        if errors:
            exit_code = 1
            print(f"FAIL {path}")
            for error in errors:
                print(f"  - {error}")
        else:
            print(f"OK   {path}")
    sys.exit(exit_code)


if __name__ == "__main__":
    main()
