import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import { ActuatorToggle } from "../actuator-toggle";
import { setActuatorAction } from "../../actions";

vi.mock("../../actions", () => ({
  setActuatorAction: vi.fn(),
}));

describe("ActuatorToggle", () => {
  beforeEach(() => {
    vi.mocked(setActuatorAction).mockReset();
  });

  it("renders checked when the initial state is on", () => {
    render(
      <ActuatorToggle
        path="/api/greenhouse/led"
        label="LED light"
        initialOn={true}
      />,
    );

    expect(screen.getByRole("switch", { name: "LED light" })).toBeChecked();
  });

  it("renders unchecked when the initial state is off", () => {
    render(
      <ActuatorToggle
        path="/api/greenhouse/led"
        label="LED light"
        initialOn={false}
      />,
    );

    expect(
      screen.getByRole("switch", { name: "LED light" }),
    ).not.toBeChecked();
  });

  it("reflects the confirmed state after toggling on", async () => {
    vi.mocked(setActuatorAction).mockResolvedValue({ on: true });

    render(
      <ActuatorToggle
        path="/api/greenhouse/led"
        label="LED light"
        initialOn={false}
      />,
    );
    screen.getByRole("switch", { name: "LED light" }).click();

    await waitFor(() =>
      expect(screen.getByRole("switch", { name: "LED light" })).toBeChecked(),
    );
    expect(setActuatorAction).toHaveBeenCalledWith("/api/greenhouse/led", true);
  });

  it("cycles the switch through on and off across repeated clicks", async () => {
    vi.mocked(setActuatorAction)
      .mockResolvedValueOnce({ on: true })
      .mockResolvedValueOnce({ on: false })
      .mockResolvedValueOnce({ on: true });

    render(
      <ActuatorToggle
        path="/api/greenhouse/led"
        label="LED light"
        initialOn={false}
      />,
    );
    const ledSwitch = screen.getByRole("switch", { name: "LED light" });

    ledSwitch.click();
    await waitFor(() => {
      expect(ledSwitch).toBeChecked();
      expect(ledSwitch).not.toBeDisabled();
    });
    expect(setActuatorAction).toHaveBeenNthCalledWith(
      1,
      "/api/greenhouse/led",
      true,
    );

    ledSwitch.click();
    await waitFor(() => {
      expect(ledSwitch).not.toBeChecked();
      expect(ledSwitch).not.toBeDisabled();
    });
    expect(setActuatorAction).toHaveBeenNthCalledWith(
      2,
      "/api/greenhouse/led",
      false,
    );

    ledSwitch.click();
    await waitFor(() => {
      expect(ledSwitch).toBeChecked();
      expect(ledSwitch).not.toBeDisabled();
    });
    expect(setActuatorAction).toHaveBeenNthCalledWith(
      3,
      "/api/greenhouse/led",
      true,
    );
  });

  it("shows an inline error when the backend call fails", async () => {
    vi.mocked(setActuatorAction).mockResolvedValue({
      error: "device unreachable",
    });

    render(
      <ActuatorToggle path="/api/greenhouse/fan" label="Fan" initialOn={false} />,
    );
    screen.getByRole("switch", { name: "Fan" }).click();

    await waitFor(() =>
      expect(screen.getByText("device unreachable")).toBeInTheDocument(),
    );
  });
});
