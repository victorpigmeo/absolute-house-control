# Backend Spec

## Tech stack
- Language/framework: Java 25, Spring Boot 4.1.0
- Build tool: Gradle
- Database: PostgreSQL
- Auth provider: Keycloak
- Architecture style: microservices
- Deployment target: Kubernetes (Talos OS)
- Test framework: JUnit 5, AssertJ, Mockito (via `spring-boot-starter-test`), Testcontainers for integration tests
- Static analysis / lint: Spotless (formatting) + Error Prone (compile-time bug detection)
- Migration tool: Flyway

## Confirmed decisions
- **Service communication**: event-driven (message broker) for asynchronous events/state changes, plain REST for synchronous service-to-service and client-facing calls. The specific broker (e.g. Kafka, RabbitMQ) is not yet chosen.
- **Gradle module layout**: a single multi-module Gradle build under `backend/` (one root `settings.gradle.kts`), with each microservice as its own subproject under `backend/services/<name>/` and shared cross-service code in a `backend/common/` module. Each service subproject still produces its own independently deployable artifact, built into a container image via Spring Boot's Buildpacks support (`bootBuildImage`) rather than separate per-service Gradle builds/repos.
- **Concrete domain microservices**: starting with two — `greenhouse` (manages everything related to the greenhouse) and `lighting` (manages everything related to house lights). More services may be added later as scope grows.
- **Per-service architecture**: each microservice uses a layered package structure — `application` (controllers, exception advice, message consumers, DTOs, mappers), `domain` (domain services, entities, exceptions), and `infrastructure` (configuration, repositories, message producers). All inbound input (HTTP requests, consumed messages) is mapped to a domain entity or command before entering the domain layer; all domain entities are mapped to response DTOs before being returned over HTTP.
- **Database strategy**: a single shared PostgreSQL instance with schema-per-service isolation — each microservice (e.g. `greenhouse`, `lighting`) gets its own schema and its own DB user/credentials scoped to only that schema (least privilege), rather than a separate Postgres instance per service.
- **API gateway approach**: no dedicated app-level gateway service (e.g. Spring Cloud Gateway) — routing/exposure is handled by plain Kubernetes Ingress, and each microservice validates Keycloak-issued JWTs itself via Spring Security's OAuth2 resource server. The specific ingress controller and TLS strategy are tracked in [spec/infra-spec.md](infra-spec.md).
- **Service discovery / configuration management**: no Spring Cloud infrastructure (Eureka/Consul, Config Server) — service discovery uses Kubernetes' built-in DNS (services call each other by their stable K8s Service DNS name), and configuration uses Kubernetes ConfigMaps (non-sensitive) and Secrets (DB credentials, Keycloak client secrets, broker credentials), consumed via Spring Boot's standard externalized configuration (env vars / mounted files). Config changes are applied via redeploy rather than dynamic runtime refresh.
- **Keycloak realm/client structure**: a single realm, `house-control`. The frontend uses one public client, `web-ui` (Authorization Code + PKCE flow). Each microservice (`greenhouse`, `lighting`) is its own confidential client, acting as a resource server (validates JWTs via the realm's JWKS) with its own service account for machine-to-machine calls with no user context; synchronous service-to-service REST calls made on behalf of a logged-in user relay the original bearer token rather than minting a new one.
- **Greenhouse device integration (ESP32)**: each ESP32 exposes a small HTTP REST server on the local network (sensor telemetry via GET, actuator commands via POST); the `greenhouse` service is the client, calling devices directly over plain HTTP — no broker, no discovery protocol. Each device has a fixed/static IP, entered by the user via a frontend settings page and persisted in the `greenhouse` service's own schema.
- **Lighting device integration (Tuya)**: Tuya devices are controlled over Tuya's local LAN protocol rather than Tuya's Cloud API, to avoid a hard runtime dependency on Tuya's cloud (there is no official Java SDK for the local protocol — `com.tuya:tuya-spring-boot-starter` is Cloud-API-only and was ruled out for this reason). A dedicated sidecar process (Python, wrapping the actively-maintained `tinytuya` library) owns local-protocol-version handling and on-LAN device discovery; the `lighting` microservice talks to it over a plain internal REST API rather than implementing the protocol itself. Device `local_key`s are obtained out-of-band for now; cloud-based key bootstrapping is deferred.
- **Testing approach**: unit tests use JUnit 5/AssertJ/Mockito against the domain layer in isolation; integration tests use Testcontainers to run against a real Postgres instance (and, once chosen, Keycloak/the broker) rather than mocking infrastructure — consistent with this spec's general preference for testing against real dependencies.
- **Static analysis / lint tooling**: Spotless (e.g. `google-java-format`) for auto-fixable formatting, plus Error Prone for compile-time bug-pattern detection. Checkstyle/PMD and SonarQube were considered and skipped — they add more rule-curation overhead (PMD/Checkstyle) or require a running server (SonarQube) than this lightweight pairing, for similar payoff on a solo-maintained backend.
- **Migrations**: Flyway, run per-service against its own schema — each microservice owns its own migration history table and `db/migration` script set within its own schema, consistent with the schema-per-service/least-privilege database strategy above. Migrations run via Spring Boot's default Flyway auto-run on startup.
- **Local dev loop**: Postgres and Keycloak run as local containers during development, with the Spring Boot services connecting to them directly (rather than via Testcontainers-managed dev-mode). The message broker isn't part of this yet since it isn't chosen.

## Open / not yet defined
- Message broker choice (deliberately deferred until a concrete async use case requires one)
- Tuya `local_key` provisioning automation (e.g. a one-time Tuya Cloud API bootstrap step) — deferred; keys are obtained out-of-band for now.
- Local dev story for device integrations (ESP32, Tuya) — e.g. whether to stub/simulate devices for development away from the physical hardware, or always develop on the home LAN with real hardware reachable — deferred.
