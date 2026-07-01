# US3: Record water pump activations with status

**As a** user
**I want** every water pump run recorded along with whether it completed
**So that** there's a history of when the pump ran and I can tell which activations finished normally

## Scenarios

### Scenario 1: Starting the pump creates a PENDING activation record
- **Given** the pump is off
- **When** I start it for 8 seconds
- **Then** a new activation record is persisted with status `PENDING`, the start time, and an 8-second duration, before the start command reaches the device

### Scenario 2: Activation marked COMPLETED after a successful auto-off
- **Given** the pump has a `PENDING` activation record and is running
- **When** its duration elapses and the auto-off command runs
- **Then** that activation record's status is updated to `COMPLETED`

### Scenario 3: Activation stays PENDING if auto-off never runs
- **Given** the pump has a `PENDING` activation record
- **When** the service restarts or crashes before the auto-off command executes
- **Then** the activation record's status remains `PENDING`

### Scenario 4: Activations accumulate rather than overwrite
- **Given** the pump has already run at least once
- **When** I start it again for a valid duration
- **Then** a new activation record is added to the log rather than replacing the previous one
