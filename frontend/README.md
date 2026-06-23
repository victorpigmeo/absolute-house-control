# Frontend — absolute-house-control

## Prerequisites

- Node.js 24.x (see `.nvmrc`). `npm install` will warn/error if your Node
  version doesn't satisfy the `engines` field in `package.json`.
- Network access on first run: `npm install` (npm registry) and
  `npx playwright install chromium` (Playwright's CDN) both download
  packages/binaries; offline environments will need these cached/pre-fetched.

## Install

```
npm install
npx playwright install chromium
```

The Playwright browser binary install is a separate step from `npm install`
(Playwright ships the test runner as an npm package but browser binaries are
downloaded independently, cached under `~/.cache/ms-playwright`).

## Building and testing

- `npm run dev` — starts the Next.js dev server on `localhost:3000`.
- `npm run build` — production build (also runs the TypeScript compiler).
- `npm run lint` — ESLint, using Next.js's default config (`eslint-config-next`).
  See [docs/development-process.md](../docs/development-process.md) for
  the current status of further lint tooling decisions.
- `npm test` — Vitest unit/component tests (one-shot `vitest run`, CI-safe).
  Use `npm run test:watch` for interactive watch mode during development.
- `npm run test:e2e` — Playwright end-to-end tests. Auto-starts the dev
  server (`npm run dev`) if one isn't already running on `localhost:3000`
  per `playwright.config.ts`'s `webServer` block.

## Tech stack

See [spec/frontend-spec.md](../spec/frontend-spec.md) for the full stack
and rationale.

This is a bootstrap only — no application features yet. One sample
shadcn/ui component (`Button`) is wired into the home page (`app/page.tsx`)
purely to prove the scaffolding (Tailwind, shadcn, the import alias) works
end-to-end.
