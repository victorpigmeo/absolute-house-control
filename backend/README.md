# Backend — absolute-house-control

## Prerequisites

- Docker, for local Postgres/Keycloak (`docker compose`) and for running the
  Testcontainers-backed integration test.
- No JDK needs to be pre-installed for building: Gradle's toolchain support
  auto-provisions JDK 25 on first build (requires network access to Foojay's
  Disco API). You still need *some* JDK available to run the Gradle daemon
  itself; if none is installed, install any JDK 17+ first.

## Local dev environment

```
docker compose up -d
```

Brings up Postgres (service name `db`, with a least-privilege
`sa_absolute_house_control` role pre-created — the `greenhouse` schema
itself is created by Flyway on first run, not by this script) and Keycloak
(service name `keycloak`, with the `absolute-house-control` realm and
`greenhouse` client pre-imported). Both service names double as their DNS
hostnames for anything sharing the compose network (e.g. a devcontainer
attached to it) — the `local` Spring profile connects to
`db:5432`/`keycloak:8080` directly rather than via published `localhost`
ports. Postgres has a `pg_isready` healthcheck; Keycloak has none (its
health-endpoint setup is version-specific and wasn't verified in this
environment), so it may take a few seconds after start before
`--import-realm` finishes and it accepts connections.

If you have an existing `postgres-data` volume from before the database
was renamed to `absolute_house_control`, recreate it (`docker compose down
-v`) — `POSTGRES_DB` only takes effect when Postgres initializes a fresh,
empty data directory.

The `greenhouse` client's secret in
`docker/keycloak/import/absolute-house-control-realm.json` is a dev-only
placeholder — never reuse it anywhere near production.

## Building and testing

- `./gradlew build` — compiles, runs the Spotless format check, runs Error
  Prone static analysis, and runs unit/slice tests only (excludes
  `@Tag("integration")` tests). Does **not** require Docker.
- `./gradlew integrationTest` — runs Testcontainers-backed integration tests
  (currently: a Postgres + Flyway integration test for the `greenhouse`
  service). Requires Docker. Not part of `./gradlew build`/`check` by design,
  so the default build stays usable in Docker-less environments.
- `./gradlew spotlessApply` — auto-fixes formatting violations.

## Running the service

```
./gradlew :services:greenhouse:bootRun
```

Serves on `localhost:8081`. Requires `docker compose up -d` to already be
running (Postgres + Keycloak).

The service has two Spring profiles, `local` and `prod`. `local` is the
default — with no `SPRING_PROFILES_ACTIVE` set, `bootRun` and plain test
runs behave exactly as before. Override local defaults with env vars
`DATABASE_USERNAME`, `GREENHOUSE_DB_PASSWORD`, `KEYCLOAK_ISSUER_URI`, and
`ESP32_BASE_URL` if needed. `ESP32_BASE_URL` defaults to
`http://192.168.18.26`, the greenhouse
ESP32 board's LAN address (see [spec/backend-spec.md](../spec/backend-spec.md))
— unreachable from most dev sandboxes, so actuator endpoints can only be
exercised against the real board from a machine on that LAN. This default
is the same in every profile, since it's the one physical board on the
one home LAN, not an environment-specific value.

`prod` (`SPRING_PROFILES_ACTIVE=prod`) has **no defaults** for
`GREENHOUSE_DB_URL`, `GREENHOUSE_DB_PASSWORD`, or `KEYCLOAK_ISSUER_URI` —
all three must be supplied (e.g. via Kubernetes ConfigMap/Secret-mounted
env vars), or the application fails fast at startup with a "could not
resolve placeholder" error rather than silently falling back to a
local-dev credential.

## API documentation

`greenhouse` serves its OpenAPI spec at `/v3/api-docs` via
`springdoc-openapi-starter-webmvc-ui`. This is what the frontend's
OpenAPI-codegen step (see [spec/frontend-spec.md](../spec/frontend-spec.md))
generates its typed client against — new services should add the same
dependency so they're covered too.

## Known sandbox limitation (as of this PR)

This change was implemented in an environment without a Docker/container
runtime available. Compile, Spotless, Error Prone, and the security slice
test (`./gradlew build`/`./gradlew test`) were verified green there. The
Testcontainers Postgres integration test (`./gradlew integrationTest`) and
`docker compose up -d` (Postgres + Keycloak, including the Keycloak
realm-export import) could **not** be executed/verified in that environment
and are deferred until Docker/Docker-in-Docker is available — see the PR
description for details.

Both test classes set a dummy, unreachable `issuer-uri` test property so the
OAuth2 resource server's `JwtDecoder` bean can construct without a live
Keycloak. This was confirmed safe for `SmokeControllerSecurityTest` in this
sandbox (which does have real outbound network access): both cases ran in
well under a second with no DNS/timeout errors, confirming Spring Security
builds that decoder lazily rather than resolving the issuer's OIDC metadata
at context-startup time — neither test actually triggers a decode (the 401
case sends no token; the 200 case uses `spring-security-test`'s `jwt()`
post-processor, which injects authentication directly and never calls the
decoder). The same should hold for `GreenhouseFlywayIntegrationTest`'s
identical setup, but that test couldn't be run here to confirm it directly.
