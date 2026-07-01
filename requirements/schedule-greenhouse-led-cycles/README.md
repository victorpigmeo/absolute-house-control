# Schedule Greenhouse LED Cycles

## Goal
Allow a user to define named LED light cycles (e.g. Veg, Flora) with cron-scheduled on/off times, and activate one at a time so the greenhouse LED is switched automatically on that schedule.

## Functional Requirements
1. A light cycle consists of a name, an ON cron expression (when the LED turns on), and an OFF cron expression (when the LED turns off), each in 6-field cron syntax (seconds, minutes, hours, day-of-month, month, day-of-week).
2. The system must allow a user to create a new light cycle by submitting a name and its two cron expressions.
3. The system must validate both cron expressions on submission and reject the submission with an error, without creating the cycle, if either expression is not a valid 6-field cron expression.
4. While a user is typing a cron expression on the setup page, the system must show an automatically updating, human-readable description of when that expression will run.
5. The system must display a list of every light cycle stored in the system, showing each cycle's name, its ON/OFF cron expressions, and whether it is currently active.
6. At most one light cycle may be active at a time.
7. The system must allow a user to activate a light cycle from the list.
8. If a different cycle is already active when the user activates a new one, the system must ask for confirmation before switching; declining leaves the previously active cycle active and unchanged.
9. When a light cycle becomes active (including on confirmation of a switch), the system must immediately set the greenhouse LED to the on/off state it should be in at that moment, based on the newly active cycle's ON/OFF cron schedule.
10. While a light cycle is active, the system must automatically turn the greenhouse LED on when the active cycle's ON cron expression matches the current time, and off when its OFF cron expression matches, without manual intervention.
11. If the backend service restarts while a cycle is active, it must resume automatic scheduling for that cycle without requiring the user to reactivate it.
12. If no light cycle is active, the system must not automatically change the LED's state, leaving it under manual/dashboard control.

## Acceptance Criteria
- [x] Submitting the setup form with a name and two syntactically valid 6-field cron expressions creates a new light cycle that appears in the list.
- [x] Submitting the setup form with a cron expression that is not a valid 6-field cron expression shows an error and does not create a cycle.
- [x] Typing into either cron expression field updates a human-readable description of when it runs, without submitting the form or reloading the page.
- [ ] The setup page's list shows every light cycle currently stored in the database, including ones created in a previous session, and indicates which one (if any) is active.
- [ ] Activating a cycle while no other cycle is active makes it the active cycle immediately, with no confirmation prompt.
- [ ] Activating a cycle while a different cycle is active shows a confirmation prompt before changing anything.
- [ ] Confirming the prompt makes the newly selected cycle active and the previously active cycle inactive.
- [ ] Declining the prompt leaves the originally active cycle active and unchanged.
- [ ] Activating a cycle immediately sets the greenhouse LED to the on/off state implied by that cycle's schedule at the current time, without waiting for the next cron trigger.
- [ ] While a cycle is active, the greenhouse LED turns on at the moment its ON cron expression matches the current time, and off at the moment its OFF cron expression matches, without any manual action.
- [ ] Restarting the backend service while a cycle is active resumes automatic on/off scheduling for that cycle without any user action.

## User Stories
- [US1: Create a light cycle](us-1-create-a-light-cycle.md)
- [US2: View all light cycles](us-2-view-all-light-cycles.md)
- [US3: Activate a light cycle](us-3-activate-a-light-cycle.md)
- [US4: Automatic LED control by the active cycle's schedule](us-4-automatic-led-control-by-schedule.md)

## Tasks
- [x] [Backend #67](https://github.com/victorpigmeo/absolute-house-control/issues/67) — Create light cycle: entity + validated create endpoint (US1)
- [ ] [Backend #68](https://github.com/victorpigmeo/absolute-house-control/issues/68) — List all light cycles endpoint (US2)
- [ ] [Backend #69](https://github.com/victorpigmeo/absolute-house-control/issues/69) — Activate a light cycle endpoint (US3)
- [ ] [Backend #70](https://github.com/victorpigmeo/absolute-house-control/issues/70) — Automatic LED scheduling by active cycle's cron, resuming after restart (US4)
- [x] [Frontend #71](https://github.com/victorpigmeo/absolute-house-control/issues/71) — Create light cycle form with live cron preview (US1)
- [ ] [Frontend #72](https://github.com/victorpigmeo/absolute-house-control/issues/72) — List all light cycles with active indicator (US2)
- [ ] [Frontend #73](https://github.com/victorpigmeo/absolute-house-control/issues/73) — Activate a light cycle with switch confirmation (US3)
