# US1: Create a light cycle

**As a** user
**I want** to create a named light cycle with an ON cron expression and an OFF cron expression
**So that** I can define reusable LED schedules (e.g. Veg, Flora) without changing code

## Scenarios

### Scenario 1: Creating a cycle with valid input
- **Given** the setup page is open
- **When** I submit the form with a name, a valid ON cron expression, and a valid OFF cron expression
- **Then** a new light cycle is persisted and appears in the cycle list

### Scenario 2: Rejecting an invalid ON cron expression
- **Given** the setup page is open
- **When** I submit the form with an ON cron expression that is not valid 6-field cron syntax
- **Then** the system shows an error identifying the invalid field and does not create a cycle

### Scenario 3: Rejecting an invalid OFF cron expression
- **Given** the setup page is open
- **When** I submit the form with an OFF cron expression that is not valid 6-field cron syntax
- **Then** the system shows an error identifying the invalid field and does not create a cycle

### Scenario 4: Live human-readable preview while typing
- **Given** the setup page is open
- **When** I type a valid cron expression into the ON or OFF field
- **Then** a human-readable description of when it will run updates automatically, without submitting the form
