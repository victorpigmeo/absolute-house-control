# Manage Greenhouse Devices

## Goal
Allow a user to control the greenhouse's LED light, fan, and water pump, and view DHT22 sensor readings.

## Functional Requirements
1. The system must allow a user to turn the LED light on or off.
2. The system must allow a user to turn the fan on or off.
3. The system must allow a user to turn the water pump on for a user-specified duration in seconds, after which it must turn off automatically.
4. The system must reject a water pump duration that is missing, non-numeric, or not a positive number, and must not start the pump in that case.
5. The system must display the current temperature and humidity reading from the DHT22 sensor.
6. The system must update the displayed LED, fan, and water pump state to reflect each device's actual current state, not just the last command sent.

## Acceptance Criteria
- [ ] Turning the LED control on or off sets the LED to that state and the UI reflects the actual state.
- [ ] Turning the fan control on or off sets the fan to that state and the UI reflects the actual state.
- [ ] Submitting a valid duration in seconds for the water pump turns the pump on, and the pump turns off automatically once that duration elapses.
- [ ] Submitting a missing, non-numeric, or non-positive duration for the water pump shows an error and does not turn the pump on.
- [ ] The dashboard displays the current DHT22 temperature and humidity readings, updated without a manual page reload.

## User Stories
- [US1: Turn the LED light on or off](us-1-turn-led-on-or-off.md)
- [US2: Turn the fan on or off](us-2-turn-fan-on-or-off.md)
- [US3: Run the water pump for a specified duration](us-3-run-water-pump-for-duration.md)
- [US4: View DHT22 sensor readings](us-4-view-dht22-sensor-readings.md)
