# Development Process

## Overview

Specs are mostly settled (see [spec/](../spec/)), and backend/frontend
implementation is already underway. This document defines the *process* by
which implementation work is tracked, branched, tested, and merged — it
does not itself choose testing/static-analysis tooling. Backend and
frontend test and lint tooling are both chosen (see
[spec/backend-spec.md](../spec/backend-spec.md), [spec/frontend-spec.md](../spec/frontend-spec.md)).

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

- **Description** — the whole issue body (Description + Implementation
  Steps + Acceptance Criteria + Branch combined) must be 1000 characters or
  fewer. Enforced by convention/comment only; classic Markdown issue
  templates have no native length validation.
- **Implementation Steps** — a numbered, step-by-step plan for the change.
- **Acceptance Criteria** — **backend and frontend tasks only**. Must
  include at least one criterion requiring an automated test exercising the
  new behavior, and at least one criterion requiring the project's static
  analysis/lint check to pass. **Infra tasks do not require acceptance
  criteria** and the infra template omits this section entirely.

When filing a task interactively, `--template` pre-fills the editor:

```
gh issue create -R victorpigmeo/absolute-house-control \
  --template "Backend task" \
  -l area:backend \
  -p "absolute-house-control — Backend"
```

`--template` only works interactively (it needs a TTY to open the editor).
For scripted/non-interactive creation (e.g. by an agent), skip `--template`
and pass the full body matching the template structure directly:

```
gh issue create -R victorpigmeo/absolute-house-control \
  --title "[Backend] <summary>" \
  --body "$(cat <<'EOF'
## Description
...

## Implementation Steps
1. ...

## Acceptance Criteria
- [ ] The project's automated tests pass, including a new/updated test that exercises <behavior>.
- [ ] The project's static analysis/lint check passes with no new violations.
EOF
)" \
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

> **IMPORTANT — move to `In Progress` first:** Before doing anything else on
> a task — including exploratory research or writing a plan — move its
> GitHub Project item to `In Progress`. This is the very first action, not
> a step that happens incidentally before the PR. It applies to backend,
> frontend, and infra tasks alike.
>
> - If plan mode is already active when picking up the task: exit plan
>   mode, move the Project item to `In Progress`, then re-enter plan mode
>   to plan the task.
> - If plan mode is not active: move the Project item to `In Progress`
>   first, then enter plan mode to plan the task.

### Backend / frontend tasks

Mechanics are identical between the two; only the branch prefix and which
project's test/lint commands run differ.

1. Pick up an issue from the relevant Project, move it to `In Progress`
   (before any planning or other work — see callout above).
2. Branch per [Branching](#branching) above.
3. Implement the change per the issue's Implementation Steps.
4. Run that area's automated tests (backend tests for backend tasks,
   frontend tests for frontend tasks).
5. Run that area's static analysis/lint check.
6. Confirm every Acceptance Criterion in the issue is actually met.
7. If the issue's Description references a requirement or user story (per
   [requirements-process.md](requirements-process.md)), update that
   requirement's Acceptance Criteria checklist in
   `requirements/<goal-slug>/README.md` to check off anything this task
   just completed, before committing.
8. Before committing, run exactly one code-review sub-agent over the
   working diff, scoped to correctness bugs and possible runtime problems
   only — not style, reuse, simplification, efficiency, or other cleanup.
   Display its findings, ordered from most to least critical. Ask the user
   whether to continue (commit as-is) or address the issues first — do not
   commit until they respond.
9. Commit, referencing the issue (e.g. `Refs #<n>` or `Closes #<n>`).
10. Push the branch and open a PR targeting `master`:
    ```
    gh pr create -R victorpigmeo/absolute-house-control \
      --base master --head <branch> \
      --title "..." --body "Closes #<n>\n\n..."
    ```
11. Move the Project item to `Done` only after the PR is merged.

### Infra tasks

Same first step as above — move the Project item to `In Progress` before
any planning or other work — then the same checkout → branch → implement →
commit → PR mechanics, with branch prefix `infra/<task-id>`. Infra tasks
have **no test-run gate and no acceptance criteria** — any manual
verification (e.g. `helm lint`, `kubectl apply --dry-run`) belongs in the
issue's Implementation Steps, not as a formal gate. The mandatory
code-review sub-agent step (step 8 in the backend/frontend workflow above)
still applies to infra tasks — only the test-gate and acceptance-criteria
requirements are waived, not code review. The requirements-checklist step
(step 7) still applies too, if the issue references a requirement.

### Docs / spec / requirements changes

Changes to `docs/`, `spec/`, `requirements/`, or `CLAUDE.md` themselves
don't fit backend/frontend/infra — they're not tied to any one area's
tooling. These use branch prefix `docs/<short-slug>` and skip the
issue/Project-board tracking used by the other three areas entirely; just
branch, commit, and open a PR.

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

- Custom Project fields beyond the default `Status` — intentionally
  deferred; keep setup minimal until a need arises.
- Required-status-checks branch protection for the backend/frontend CI
  workflows — enabled (a repo ruleset on `master` requires both
  `Build, lint, and test (backend)` and `Lint, test, build, and e2e
  (frontend)`). Both workflows trigger on every PR but skip their
  build/test steps unless the changed paths are relevant, specifically so
  this doesn't block PRs that don't touch backend/frontend code (e.g.
  docs-only) — see the change-detection step in each workflow file.
- Infra build-out (provisioning the Talos cluster, FluxCD bootstrap, etc.)
  — deferred until the greenhouse backend module is finished, or until
  explicitly requested; CI accordingly only covers backend and frontend
  for now.
