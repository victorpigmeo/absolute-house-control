import createClient from "openapi-fetch";
import type { paths } from "./schema";

const client = createClient<paths>({
  baseUrl: process.env.GREENHOUSE_SERVICE_URL ?? "http://localhost:8080",
});

export default client;
