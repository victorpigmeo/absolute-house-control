import { createServer } from "node:http";

const PORT = 4010;
const calls = { led: 0, fan: 0, pump: 0 };
const state = { led: false, fan: false };

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

    res.writeHead(404).end();
  } catch {
    sendJson(res, 400, { error: "Malformed request body" });
  }
});

server.listen(PORT, () => {
  console.log(`Mock greenhouse server listening on :${PORT}`);
});
