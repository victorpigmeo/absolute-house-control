# US4: Automatic LED control by the active cycle's schedule

**As a** user
**I want** the greenhouse LED to turn on and off automatically according to the active light cycle's cron expressions
**So that** I don't have to manually toggle the light for Veg/Flora schedules

## Scenarios

### Scenario 1: LED turns on at the ON cron time
- **Given** a light cycle is active
- **When** the current time matches its ON cron expression
- **Then** the greenhouse LED is turned on

### Scenario 2: LED turns off at the OFF cron time
- **Given** a light cycle is active
- **When** the current time matches its OFF cron expression
- **Then** the greenhouse LED is turned off

### Scenario 3: No automatic changes when no cycle is active
- **Given** no light cycle is active
- **When** time passes
- **Then** the greenhouse LED's state is not changed automatically, and manual/dashboard control still works

### Scenario 4: Scheduling resumes after a restart
- **Given** a light cycle is active
- **When** the backend service restarts
- **Then** automatic ON/OFF scheduling for that cycle resumes without the user needing to reactivate it
