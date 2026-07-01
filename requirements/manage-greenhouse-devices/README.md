# Manage Greenhouse Devices

## Goal
Allow a user to control the greenhouse's LED light, fan, and water pump, and view DHT22 sensor readings.

## Functional Requirements
1. The system must allow a user to turn the LED light on or off.
2. The system must allow a user to turn the fan on or off.
3. The system must allow a user to turn the water pump on for a user-specified duration in seconds, up to a maximum of 10 seconds, after which it must turn off automatically.
4. The system must reject a water pump duration that is missing, not a positive whole number of seconds, or exceeds the 10-second maximum, and must not start the pump in that case.
5. The system must display the current temperature and humidity reading from the DHT22 sensor.
6. The system must update the displayed LED, fan, and water pump state to reflect each device's actual current state, not just the last command sent, within 2 seconds of the change occurring.

## Acceptance Criteria
- [x] Turning the LED control on or off sets the LED to that state and the UI reflects the actual state within 2 seconds.
- [x] Turning the fan control on or off sets the fan to that state and the UI reflects the actual state within 2 seconds.
- [x] Submitting a valid duration (a positive whole number of seconds, up to 10) for the water pump turns the pump on, and the pump turns off automatically once that duration elapses.
- [x] Submitting a missing, non-numeric, fractional, non-positive, or over-10-second duration for the water pump shows an error and does not turn the pump on.
- [ ] The dashboard displays the current DHT22 temperature and humidity readings, updated without a manual page reload.

## User Stories
- [US1: Turn the LED light on or off](us-1-turn-led-on-or-off.md)
- [US2: Turn the fan on or off](us-2-turn-fan-on-or-off.md)
- [US3: Run the water pump for a specified duration](us-3-run-water-pump-for-duration.md)
- [US4: View DHT22 sensor readings](us-4-view-dht22-sensor-readings.md)
