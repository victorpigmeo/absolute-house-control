# Persist Switch State

## Goal
Persist the last-known state of the greenhouse's LED, fan, and water pump to the database so it survives service restarts and can be read back instead of only reflecting the last command sent in the current session.

## Functional Requirements
1. The system must persist the LED's on/off state to the database whenever it changes.
2. The system must persist the fan's on/off state to the database whenever it changes.
3. Before sending the pump's start command, the system must create a new pump activation record in the database with status `PENDING`, including the start time and requested duration.
4. Once the pump's scheduled auto-off command has run, the system must update the corresponding activation record's status to `COMPLETED`.
5. Before any command has ever been sent to the LED or fan, the system must report its state as off.
6. The system must expose the current state of the LED and fan via a read (GET) API endpoint. The pump's activation log is persisted but not exposed via this endpoint.
7. The frontend must load the LED's and fan's current state from this endpoint when the greenhouse dashboard page loads, and display it accordingly.

## Acceptance Criteria
- [ ] Turning the LED on or off persists the new state to the database.
- [ ] Turning the fan on or off persists the new state to the database.
- [ ] Restarting the greenhouse service preserves the LED's and fan's last-known state instead of resetting to a default.
- [ ] Before any command has ever been sent to the LED or fan, the read endpoint reports its state as off.
- [ ] Starting the water pump creates a new activation record with status `PENDING`, a recorded start time, and the requested duration, before the start command is sent to the device.
- [ ] After the pump's auto-off command runs successfully, the corresponding activation record's status is updated to `COMPLETED`.
- [ ] If the service restarts or crashes before the auto-off command runs, the activation record's status remains `PENDING`.
- [ ] Loading the greenhouse dashboard page displays the LED and fan states as currently persisted in the database, not just state from commands sent during the current browser session.

## User Stories
- [US1: Persist the LED's state](us-1-persist-led-state.md)
- [US2: Persist the fan's state](us-2-persist-fan-state.md)
- [US3: Record water pump activations with status](us-3-record-pump-activations.md)
- [US4: View current LED/fan state on page load](us-4-view-current-switch-state-on-load.md)

## Implementation Tasks
- [ ] [Backend #53: Persist LED/fan actuator state to Postgres](https://github.com/victorpigmeo/absolute-house-control/issues/53)
- [ ] [Backend #54: Expose LED/fan state via GET endpoint](https://github.com/victorpigmeo/absolute-house-control/issues/54)
- [ ] [Backend #55: Record water pump activations with status](https://github.com/victorpigmeo/absolute-house-control/issues/55)
- [ ] [Frontend #56: Load persisted LED/fan state on dashboard load](https://github.com/victorpigmeo/absolute-house-control/issues/56)
