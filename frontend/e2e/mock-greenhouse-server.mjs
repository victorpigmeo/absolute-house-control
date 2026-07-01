import { createServer } from "node:http";

const PORT = 4010;
const calls = { led: 0, fan: 0, pump: 0, lightCycles: 0 };
const state = { led: false, fan: false };
const SIX_FIELD_CRON = /^\S+\s+\S+\s+\S+\s+\S+\s+\S+\s+\S+$/;
let nextLightCycleId = 1;

function readJsonBody(req) {
  return new Promise((resolve, reject) => {
    let body = "";
    req.on("data", (chunk) => (body += chunk));
    req.on("end", () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch (err) {
        reject(err);
      }
    });
  });
}

function sendJson(res, status, payload) {
  res.writeHead(status, { "Content-Type": "application/json" });
  res.end(JSON.stringify(payload));
}

const server = createServer(async (req, res) => {
  try {
    if (req.method === "GET" && req.url === "/__calls") {
      sendJson(res, 200, calls);
      return;
    }

    if (req.method === "POST" && req.url === "/__state") {
      const body = await readJsonBody(req);
      Object.assign(state, body);
      sendJson(res, 200, state);
      return;
    }

    if (req.method === "GET" && req.url === "/api/greenhouse/state") {
      sendJson(res, 200, state);
      return;
    }

    if (req.method === "POST" && req.url === "/api/greenhouse/led") {
      calls.led++;
      const { on } = await readJsonBody(req);
      state.led = on;
      sendJson(res, 200, { on });
      return;
    }

    if (req.method === "POST" && req.url === "/api/greenhouse/fan") {
      calls.fan++;
      const { on } = await readJsonBody(req);
      state.fan = on;
      sendJson(res, 200, { on });
      return;
    }

    if (req.method === "POST" && req.url === "/api/greenhouse/pump") {
      calls.pump++;
      const { durationSeconds } = await readJsonBody(req);
      sendJson(res, 200, { durationSeconds });
      return;
    }

    if (req.method === "POST" && req.url === "/api/greenhouse/light-cycles") {
      calls.lightCycles++;
      const body = await readJsonBody(req);
      const details = [];
      if (!body.name || !body.name.trim()) {
        details.push("name: must not be blank");
      }
      if (!body.onCron || !SIX_FIELD_CRON.test(body.onCron)) {
        details.push("onCron: must be a valid 6-field cron expression");
      }
      if (!body.offCron || !SIX_FIELD_CRON.test(body.offCron)) {
        details.push("offCron: must be a valid 6-field cron expression");
      }
      if (details.length > 0) {
        sendJson(res, 400, { error: "Validation failed", details });
        return;
      }
      sendJson(res, 200, {
        id: nextLightCycleId++,
        name: body.name,
        onCron: body.onCron,
        offCron: body.offCron,
        active: false,
      });
      return;
    }

    res.writeHead(404).end();
  } catch {
    sendJson(res, 400, { error: "Malformed request body" });
  }
});

server.listen(PORT, () => {
  console.log(`Mock greenhouse server listening on :${PORT}`);
});
