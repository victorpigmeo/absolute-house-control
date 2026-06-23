# Development Process

## Overview

This repo is spec-only today (see [spec/](../spec/)). This document defines
the *process* by which future implementation work is tracked, branched,
tested, and merged — it does **not** choose backend/frontend testing or
static-analysis tooling; those remain open decisions (see
[Open items / deferred decisions](#open-items--deferred-decisions)).

Work is split into three independent areas, matching the repo structure:
backend, frontend, infra. Each area has its own GitHub Project, its own issue
template, and its own branch prefix.

## GitHub Projects layout

| Area     | Project title                          | Owner          | Linked repo                              | Project # | URL |
|----------|------------------------------------------|----------------|-------------------------------------------|-----------|-----|
| Backend  | `absolute-house-control — Backend`       | `victorpigmeo` | `victorpigmeo/absolute-house-control`     | 5         | https://github.com/users/victorpigmeo/projects/5 |
| Frontend | `absolute-house-control — Frontend`      | `victorpigmeo` | `victorpigmeo/absolute-house-control`     | 6         | https://github.com/users/victorpigmeo/projects/6 |
| Infra    | `absolute-house-control — Infra`         | `victorpigmeo` | `victorpigmeo/absolute-house-control`     | 7         | https://github.com/users/victorpigmeo/projects/7 |

Each project uses the default GitHub Projects v2 `Status` field
(`Todo` / `In Progress` / `Done`) — no custom fields. Area is already
expressed by which project a task lives in and by its `area:*` label, so a
separate "Area" field would be redundant.

These 3 projects are new and dedicated to this repo. The account also has 4
unrelated legacy projects (`Infra`, `lofirankd — Frontend`,
`lofirankd — Backend`, `PDUI`) — those are out of scope and are not touched
by this process.

## Task / issue fields and templates

Every task is a GitHub Issue, filed using the template for its area:

- `.github/ISSUE_TEMPLATE/backend-task.md` — label `area:backend`
- `.github/ISSUE_TEMPLATE/frontend-task.md` — label `area:frontend`
- `.github/ISSUE_TEMPLATE/infra-task.md` — label `area:infra`

Required fields:

- **Description** — max 1000 characters. Enforced by convention/comment
  only; classic Markdown issue templates have no native length validation.
- **Implementation Steps** — a numbered, step-by-step plan for the change.
- **Acceptance Criteria** — **backend and frontend tasks only**. Must
  include at least one criterion requiring an automated test exercising the
  new behavior, and at least one criterion requiring the project's static
  analysis/lint check to pass. **Infra tasks do not require acceptance
  criteria** and the infra template omits this section entirely.

When filing a task, add it to the matching project, e.g.:

```
gh issue create -R victorpigmeo/absolute-house-control \
  --template "Backend task" \
  --title "[Backend] <summary>" \
  -l area:backend \
  -p "absolute-house-control — Backend"
```

## Branching

Always branch from up-to-date `master`:

```
git checkout master
git pull origin master
git checkout -b <prefix>/<task-id>
```

Branch prefixes:

- `backend/<issue-number>` — backend tasks
- `frontend/<issue-number>` — frontend tasks
- `infra/<issue-number>` — infra tasks

The task-id is the GitHub issue number. It's the natural unique identifier
already produced by `gh issue create`, it's immutable, and it makes the
branch name self-documenting — `gh issue view <number>` works straight off
the branch name.

## Implementation workflow per task type

### Backend / frontend tasks

Mechanics are identical between the two; only the branch prefix and which
project's test/lint commands run differ.

1. Pick up an issue from the relevant Project, move it to `In Progress`.
2. Branch per [Branching](#branching) above.
3. Implement the change per the issue's Implementation Steps.
4. Run that area's automated tests (backend tests for backend tasks,
   frontend tests for frontend tasks).
5. Run that area's static analysis/lint check.
6. Confirm every Acceptance Criterion in the issue is actually met.
7. Commit, referencing the issue (e.g. `Refs #<n>` or `Closes #<n>`).
8. Push the branch and open a PR targeting `master`:
   ```
   gh pr create -R victorpigmeo/absolute-house-control \
     --base master --head <branch> \
     --title "..." --body "Closes #<n>\n\n..."
   ```
9. Move the Project item to `Done` only after the PR is merged.

### Infra tasks

Same checkout → branch → implement → commit → PR mechanics, with branch
prefix `infra/<task-id>`. Infra tasks have **no test-run gate and no
acceptance criteria** — any manual verification (e.g. `helm lint`,
`kubectl apply --dry-run`) belongs in the issue's Implementation Steps, not
as a formal gate.

## Testing gate

A backend or frontend PR must not be opened until that project's automated
tests and static analysis/lint check both pass. Acceptance criteria should
be phrased like:

- `- [ ] The project's automated tests pass, including a new/updated test that exercises <behavior>.`
- `- [ ] The project's static analysis/lint check passes with no new violations.`

## PR & review policy

- All PRs target `master`.
- PRs are created only via `gh pr create` — no direct pushes to `master`, no
  GitHub API calls, no web UI.

> **IMPORTANT:** Claude (or any automated agent) must never run
> `gh pr merge` or otherwise merge a pull request, under any circumstances.
> Merging is a human-only action, performed by a repository maintainer after
> review.

## Open items / deferred decisions

- Backend test framework — TBD.
- Backend static analysis tool — TBD.
- Frontend test framework — TBD.
- Frontend lint tool — TBD (Next.js ships ESLint by default, but
  selection/config is still an open decision per [spec/frontend-spec.md](../spec/frontend-spec.md)).
- CI automation (e.g. GitHub Actions running tests/lint on PRs) — not
  addressed by this process yet; a natural follow-up once tooling above is
  chosen.
- Custom Project fields beyond the default `Status` — intentionally
  deferred; keep setup minimal until a need arises.
