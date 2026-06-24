# US4: View DHT22 sensor readings

**As a** user
**I want** to see the current temperature and humidity from the greenhouse's DHT22 sensor
**So that** I can monitor greenhouse conditions remotely

## Scenarios

### Scenario 1: Viewing the current reading
- **Given** the greenhouse page is open
- **When** the DHT22 sensor has reported a temperature and humidity reading
- **Then** the dashboard displays the current temperature and humidity values

### Scenario 2: Readings update without a manual refresh
- **Given** the greenhouse page is open and displaying a sensor reading
- **When** the DHT22 sensor reports a new reading
- **Then** the displayed temperature and humidity update without me reloading the page
