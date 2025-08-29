// src/routes/_admin.tsx
import { createFileRoute, redirect } from '@tanstack/react-router';
import { useAuthStore } from '@/features/auth/stores/auth.store';
import { AdminLayout } from '@/components/layout/AdminLayout';
import { toast } from 'sonner';

export const Route = createFileRoute('/_admin')({
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

    // Check if user has role (not null)
    if (!state.hasRole()) {
      toast.error('Access denied. Admin privileges required.');
      throw redirect({
        to: '/',
      });
    }
  },
  component: AdminLayout,
});