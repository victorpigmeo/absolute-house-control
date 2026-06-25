"use server";

import client from "@/lib/greenhouse/client";

type RequestResult<T> = { data?: T; error?: string };

async function safeRequest<T>(
  call: () => Promise<{ data?: T; error?: { error?: string } }>,
  fallbackMessage: string,
): Promise<RequestResult<T>> {
  try {
    const { data, error } = await call();
    if (error) {
      return { error: error.error ?? fallbackMessage };
    }
    if (!data) {
      return { error: fallbackMessage };
    }
    return { data };
  } catch {
    return { error: fallbackMessage };
  }
}

export type ActuatorPath = "/api/greenhouse/led" | "/api/greenhouse/fan";

export type ActuatorResult = { on?: boolean; error?: string };

export async function setActuatorAction(
  path: ActuatorPath,
  on: boolean,
): Promise<ActuatorResult> {
  const { data, error } = await safeRequest(
    () => client.POST(path, { body: { on } }),
    "Failed to update device state.",
  );
  if (error) {
    return { error };
  }
  return { on: data?.on ?? on };
}

export type RunPumpFormState = {
  durationSeconds?: number;
  error?: string;
};

export async function runPumpAction(
  _prevState: RunPumpFormState,
  formData: FormData,
): Promise<RunPumpFormState> {
  const raw = formData.get("durationSeconds");
  if (raw === null || raw === "") {
    return { error: "Duration is required." };
  }

  const durationSeconds = Number(raw);
  if (Number.isNaN(durationSeconds) || !Number.isInteger(durationSeconds)) {
    return { error: "Duration must be a whole number of seconds." };
  }
  if (durationSeconds <= 0) {
    return { error: "Duration must be a positive number." };
  }

  const { data, error } = await safeRequest(
    () => client.POST("/api/greenhouse/pump", { body: { durationSeconds } }),
    "Failed to start the pump.",
  );
  if (error) {
    return { error };
  }
  return { durationSeconds: data?.durationSeconds ?? durationSeconds };
}
