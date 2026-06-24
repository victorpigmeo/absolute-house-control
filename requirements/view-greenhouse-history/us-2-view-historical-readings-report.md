# US2: View a report of historical DHT22 readings

**As a** user
**I want** to see a summary report of past temperature and humidity readings
**So that** I can quickly understand overall greenhouse conditions without reading a full chart

## Scenarios

### Scenario 1: Viewing a summary report for a selected time range
- **Given** historical DHT22 readings exist for the greenhouse
- **When** I select a time range to view
- **Then** the report shows the minimum, maximum, and average temperature and humidity recorded within that range

### Scenario 2: No data available for the selected range
- **Given** no DHT22 readings were recorded in a given time range
- **When** I select that time range
- **Then** the report shows an empty/no-data state rather than an error
