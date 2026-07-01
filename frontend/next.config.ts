import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // "localhost" is allowed by default, but the dev server otherwise rejects
  // asset requests whose Origin doesn't match the request host, so browsing
  // via 127.0.0.1 (or a devcontainer's forwarded IP) 403s every JS chunk.
  allowedDevOrigins: ["127.0.0.1"],

  async headers() {
    if (process.env.NODE_ENV === "production") {
      return [];
    }
    // Dev-only: after a `next dev` (Turbopack) restart, an already-open tab
    // can get a stale chunk body via conditional (304) revalidation even on
    // a hard refresh — see frontend-spec.md's known-issues section. Forcing
    // no-store means the browser never has anything to conditionally
    // revalidate in the first place. Prod builds are content-hashed and
    // don't need this.
    return [
      {
        source: "/_next/static/:path*",
        headers: [{ key: "Cache-Control", value: "no-store, must-revalidate" }],
      },
    ];
  },
};

export default nextConfig;
