#!/usr/bin/env python3
"""Generate extra sample workout history entries for HoloPulse Fit review.

Helper tool only. It is not part of the Android app (TRD section 10). It writes additional rows in
the same shape as sample-workout-history.json (schema.md section 6) so reviewers can populate a
larger history when seeding.

Usage:
    python generate_test_data.py --count 10 --out extra-workout-history.json
"""

import argparse
import json
import random
from datetime import date, timedelta

EXERCISES = ["squats", "jumping_jacks", "pushup_approximation"]
MET_FACTOR = {"squats": 5.0, "jumping_jacks": 8.0, "pushup_approximation": 7.0}
TIME_FACTOR = 2.0
REP_FACTOR = 0.3


def estimate_calories(exercise, duration_seconds, reps):
    minutes = duration_seconds / 60.0
    kcal = MET_FACTOR[exercise] * minutes * TIME_FACTOR + reps * REP_FACTOR
    return max(0, round(kcal))


def generate(count, seed):
    rng = random.Random(seed)
    today = date.today()
    rows = []
    for i in range(count):
        exercise = rng.choice(EXERCISES)
        reps = rng.randint(8, 40)
        duration = rng.randint(60, 300)
        completed = rng.random() > 0.2
        day = today - timedelta(days=rng.randint(0, 30))
        rows.append({
            "session_id": f"generated-session-{i + 1:03d}",
            "date": day.isoformat(),
            "exercise_type": exercise,
            "duration_seconds": duration,
            "reps": reps,
            "calories_estimate": estimate_calories(exercise, duration, reps),
            "completed_status": "completed" if completed else "stopped_early",
        })
    rows.sort(key=lambda row: row["date"], reverse=True)
    return rows


def main():
    parser = argparse.ArgumentParser(description="Generate sample workout history rows.")
    parser.add_argument("--count", type=int, default=10, help="number of sessions to generate")
    parser.add_argument("--seed", type=int, default=42, help="random seed for reproducibility")
    parser.add_argument("--out", default="extra-workout-history.json", help="output JSON file")
    args = parser.parse_args()

    rows = generate(args.count, args.seed)
    with open(args.out, "w", encoding="utf-8") as handle:
        json.dump(rows, handle, indent=2)
    print(f"Wrote {len(rows)} sessions to {args.out}")


if __name__ == "__main__":
    main()
