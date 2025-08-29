// src/routes/_authenticated.tsx (Update lại)
import { createFileRoute, Outlet, redirect } from '@tanstack/react-router';
import { useAuthStore } from '@/features/auth/stores/auth.store';

export const Route = createFileRoute('/_authenticated')({
  beforeLoad: async ({ location }) => {
    const isAuthenticated = useAuthStore.getState().isAuthenticated;
    
    if (!isAuthenticated) {
      throw redirect({
        to: '/login',
        search: {
          redirect: location.href,
        },
      });
    }
  },
  component: () => <Outlet />,
});