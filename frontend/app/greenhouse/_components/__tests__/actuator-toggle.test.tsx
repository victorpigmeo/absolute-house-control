import { describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import { ActuatorToggle } from "../actuator-toggle";
import { setActuatorAction } from "../../actions";

vi.mock("../../actions", () => ({
  setActuatorAction: vi.fn(),
}));

describe("ActuatorToggle", () => {
  it("shows state as unknown until the first toggle", () => {
    render(<ActuatorToggle path="/api/greenhouse/led" label="LED light" />);

    expect(screen.getByText("State unknown until toggled")).toBeInTheDocument();
    expect(screen.getByRole("switch", { name: "LED light" })).not.toBeChecked();
  });

  it("reflects the confirmed state after toggling on", async () => {
    vi.mocked(setActuatorAction).mockResolvedValue({ on: true });

    render(<ActuatorToggle path="/api/greenhouse/led" label="LED light" />);
    screen.getByRole("switch", { name: "LED light" }).click();

    await waitFor(() =>
      expect(screen.getByRole("switch", { name: "LED light" })).toBeChecked(),
    );
    expect(setActuatorAction).toHaveBeenCalledWith("/api/greenhouse/led", true);
  });

  it("shows an inline error when the backend call fails", async () => {
    vi.mocked(setActuatorAction).mockResolvedValue({
      error: "device unreachable",
    });

    render(<ActuatorToggle path="/api/greenhouse/fan" label="Fan" />);
    screen.getByRole("switch", { name: "Fan" }).click();

    await waitFor(() =>
      expect(screen.getByText("device unreachable")).toBeInTheDocument(),
    );
  });
});
