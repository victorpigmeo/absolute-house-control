"use client";

import { Button } from "@/components/ui/button";

export default function GreenhouseError({
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <main className="mx-auto flex max-w-md flex-col items-start gap-4 p-8">
      <h1 className="text-2xl font-semibold">Greenhouse</h1>
      <p className="text-destructive text-sm">
        Couldn&apos;t load the greenhouse dashboard.
      </p>
      <Button onClick={reset}>Retry</Button>
    </main>
  );
}
