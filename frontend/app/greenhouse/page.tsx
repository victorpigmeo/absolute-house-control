import { ActuatorToggle } from "./_components/actuator-toggle";
import { LightCycleForm } from "./_components/light-cycle-form";
import { PumpForm } from "./_components/pump-form";
import { getGreenhouseState } from "./queries";

export default async function GreenhousePage() {
  const state = await getGreenhouseState();

  return (
    <main className="mx-auto flex max-w-md flex-col gap-6 p-8">
      <h1 className="text-2xl font-semibold">Greenhouse</h1>
      <ActuatorToggle
        path="/api/greenhouse/led"
        label="LED light"
        initialOn={state.led}
      />
      <ActuatorToggle
        path="/api/greenhouse/fan"
        label="Fan"
        initialOn={state.fan}
      />
      <PumpForm />
      <LightCycleForm />
    </main>
  );
}
