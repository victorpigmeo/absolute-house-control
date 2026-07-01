# US2: Log out

**As a** user
**I want** to log out of the system
**So that** I can end my session and prevent further access from this browser without my credentials

## Scenarios

### Scenario 1: Logging out ends the session
- **Given** I am authenticated and on a dashboard page
- **When** I log out
- **Then** I am returned to the login page and my session (including the Keycloak SSO session) has ended

### Scenario 2: Logged-out access requires re-authentication
- **Given** I have logged out
- **When** I navigate to a dashboard page
- **Then** I am redirected to the login page and must authenticate again
