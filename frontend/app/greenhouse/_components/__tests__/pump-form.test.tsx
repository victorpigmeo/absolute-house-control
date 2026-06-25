import { describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { PumpForm } from "../pump-form";
import { runPumpAction } from "../../actions";

vi.mock("../../actions", () => ({
  runPumpAction: vi.fn(),
}));

describe("PumpForm", () => {
  it("shows a running indicator after a successful submission", async () => {
    vi.mocked(runPumpAction).mockResolvedValue({ durationSeconds: 5 });

    render(<PumpForm />);
    fireEvent.change(screen.getByRole("spinbutton"), {
      target: { value: "5" },
    });
    screen.getByRole("button", { name: "Run" }).click();

    await waitFor(() =>
      expect(screen.getByText(/Running for 5s/)).toBeInTheDocument(),
    );
  });

  it("shows an inline error returned by the action", async () => {
    vi.mocked(runPumpAction).mockResolvedValue({
      error: "Duration must be a positive number.",
    });

    render(<PumpForm />);
    screen.getByRole("button", { name: "Run" }).click();

    await waitFor(() =>
      expect(
        screen.getByText("Duration must be a positive number."),
      ).toBeInTheDocument(),
    );
  });
});
