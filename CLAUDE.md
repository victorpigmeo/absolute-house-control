# absolute-house-control

A web-based home/house control system, structured as a monorepo.

**Status: spec-only.** No application code has been written yet, and most
architecture decisions beyond the tech stack and constraints below are
intentionally not yet defined — they will be designed in a future session.

## Structure

- `backend/` — Java / Spring Boot 4 microservices, built with Gradle. See [spec/backend-spec.md](spec/backend-spec.md).
- `frontend/` — Next.js (TypeScript, strict) web UI. See [spec/frontend-spec.md](spec/frontend-spec.md).
- `infra/` — Kubernetes (Talos OS) cluster and deployment config. See [spec/infra-spec.md](spec/infra-spec.md).

Each folder uses only its own native tooling (Gradle / npm or pnpm / kubectl-Helm)
— there is no monorepo build tool unifying them.

Read the relevant spec file before writing code in a folder.

## Development process

Tasks are tracked in per-area GitHub Projects and follow a branch/PR
workflow described in [docs/development-process.md](docs/development-process.md).
Read it before picking up or filing a backend, frontend, or infra task.
