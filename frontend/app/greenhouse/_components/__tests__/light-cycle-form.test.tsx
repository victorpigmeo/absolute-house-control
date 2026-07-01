import { beforeEach, describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { LightCycleForm } from "../light-cycle-form";
import { createLightCycleAction } from "../../actions";

vi.mock("../../actions", () => ({
  createLightCycleAction: vi.fn(),
}));

function fillForm(name: string, onCron: string, offCron: string) {
  fireEvent.change(screen.getByLabelText("Name"), { target: { value: name } });
  fireEvent.change(screen.getByLabelText("ON cron"), {
    target: { value: onCron },
  });
  fireEvent.change(screen.getByLabelText("OFF cron"), {
    target: { value: offCron },
  });
}

describe("LightCycleForm", () => {
  beforeEach(() => {
    vi.mocked(createLightCycleAction).mockReset();
  });

  it("adds the created cycle to the list and clears the form on success", async () => {
    vi.mocked(createLightCycleAction).mockResolvedValue({
      lightCycle: {
        id: 1,
        name: "Veg",
        onCron: "0 0 8 * * *",
        offCron: "0 0 20 * * *",
        active: false,
      },
    });

    render(<LightCycleForm />);
    fillForm("Veg", "0 0 8 * * *", "0 0 20 * * *");
    screen.getByRole("button", { name: "Create light cycle" }).click();

    await waitFor(() => expect(screen.getByText("Veg")).toBeInTheDocument());
    expect(screen.getByLabelText("Name")).toHaveValue("");
  });

  it("shows a field error for an invalid cron and does not call the action", async () => {
    render(<LightCycleForm />);
    fillForm("Veg", "not a cron", "0 0 20 * * *");
    screen.getByRole("button", { name: "Create light cycle" }).click();

    await waitFor(() =>
      expect(
        screen.getByText(/Must be a 6-field cron expression/),
      ).toBeInTheDocument(),
    );
    expect(createLightCycleAction).not.toHaveBeenCalled();
  });

  it("shows a field error for an invalid OFF cron and does not call the action", async () => {
    render(<LightCycleForm />);
    fillForm("Veg", "0 0 8 * * *", "not a cron");
    screen.getByRole("button", { name: "Create light cycle" }).click();

    await waitFor(() =>
      expect(
        screen.getByText(/Must be a 6-field cron expression/),
      ).toBeInTheDocument(),
    );
    expect(createLightCycleAction).not.toHaveBeenCalled();
  });

  it("updates the live preview for the ON field while typing without submitting", async () => {
    render(<LightCycleForm />);
    fireEvent.change(screen.getByLabelText("ON cron"), {
      target: { value: "0 0 8 * * *" },
    });

    await waitFor(() =>
      expect(screen.getByText("At 08:00 AM")).toBeInTheDocument(),
    );
    expect(createLightCycleAction).not.toHaveBeenCalled();
  });

  it("updates the live preview for the OFF field while typing without submitting", async () => {
    render(<LightCycleForm />);
    fireEvent.change(screen.getByLabelText("OFF cron"), {
      target: { value: "0 0 20 * * *" },
    });

    await waitFor(() =>
      expect(screen.getByText("At 08:00 PM")).toBeInTheDocument(),
    );
    expect(createLightCycleAction).not.toHaveBeenCalled();
  });
});
