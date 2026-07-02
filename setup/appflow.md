# HoloPulse Fit: App Flow Document

 
Navigation model: Single activity, Navigation Compose, 8 screen areas.

## 1. Screen Map

```
Launch
  -> Dashboard
       -> Workout Library -> Camera Workout -> Session Summary -> Dashboard
       -> History -> History Detail
       -> Ghost Trainer Replay
       -> Settings
```

| Route | Screen | Entry Points |
| --- | --- | --- |
| launch | Launch screen | App start |
| dashboard | Dashboard | After launch, after summary save, bottom or top navigation |
| library | Workout Library | Dashboard quick start, navigation |
| workout/{exerciseType} | Camera Workout Session | Library start action, dashboard quick start |
| summary/{sessionId} | Session Summary | Automatic on session end |
| history | Workout History list | Dashboard recent card, navigation |
| history/{sessionId} | History Detail | History list item |
| ghost/{exerciseType} | Ghost Trainer Replay | History detail, workout screen toggle, dashboard entry |
| settings | Settings | Navigation, permission guidance links |

## 2. Launch Flow

1. Launch screen shows HoloPulse Fit identity with logo and glow animation for a short branded moment.
2. App loads UserStats and Settings from Room while the launch screen is visible.
3. Navigate to Dashboard. Launch screen is not reachable again through back navigation.

## 3. Dashboard Flow

1. Shows greeting, today progress, streak counter, completed session count, and quick start button.
2. Recent workout cards show duration, reps, calories, and date. Tapping a card opens History Detail.
3. Quick start opens the Workout Library, or directly opens the last used workout when one exists.
4. Navigation gives access to Library, History, Ghost Trainer, and Settings.

## 4. Workout Library Flow

1. Animated cards list squats, jumping jacks, and push-up approximation.
2. Each card shows short instructions, target rep options, estimated effort, and a start action.
3. Users move between cards by touch swipe. When the camera is already active in a session context, side swipe gestures also move between cards.
4. Start action checks camera permission state before opening the Camera Workout screen.

## 5. Camera Permission Flow

1. First tracked workout triggers a permission explainer with clear privacy wording: camera frames are analyzed on device, no video is stored.
2. Grant: continue to the Camera Workout screen.
3. Deny: show guidance screen with retry action and a link to system settings for permanent denial. Tracking cannot start until permission is granted.
4. Settings screen repeats the permission state and guidance at any time.

## 6. Camera Workout Session Flow

```
Countdown -> Tracking -> (Paused <-> Tracking) -> Complete -> Summary
              |
              -> Guidance (tracking loss) -> Tracking
```

1. Screen opens with live camera preview, pose skeleton overlay, rep counter, timer, exercise name, current state, next action hint, and touch pause button.
2. A countdown ring gives the user time to step into frame.
3. Tracking state: reps count on complete movement cycles, Aura Energy trails follow tracked joints, gesture hints stay visible.
4. Gesture actions inside the session:
   Hand raise (stable hold): next exercise or confirm highlighted action.
   Side swipe: move between session panels.
   Both hands hold: pause or resume, once per hold cycle.
5. Guidance state: when the body is not fully visible or confidence drops, counting pauses and a clear message with positioning tips appears. The session does not end unexpectedly.
6. Session end: target reached, user confirms finish by gesture, or touch stop. Motion path recording stops and the app navigates to Session Summary.

## 7. Session Summary Flow

1. Shows completed reps, duration, calories estimate, streak update, and save status.
2. Session, exercise set, motion path, and updated user stats save to Room.
3. Actions: back to Dashboard, repeat workout, view Ghost Trainer replay of the just saved path.

## 8. History Flow

1. History list shows completed sessions with date, exercise type, duration, reps, and calories.
2. Progress summary strip shows totals and streak trend.
3. History Detail shows full session data, linked motion path availability, delete action, and a replay action when a motion path exists.
4. Delete removes the session and its linked ExerciseSet and MotionPath rows after confirmation.

## 9. Ghost Trainer Replay Flow

1. Entry with a saved motion path: replays the transparent skeleton guide synchronized to timestamps.
2. Entry without saved paths: loads ghost-trainer-sample.json so replay is always demonstrable.
3. Overlay mode during a matching workout: ghost guide renders behind the live skeleton, with timing and path similarity cues.
4. A persistent label states the replay is guidance, not medical or professional fitness correction.

## 10. Settings Flow

1. Camera permission status and guidance.
2. Aura intensity control and reduced effects toggle.
3. Ghost Trainer enable toggle.
4. History management: clear all local workout records with confirmation.
5. Privacy notes restating on device processing and no raw video storage.

## 11. Global State and Error Flows

| Condition | Behavior |
| --- | --- |
| App backgrounded during session | Camera processing pauses, session state preserved, resume on return |
| Permission revoked mid use | Session ends safely, guidance screen shown |
| Low light | Guidance banner with lighting tip, counting confidence gate active |
| Partial body visibility | Guidance state, counting suspended, no accidental screen switches |
| Empty history | Friendly empty state with quick start shortcut |
| No saved motion paths | Ghost Trainer falls back to bundled sample path |
