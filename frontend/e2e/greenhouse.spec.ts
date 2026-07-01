import { test, expect } from "@playwright/test";

async function callCounts(request: import("@playwright/test").APIRequestContext) {
  const response = await request.get("http://localhost:4010/__calls");
  return response.json();
}

async function seedState(
  request: import("@playwright/test").APIRequestContext,
  state: { led?: boolean; fan?: boolean },
) {
  await request.post("http://localhost:4010/__state", { data: state });
}

// Serialized: tests share the mock server's call counters, and the
// invalid-duration test asserts on a before/after delta for one of them.
test.describe.serial("greenhouse controls", () => {
  test("dashboard reflects state persisted from outside the current session", async ({
    page,
    request,
  }) => {
    await seedState(request, { led: true, fan: true });
    await page.goto("/greenhouse");

    await expect(page.getByRole("switch", { name: "LED light" })).toBeChecked();
    await expect(page.getByRole("switch", { name: "Fan" })).toBeChecked();

    // Reset so the following tests' toggle-click sequences start from off.
    await seedState(request, { led: false, fan: false });
  });

  test("toggling the LED reflects the confirmed on/off state", async ({
    page,
  }) => {
    await page.goto("/greenhouse");
    const ledSwitch = page.getByRole("switch", { name: "LED light" });

    await ledSwitch.click();
    await expect(ledSwitch).toBeChecked();

    await ledSwitch.click();
    await expect(ledSwitch).not.toBeChecked();

    await ledSwitch.click();
    await expect(ledSwitch).toBeChecked();
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

test.describe.serial("light cycle creation", () => {
  test("a valid submission creates a light cycle and it appears in the list", async ({
    page,
  }) => {
    await page.goto("/greenhouse");

    await page.getByLabel("Name").fill("Veg");
    await page.getByLabel("ON cron").fill("0 0 8 * * *");
    await page.getByLabel("OFF cron").fill("0 0 20 * * *");
    await page.getByRole("button", { name: "Create light cycle" }).click();

    await expect(page.getByText("Veg")).toBeVisible();
  });

  test("an invalid ON cron shows a field error and never calls the backend", async ({
    page,
    request,
  }) => {
    await page.goto("/greenhouse");
    const before = await callCounts(request);

    await page.getByLabel("Name").fill("Flora");
    await page.getByLabel("ON cron").fill("not a cron");
    await page.getByLabel("OFF cron").fill("0 0 20 * * *");
    await page.getByRole("button", { name: "Create light cycle" }).click();

    await expect(
      page.getByText(/Must be a 6-field cron expression/),
    ).toBeVisible();
    const after = await callCounts(request);
    expect(after.lightCycles).toBe(before.lightCycles);
  });

  test("an invalid OFF cron shows a field error and never calls the backend", async ({
    page,
    request,
  }) => {
    await page.goto("/greenhouse");
    const before = await callCounts(request);

    await page.getByLabel("Name").fill("Flora");
    await page.getByLabel("ON cron").fill("0 0 8 * * *");
    await page.getByLabel("OFF cron").fill("not a cron");
    await page.getByRole("button", { name: "Create light cycle" }).click();

    await expect(
      page.getByText(/Must be a 6-field cron expression/),
    ).toBeVisible();
    const after = await callCounts(request);
    expect(after.lightCycles).toBe(before.lightCycles);
  });

  test("typing a valid ON cron updates the live preview without submitting", async ({
    page,
    request,
  }) => {
    await page.goto("/greenhouse");
    const before = await callCounts(request);

    await page.getByLabel("ON cron").fill("0 0 8 * * *");

    await expect(page.getByText("At 08:00 AM")).toBeVisible();
    const after = await callCounts(request);
    expect(after.lightCycles).toBe(before.lightCycles);
  });

  test("typing a valid OFF cron updates the live preview without submitting", async ({
    page,
    request,
  }) => {
    await page.goto("/greenhouse");
    const before = await callCounts(request);

    await page.getByLabel("OFF cron").fill("0 0 20 * * *");

    await expect(page.getByText("At 08:00 PM")).toBeVisible();
    const after = await callCounts(request);
    expect(after.lightCycles).toBe(before.lightCycles);
  });
});
