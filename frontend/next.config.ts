import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // "localhost" is allowed by default, but the dev server otherwise rejects
  // asset requests whose Origin doesn't match the request host, so browsing
  // via 127.0.0.1 (or a devcontainer's forwarded IP) 403s every JS chunk.
  allowedDevOrigins: ["127.0.0.1"],
};

export default nextConfig;
