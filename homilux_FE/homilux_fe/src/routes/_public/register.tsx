// src/routes/_public/register.tsx
import { createFileRoute } from '@tanstack/react-router';
import { RegisterForm } from '@/features/auth/components/RegisterForm';
import { Link } from '@tanstack/react-router';

export const Route = createFileRoute('/_public/register')({
  component: RegisterPage,
});

function RegisterPage() {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="w-full max-w-md space-y-8 px-4">
        <div className="text-center">
          <h2 className="text-3xl font-bold">Create new account</h2>
          <p className="mt-2 text-sm text-muted-foreground">
            Already have an account?{' '}
            <Link to="/login" className="text-primary hover:underline">
              Sign in
            </Link>
          </p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
}