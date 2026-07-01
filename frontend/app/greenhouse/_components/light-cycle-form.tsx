"use client";

import { useState } from "react";
import { useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toString as cronToString } from "cronstrue";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { createLightCycleAction, type LightCycle } from "../actions";
import {
  lightCycleSchema,
  type LightCycleFormValues,
} from "./light-cycle-schema";

function cronPreview(expression: string): string | null {
  if (!expression.trim()) {
    return null;
  }
  try {
    return cronToString(expression, { throwExceptionOnParseError: true });
  } catch {
    return null;
  }
}

export function LightCycleForm() {
  const [cycles, setCycles] = useState<LightCycle[]>([]);
  const [formError, setFormError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    control,
    setError,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<LightCycleFormValues>({
    resolver: zodResolver(lightCycleSchema),
    defaultValues: { name: "", onCron: "", offCron: "" },
  });

  const onCronValue = useWatch({ control, name: "onCron" });
  const offCronValue = useWatch({ control, name: "offCron" });
  const onPreview = cronPreview(onCronValue);
  const offPreview = cronPreview(offCronValue);

  async function onSubmit(values: LightCycleFormValues) {
    setFormError(null);
    const result = await createLightCycleAction(values);
    if (result.fieldErrors) {
      for (const [field, message] of Object.entries(result.fieldErrors)) {
        setError(field as keyof LightCycleFormValues, { message });
      }
    }
    if (result.error) {
      setFormError(result.error);
    }
    if (result.lightCycle) {
      setCycles((prev) => [...prev, result.lightCycle as LightCycle]);
      reset();
    }
  }

  return (
    <div className="flex flex-col gap-4">
      <form
        onSubmit={handleSubmit(onSubmit)}
        noValidate
        className="flex flex-col gap-3"
      >
        <h2 className="font-medium">Light cycles</h2>
        <div className="flex flex-col gap-1">
          <Label htmlFor="name">Name</Label>
          <Input id="name" {...register("name")} disabled={isSubmitting} />
          {errors.name && (
            <span className="text-destructive text-sm">
              {errors.name.message}
            </span>
          )}
        </div>
        <div className="flex flex-col gap-1">
          <Label htmlFor="onCron">ON cron</Label>
          <Input
            id="onCron"
            placeholder="0 0 8 * * *"
            {...register("onCron")}
            disabled={isSubmitting}
          />
          {errors.onCron ? (
            <span className="text-destructive text-sm">
              {errors.onCron.message}
            </span>
          ) : (
            onPreview && (
              <span className="text-muted-foreground text-sm">
                {onPreview}
              </span>
            )
          )}
        </div>
        <div className="flex flex-col gap-1">
          <Label htmlFor="offCron">OFF cron</Label>
          <Input
            id="offCron"
            placeholder="0 0 20 * * *"
            {...register("offCron")}
            disabled={isSubmitting}
          />
          {errors.offCron ? (
            <span className="text-destructive text-sm">
              {errors.offCron.message}
            </span>
          ) : (
            offPreview && (
              <span className="text-muted-foreground text-sm">
                {offPreview}
              </span>
            )
          )}
        </div>
        {formError && (
          <span className="text-destructive text-sm">{formError}</span>
        )}
        <Button type="submit" disabled={isSubmitting}>
          Create light cycle
        </Button>
      </form>
      {cycles.length > 0 && (
        <ul className="flex flex-col gap-2">
          {cycles.map((cycle) => (
            <li key={cycle.id} className="rounded-md border p-2 text-sm">
              <span className="font-medium">{cycle.name}</span>
              <span className="text-muted-foreground">
                {" "}
                — ON {cycle.onCron} / OFF {cycle.offCron}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
