# US1: Persist the LED's state

**As a** user
**I want** the LED's on/off state to be saved
**So that** it isn't lost if the system restarts

## Scenarios

### Scenario 1: LED state survives a restart
- **Given** the LED is currently on
- **When** the greenhouse service restarts
- **Then** querying the LED's state returns on

### Scenario 2: Default state before any command
- **Given** no command has ever been sent to the LED
- **When** I query its state
- **Then** it is reported as off
