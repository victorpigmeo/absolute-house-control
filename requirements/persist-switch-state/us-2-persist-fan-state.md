# US2: Persist the fan's state

**As a** user
**I want** the fan's on/off state to be saved
**So that** it isn't lost if the system restarts

## Scenarios

### Scenario 1: Fan state survives a restart
- **Given** the fan is currently on
- **When** the greenhouse service restarts
- **Then** querying the fan's state returns on

### Scenario 2: Default state before any command
- **Given** no command has ever been sent to the fan
- **When** I query its state
- **Then** it is reported as off
