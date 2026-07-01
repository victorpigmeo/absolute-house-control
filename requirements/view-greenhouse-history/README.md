# View Greenhouse History

## Goal
Allow a user to view historical DHT22 sensor readings as charts and reports.

## Description
Builds on the live DHT22 readings introduced in the Manage Greenhouse Devices requirement, adding persistence of those readings over time and historical visualization.

## Functional Requirements
1. The system must record a DHT22 temperature and humidity reading, along with the time it was taken, at least once every 60 seconds — independently of whether the live dashboard is open.
2. The system must retain historical DHT22 readings so they remain available for later viewing.
3. The system must allow a user to select a time range and view a chart of the temperature and humidity readings recorded within that range.
4. The system must allow a user to select a time range and view a report showing the minimum, maximum, and average temperature and humidity recorded within that range.

## Acceptance Criteria
- [ ] A DHT22 reading is recorded and later retrievable for any time range that includes it.
- [ ] Selecting a time range displays a chart of the temperature and humidity readings recorded within that range.
- [ ] Selecting a time range with no recorded readings shows an empty/no-data state rather than an error.
- [ ] Selecting a time range displays a report showing the minimum, maximum, and average temperature and humidity recorded within that range.

## User Stories
- [US1: View a chart of historical DHT22 readings](us-1-view-historical-readings-chart.md)
- [US2: View a report of historical DHT22 readings](us-2-view-historical-readings-report.md)
