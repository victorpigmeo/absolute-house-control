import { describe, expect, it, vi, beforeEach } from "vitest";
import { runPumpAction, setActuatorAction } from "../actions";
import client from "@/lib/greenhouse/client";

vi.mock("@/lib/greenhouse/client", () => ({
  default: { POST: vi.fn() },
}));

function formDataWith(durationSeconds: string | undefined) {
  const formData = new FormData();
  if (durationSeconds !== undefined) {
    formData.set("durationSeconds", durationSeconds);
  }
  return formData;
}

describe("runPumpAction", () => {
  beforeEach(() => {
    vi.mocked(client.POST).mockReset();
  });

  it("rejects a missing duration without calling the backend", async () => {
    const result = await runPumpAction({}, formDataWith(undefined));

    expect(result.error).toBeTruthy();
    expect(client.POST).not.toHaveBeenCalled();
  });

  it("rejects a non-numeric duration without calling the backend", async () => {
    const result = await runPumpAction({}, formDataWith("thirty"));

    expect(result.error).toBeTruthy();
    expect(client.POST).not.toHaveBeenCalled();
  });

  it("rejects a non-positive duration without calling the backend", async () => {
    const result = await runPumpAction({}, formDataWith("0"));

    expect(result.error).toBeTruthy();
    expect(client.POST).not.toHaveBeenCalled();
  });

  it("calls the backend with a valid duration and returns it", async () => {
    vi.mocked(client.POST).mockResolvedValue({
      data: { durationSeconds: 5 },
      error: undefined,
      response: new Response(),
    } as never);

    const result = await runPumpAction({}, formDataWith("5"));

    expect(client.POST).toHaveBeenCalledWith("/api/greenhouse/pump", {
      body: { durationSeconds: 5 },
    });
    expect(result.durationSeconds).toBe(5);
  });

  it("relays the backend's error message when the request fails", async () => {
    vi.mocked(client.POST).mockResolvedValue({
      data: undefined,
      error: { error: "durationSeconds must not exceed 10" },
      response: new Response(),
    } as never);

    const result = await runPumpAction({}, formDataWith("30"));

    expect(result.error).toBe("durationSeconds must not exceed 10");
  });

  it("falls back to a generic error on a network failure", async () => {
    vi.mocked(client.POST).mockRejectedValue(new Error("fetch failed"));

    const result = await runPumpAction({}, formDataWith("5"));

    expect(result.error).toBeTruthy();
  });

  it("falls back to a generic error on a non-2xx response with no body", async () => {
    vi.mocked(client.POST).mockResolvedValue({
      data: undefined,
      error: undefined,
      response: new Response(null, { status: 502 }),
    } as never);

    const result = await runPumpAction({}, formDataWith("5"));

    expect(result.error).toBeTruthy();
  });
});

describe("setActuatorAction", () => {
  beforeEach(() => {
    vi.mocked(client.POST).mockReset();
  });

  it("returns the confirmed state from the backend", async () => {
    vi.mocked(client.POST).mockResolvedValue({
      data: { on: true },
      error: undefined,
      response: new Response(),
    } as never);

    const result = await setActuatorAction("/api/greenhouse/led", true);

    expect(client.POST).toHaveBeenCalledWith("/api/greenhouse/led", {
      body: { on: true },
    });
    expect(result.on).toBe(true);
  });

  it("relays an error from the backend", async () => {
    vi.mocked(client.POST).mockResolvedValue({
      data: undefined,
      error: { error: "device unreachable" },
      response: new Response(),
    } as never);

    const result = await setActuatorAction("/api/greenhouse/fan", true);

    expect(result.error).toBe("device unreachable");
  });

  it("falls back to a generic error on a network failure", async () => {
    vi.mocked(client.POST).mockRejectedValue(new Error("fetch failed"));

    const result = await setActuatorAction("/api/greenhouse/led", true);

    expect(result.error).toBeTruthy();
  });

  it("falls back to a generic error on a non-2xx response with no body", async () => {
    vi.mocked(client.POST).mockResolvedValue({
      data: undefined,
      error: undefined,
      response: new Response(null, { status: 503 }),
    } as never);

    const result = await setActuatorAction("/api/greenhouse/led", true);

    expect(result.error).toBeTruthy();
  });
});
