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

export type LightCycle = {
  id: number;
  name: string;
  onCron: string;
  offCron: string;
  active: boolean;
};

export type LightCycleFieldErrors = Partial<
  Record<"name" | "onCron" | "offCron", string>
>;

export type CreateLightCycleResult = {
  lightCycle?: LightCycle;
  error?: string;
  fieldErrors?: LightCycleFieldErrors;
};

function parseLightCycleFieldErrors(details: string[] | undefined): {
  fieldErrors?: LightCycleFieldErrors;
  unmatched: string[];
} {
  const fieldErrors: LightCycleFieldErrors = {};
  const unmatched: string[] = [];
  for (const detail of details ?? []) {
    const separatorIndex = detail.indexOf(": ");
    const field = separatorIndex === -1 ? undefined : detail.slice(0, separatorIndex);
    if (field === "name" || field === "onCron" || field === "offCron") {
      fieldErrors[field] = detail.slice(separatorIndex + 2);
    } else {
      unmatched.push(detail);
    }
  }
  return {
    fieldErrors: Object.keys(fieldErrors).length > 0 ? fieldErrors : undefined,
    unmatched,
  };
}

export async function createLightCycleAction(values: {
  name: string;
  onCron: string;
  offCron: string;
}): Promise<CreateLightCycleResult> {
  const fallbackMessage = "Failed to create the light cycle.";
  try {
    const { data, error } = await client.POST("/api/greenhouse/light-cycles", {
      body: values,
    });
    if (error) {
      const { fieldErrors, unmatched } = parseLightCycleFieldErrors(
        error.details,
      );
      if (fieldErrors || unmatched.length > 0) {
        return {
          fieldErrors,
          error: unmatched.length > 0 ? unmatched.join(" ") : undefined,
        };
      }
      return { error: error.error ?? fallbackMessage };
    }
    if (!data || data.id === undefined) {
      return { error: fallbackMessage };
    }
    return {
      lightCycle: {
        id: data.id,
        name: data.name ?? values.name,
        onCron: data.onCron ?? values.onCron,
        offCron: data.offCron ?? values.offCron,
        active: data.active ?? false,
      },
    };
  } catch {
    return { error: fallbackMessage };
  }
}
