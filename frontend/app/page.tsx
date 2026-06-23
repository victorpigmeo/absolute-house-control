import { Button } from "@/components/ui/button";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-4">
      <h1 className="text-2xl font-semibold">Absolute House Control</h1>
      <p className="text-muted-foreground">Frontend bootstrap is wired up.</p>
      <Button>shadcn/ui Button</Button>
    </main>
  );
}
