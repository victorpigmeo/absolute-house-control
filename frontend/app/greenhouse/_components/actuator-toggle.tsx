"use client";

import { useTransition, useState } from "react";
import { Switch } from "@/components/ui/switch";
import { setActuatorAction, type ActuatorPath } from "../actions";

export function ActuatorToggle({
  path,
  label,
  initialOn,
}: {
  path: ActuatorPath;
  label: string;
  initialOn: boolean;
}) {
  const [on, setOn] = useState(initialOn);
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
        {error && <span className="text-destructive text-sm">{error}</span>}
      </div>
      <Switch
        checked={on}
        onCheckedChange={handleChange}
        disabled={isPending}
        aria-label={label}
      />
    </div>
  );
}
