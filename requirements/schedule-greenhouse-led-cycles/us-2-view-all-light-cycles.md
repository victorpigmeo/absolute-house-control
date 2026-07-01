# US2: View all light cycles

**As a** user
**I want** to see a list of every light cycle stored in the system
**So that** I know what schedules exist and which one is currently controlling the LED

## Scenarios

### Scenario 1: Listing persisted cycles
- **Given** two or more light cycles were created in a previous session
- **When** I open the setup page
- **Then** I see all of them listed with their name and ON/OFF cron expressions

### Scenario 2: Indicating the active cycle
- **Given** one light cycle is marked active
- **When** I view the list
- **Then** that cycle is visibly marked as active and the others are not

## Tasks
- [ ] [Backend #68](https://github.com/victorpigmeo/absolute-house-control/issues/68) — List all light cycles endpoint
- [ ] [Frontend #72](https://github.com/victorpigmeo/absolute-house-control/issues/72) — List all light cycles with active indicator
