# US3: Activate a light cycle

**As a** user
**I want** to activate one light cycle at a time, with confirmation before replacing an already-active one
**So that** I don't accidentally switch my greenhouse's light schedule

## Scenarios

### Scenario 1: Activating when none is active
- **Given** no light cycle is currently active
- **When** I activate a cycle
- **Then** it becomes active immediately without a confirmation prompt

### Scenario 2: Confirming a switch
- **Given** a light cycle is already active
- **When** I activate a different cycle and confirm the prompt
- **Then** the newly selected cycle becomes active and the previous one becomes inactive

### Scenario 3: Declining a switch
- **Given** a light cycle is already active
- **When** I activate a different cycle and decline the prompt
- **Then** the originally active cycle remains active and unchanged

### Scenario 4: Immediate LED reconciliation on activation
- **Given** a cycle is activated whose schedule says the LED should currently be on (or off)
- **When** the activation completes
- **Then** the greenhouse LED is immediately set to that on/off state, rather than waiting for the next cron trigger

## Tasks
- [ ] [Backend #69](https://github.com/victorpigmeo/absolute-house-control/issues/69) — Activate a light cycle endpoint
- [ ] [Frontend #73](https://github.com/victorpigmeo/absolute-house-control/issues/73) — Activate a light cycle with switch confirmation
