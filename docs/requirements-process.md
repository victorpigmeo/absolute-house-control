# Requirements Process

## Overview

Requirements capture *what* to build and *why*, independently of the
backend/frontend/infra implementation tasks described in
[development-process.md](development-process.md). A requirement can span
more than one area (e.g. a feature with both a backend service change and a
frontend UI change) — it is not filed per-area the way GitHub issues are.

Requirements live in [requirements/](../requirements/), one folder per
requirement. Start a new one by copying
[requirements/_template/](../requirements/_template/).

## Folder and file layout

```
requirements/
  <goal-slug>/
    README.md            # the requirement: goal, description, functional
                          # requirements, acceptance criteria, user story links
    us-1-<slug>.md        # user story 1
    us-2-<slug>.md        # user story 2
    ...
```

## Naming

- **`<goal-slug>`** (folder name) — the goal, slugified: lowercase, spaces
  and punctuation become hyphens, truncated to **30 characters at a word
  boundary** (never mid-word), no leading/trailing hyphen.
  Example: goal "Allow homeowners to remotely view greenhouse temperature"
  → folder `view-greenhouse-temperature`.
- **User story files** — `us-<n>-<short-slug>.md`, numbered in the order
  they're added within the requirement (`us-1-...`, `us-2-...`).

## Requirement main file (`README.md`)

Required sections, in order:

1. **Goal** — one sentence summary of the feature.
2. **Description** — optional, up to 500 characters. Delete the section
   entirely if not needed.
3. **Functional Requirements** — numbered list of what the system must do.
4. **Acceptance Criteria** — checklist; see [Keep acceptance criteria
   measurable](#keep-acceptance-criteria-measurable) below.
5. **User Stories** — a link to each `us-*.md` file in the folder.

## User story file

Each user story file has two parts:

1. The narrative, who/what/why, in Connextra form:
   **As a** \<role\>, **I want** \<capability\>, **so that** \<benefit\>.
2. One or more **Scenario** blocks, each a Given/When/Then triplet
   describing a concrete, testable behavior.

See [requirements/_template/us-1-template.md](../requirements/_template/us-1-template.md).

## Keep acceptance criteria measurable

Every acceptance criterion (at the requirement level) and every
Given/When/Then scenario (at the user story level) must resolve to an
unambiguous pass/fail check.

- Bad: "The dashboard performs well."
- Good: "The dashboard displays the current temperature within 2 seconds
  of page load."

## Relationship to implementation tasks

A requirement or user story is not itself a GitHub issue and doesn't move
through a Project board. When implementation starts, file the normal
backend/frontend/infra task per
[development-process.md](development-process.md) and reference the
requirement/user story path in the issue's Description, e.g.:

```
Implements requirements/view-greenhouse-temperature/us-1-show-current-reading.md
```

When a requirement spans more than one area, list each per-area issue
filed against it in the requirement's `README.md` as they're created (a
simple checklist is enough) so it's visible in one place whether every
area's work is done — there's no separate tracking issue.

## Handling `<goal-slug>` collisions

If a new requirement's slugified goal would collide with an existing
`requirements/` folder name, stop and ask before creating it rather than
silently disambiguating (e.g. appending a number).
