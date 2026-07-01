import { describe, expect, it, vi, beforeEach } from "vitest";
import { getGreenhouseState } from "../queries";
import client from "@/lib/greenhouse/client";

vi.mock("@/lib/greenhouse/client", () => ({
  default: { GET: vi.fn() },
}));

describe("getGreenhouseState", () => {
  beforeEach(() => {
    vi.mocked(client.GET).mockReset();
  });

  it("returns the persisted LED/fan state from the backend", async () => {
    vi.mocked(client.GET).mockResolvedValue({
      data: { led: true, fan: false },
      error: undefined,
      response: new Response(),
    } as never);

    const result = await getGreenhouseState();

    expect(client.GET).toHaveBeenCalledWith("/api/greenhouse/state", {
      cache: "no-store",
    });
    expect(result).toEqual({ led: true, fan: false });
  });

  it("throws when the backend returns an error", async () => {
    vi.mocked(client.GET).mockResolvedValue({
      data: undefined,
      error: { error: "device unreachable" },
      response: new Response(),
    } as never);

    await expect(getGreenhouseState()).rejects.toThrow(
      "Failed to load device state.",
    );
  });

  it("throws a friendly message on a network failure", async () => {
    vi.mocked(client.GET).mockRejectedValue(new Error("fetch failed"));

    await expect(getGreenhouseState()).rejects.toThrow(
      "Failed to load device state.",
    );
  });
});
