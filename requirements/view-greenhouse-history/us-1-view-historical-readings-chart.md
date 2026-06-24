# US1: View a chart of historical DHT22 readings

**As a** user
**I want** to see a chart of past temperature and humidity readings over time
**So that** I can spot trends and changes in greenhouse conditions

## Scenarios

### Scenario 1: Viewing a chart for a selected time range
- **Given** historical DHT22 readings exist for the greenhouse
- **When** I select a time range to view
- **Then** a chart displays the temperature and humidity readings recorded within that range

### Scenario 2: No data available for the selected range
- **Given** no DHT22 readings were recorded in a given time range
- **When** I select that time range
- **Then** the chart shows an empty/no-data state rather than an error
