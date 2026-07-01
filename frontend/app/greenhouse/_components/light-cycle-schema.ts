import { z } from "zod";

const SIX_FIELD_CRON = /^\S+\s+\S+\s+\S+\s+\S+\s+\S+\s+\S+$/;

const cronField = z
  .string()
  .min(1, "Required")
  .regex(
    SIX_FIELD_CRON,
    "Must be a 6-field cron expression (second minute hour day-of-month month day-of-week)",
  );

export const lightCycleSchema = z.object({
  name: z.string().min(1, "Required"),
  onCron: cronField,
  offCron: cronField,
});

export type LightCycleFormValues = z.infer<typeof lightCycleSchema>;
