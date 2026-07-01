# US3: Run the water pump for a specified duration

**As a** user
**I want** to turn the water pump on for a number of seconds that I specify
**So that** I can water the greenhouse a controlled amount without watching the clock myself

## Scenarios

### Scenario 1: Starting the pump for a valid duration
- **Given** the water pump is off
- **When** I start the pump with a duration of 8 seconds
- **Then** the pump turns on immediately

### Scenario 2: Pump turns off automatically after the duration
- **Given** the water pump is running for a specified duration
- **When** that duration elapses
- **Then** the pump turns off automatically without further action from me

### Scenario 3: Rejecting an invalid duration
- **Given** I am about to start the water pump
- **When** I submit a duration that is missing, non-numeric, fractional, not a positive number, or exceeds 10 seconds
- **Then** the system shows an error and the pump does not turn on
