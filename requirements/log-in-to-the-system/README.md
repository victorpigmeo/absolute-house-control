# Log In To The System

## Goal
Allow a user to log in to access the system.

## Functional Requirements
1. The system must prevent access to any page other than the login page until the user is authenticated.
2. The system must allow a user to submit credentials on the login page and authenticate.
3. On successful authentication, the system must grant access and return the user to the page they originally requested, if any.
4. On failed authentication, the system must show an error message and leave the user unauthenticated on the login page.
5. The system must keep the user authenticated across page reloads and navigation until the session expires or the user logs out.

## Acceptance Criteria
- [ ] Visiting any authenticated page while logged out redirects to the login page.
- [ ] Submitting valid credentials on the login page authenticates the user and redirects to the dashboard (or the originally requested page).
- [ ] Submitting invalid credentials on the login page shows an error message and leaves the user unauthenticated on the login page.
- [ ] Reloading the page or navigating to another authenticated page after login does not require re-entering credentials, until the session expires or the user logs out.

## User Stories
- [US1: Log in with valid credentials](us-1-log-in-with-credentials.md)
