# absolute-house-control

A web-based home/house control system, structured as a monorepo.

**Status: specs mostly settled, implementation underway.** The
architecture decisions in `spec/` are largely confirmed (see each spec
file's "Confirmed decisions" section). Real backend (`greenhouse` service)
and frontend (device-control UI) code is already implemented and merged;
`lighting` and infra build-out (provisioning the actual cluster) have not
started yet.

## Structure

- `backend/` — Java / Spring Boot 4 microservices, built with Gradle. See [spec/backend-spec.md](spec/backend-spec.md).
- `frontend/` — Next.js (TypeScript, strict) web UI. See [spec/frontend-spec.md](spec/frontend-spec.md).
- `infra/` — Kubernetes (Talos OS) cluster and deployment config. See [spec/infra-spec.md](spec/infra-spec.md).

Each folder uses only its own native tooling (Gradle / npm / kubectl-Helm)
— there is no monorepo build tool unifying them.

Read the relevant spec file before writing code in a folder.

## Requirements

Product requirements and user stories (goal, functional requirements,
acceptance criteria, user stories) are tracked in
[requirements/](requirements/), one folder per requirement, following
[docs/requirements-process.md](docs/requirements-process.md). A requirement
can span more than one area, so this is independent of the per-area
GitHub Projects below.

## Development process

Tasks are tracked in per-area GitHub Projects and follow a branch/PR
workflow described in [docs/development-process.md](docs/development-process.md).
Read it before picking up or filing a backend, frontend, or infra task.
