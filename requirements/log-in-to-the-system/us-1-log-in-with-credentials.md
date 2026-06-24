# US1: Log in with valid credentials

**As a** user
**I want** to log in with my credentials
**So that** I can securely access my house control dashboard

## Scenarios

### Scenario 1: Successful login redirects to the dashboard
- **Given** I am not authenticated and navigate to a dashboard page
- **When** I am redirected to the login page and submit valid credentials
- **Then** I am authenticated and redirected to the dashboard

### Scenario 2: Failed login shows an error
- **Given** I am on the login page
- **When** I submit invalid credentials
- **Then** I see an error message and remain unauthenticated on the login page

### Scenario 3: Session persists across navigation
- **Given** I have successfully logged in
- **When** I reload the page or navigate to another dashboard page
- **Then** I remain authenticated and am not redirected back to the login page

### Scenario 4: Returning to the originally requested page
- **Given** I navigate directly to a specific dashboard page while unauthenticated
- **When** I log in successfully
- **Then** I am redirected to that originally requested page rather than a generic default page
