"use client";

import { useTransition, useState } from "react";
import { Switch } from "@/components/ui/switch";
import { setActuatorAction, type ActuatorPath } from "../actions";

export function ActuatorToggle({
  path,
  label,
}: {
  path: ActuatorPath;
  label: string;
}) {
  const [on, setOn] = useState<boolean | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isPending, startTransition] = useTransition();

  function handleChange(checked: boolean) {
    setError(null);
    startTransition(async () => {
      const result = await setActuatorAction(path, checked);
      if (result.error) {
        setError(result.error);
      } else if (result.on !== undefined) {
        setOn(result.on);
      }
    });
  }

  return (
    <div className="flex items-center justify-between gap-4">
      <div className="flex flex-col">
        <span className="font-medium">{label}</span>
        {on === null && !error && (
          <span className="text-muted-foreground text-sm">
            State unknown until toggled
          </span>
        )}
        {error && <span className="text-destructive text-sm">{error}</span>}
      </div>
      <Switch
        checked={on ?? false}
        onCheckedChange={handleChange}
        disabled={isPending}
        aria-label={label}
      />
    </div>
  );
}
