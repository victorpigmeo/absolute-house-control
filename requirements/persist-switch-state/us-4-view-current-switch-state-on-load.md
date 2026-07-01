# US4: View current LED/fan state on page load

**As a** user
**I want** the dashboard to show the LED's and fan's real saved state when I open it
**So that** it isn't just showing stale defaults or whatever I last clicked in this session

## Scenarios

### Scenario 1: Dashboard loads persisted LED/fan state
- **Given** the LED and fan have known on/off states in the database
- **When** I open the greenhouse dashboard
- **Then** the LED and fan switches are shown reflecting their persisted states

### Scenario 2: Dashboard reflects state set outside the current session
- **Given** the LED's state was last changed from a different browser session, or before a restart
- **When** I open the greenhouse dashboard
- **Then** it shows that persisted state rather than a default
