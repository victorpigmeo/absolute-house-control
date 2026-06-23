import { test, expect } from "@playwright/test";

test("home page renders the bootstrap button", async ({ page }) => {
  await page.goto("/");
  await expect(
    page.getByRole("heading", { name: "Absolute House Control" }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "shadcn/ui Button" }),
  ).toBeVisible();
});
