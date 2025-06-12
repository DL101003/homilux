import { ModeToggle } from "@/components/model-toggle";
import { Button } from "@/components/ui/button";

export default function Home() {
  return (
    <div>
      <ModeToggle />
      <h1 className="text-2xl font-bold">Welcome to the Next.js App!</h1>
      <p className="mt-4">This is a simple example of a Next.js application with a theme toggle.</p>
      <Button className="mt-4" variant="default">
        Click Me
      </Button>
    </div>
  )
}