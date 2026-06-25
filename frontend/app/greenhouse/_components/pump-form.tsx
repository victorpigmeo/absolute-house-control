"use client";

import { useActionState, useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { runPumpAction, type RunPumpFormState } from "../actions";

const initialState: RunPumpFormState = {};

export function PumpForm() {
  const [state, formAction, isPending] = useActionState(
    runPumpAction,
    initialState,
  );
  const [clearedState, setClearedState] = useState<RunPumpFormState | null>(
    null,
  );
  const running = state.durationSeconds != null && state !== clearedState;

  useEffect(() => {
    if (state.durationSeconds == null) {
      return;
    }
    const timer = setTimeout(() => {
      setClearedState(state);
    }, state.durationSeconds * 1000);
    return () => clearTimeout(timer);
  }, [state]);

  return (
    <form action={formAction} noValidate className="flex flex-col gap-2">
      <div className="flex items-center justify-between gap-4">
        <label htmlFor="durationSeconds" className="font-medium">
          Water pump
        </label>
        <div className="flex items-center gap-2">
          <input
            id="durationSeconds"
            name="durationSeconds"
            type="number"
            min={1}
            placeholder="Seconds"
            className="border-input bg-background h-8 w-24 rounded-md border px-2 text-sm"
            disabled={isPending}
          />
          <Button type="submit" disabled={isPending}>
            Run
          </Button>
        </div>
      </div>
      {running && (
        <span className="text-muted-foreground text-sm">
          Running for {state.durationSeconds}s…
        </span>
      )}
      {state.error && (
        <span className="text-destructive text-sm">{state.error}</span>
      )}
    </form>
  );
}
