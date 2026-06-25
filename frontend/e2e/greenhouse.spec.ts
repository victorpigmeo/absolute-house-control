import { test, expect } from "@playwright/test";

async function callCounts(request: import("@playwright/test").APIRequestContext) {
  const response = await request.get("http://localhost:4010/__calls");
  return response.json();
}

// Serialized: tests share the mock server's call counters, and the
// invalid-duration test asserts on a before/after delta for one of them.
test.describe.serial("greenhouse controls", () => {
  test("toggling the LED reflects the confirmed on/off state", async ({
    page,
  }) => {
    await page.goto("/greenhouse");
    const ledSwitch = page.getByRole("switch", { name: "LED light" });

    await expect(
      page.getByText("State unknown until toggled").first(),
    ).toBeVisible();

    await ledSwitch.click();
    await expect(ledSwitch).toBeChecked();

    await ledSwitch.click();
    await expect(ledSwitch).not.toBeChecked();
  });

  test("toggling the fan reflects the confirmed on/off state", async ({
    page,
  }) => {
    await page.goto("/greenhouse");
    const fanSwitch = page.getByRole("switch", { name: "Fan" });

    await fanSwitch.click();
    await expect(fanSwitch).toBeChecked();
  });

  test("a valid pump duration shows a running indicator", async ({ page }) => {
    await page.goto("/greenhouse");

    await page.getByLabel("Water pump").fill("3");
    await page.getByRole("button", { name: "Run" }).click();

    await expect(page.getByText(/Running for 3s/)).toBeVisible();
  });

  test("an invalid pump duration shows an inline error and never calls the backend", async ({
    page,
    request,
  }) => {
    await page.goto("/greenhouse");
    const before = await callCounts(request);

    await page.getByLabel("Water pump").fill("-5");
    await page.getByRole("button", { name: "Run" }).click();

    await expect(
      page.getByText("Duration must be a positive number."),
    ).toBeVisible();
    const after = await callCounts(request);
    expect(after.pump).toBe(before.pump);
  });
});
