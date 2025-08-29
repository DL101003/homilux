// src/routes/_user.tsx
import { createFileRoute, redirect } from '@tanstack/react-router';
import { useAuthStore } from '@/features/auth/stores/auth.store';
import { UserLayout } from '@/components/layout/UserLayout';

export const Route = createFileRoute('/_user')({
  beforeLoad: async ({ location }) => {
    const state = useAuthStore.getState();
    
    if (!state.isAuthenticated) {
      throw redirect({
        to: '/login',
        search: {
          redirect: location.href,
        },
      });
    }

    // Redirect admin users to admin area
    if (state.hasRole()) {
      throw redirect({
        to: '/admin/dashboard',
      });
    }
  },
  component: UserLayout,
});