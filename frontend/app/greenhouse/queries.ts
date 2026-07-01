import { unstable_rethrow } from "next/navigation";
import client from "@/lib/greenhouse/client";

export type GreenhouseState = { led: boolean; fan: boolean };

export async function getGreenhouseState(): Promise<GreenhouseState> {
  try {
    const { data, error } = await client.GET("/api/greenhouse/state", {
      cache: "no-store",
    });
    if (error || !data) {
      throw new Error("Failed to load device state.");
    }
    return { led: data.led ?? false, fan: data.fan ?? false };
  } catch (err) {
    unstable_rethrow(err);
    throw new Error("Failed to load device state.");
  }
}
