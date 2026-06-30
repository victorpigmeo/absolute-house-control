import { ActuatorToggle } from "./_components/actuator-toggle";
import { PumpForm } from "./_components/pump-form";

export default function GreenhousePage() {
  return (
    <main className="mx-auto flex max-w-md flex-col gap-6 p-8">
      <h1 className="text-2xl font-semibold">Greenhouse</h1>
      <ActuatorToggle path="/api/greenhouse/led" label="LED light" />
      <ActuatorToggle path="/api/greenhouse/fan" label="Fan" />
      <PumpForm />
    </main>
  );
}
