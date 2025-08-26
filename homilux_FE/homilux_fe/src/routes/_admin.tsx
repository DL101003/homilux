// src/routes/_admin.tsx
import { createFileRoute, Outlet, redirect } from '@tanstack/react-router';
import { useAuthStore } from '@/features/auth/stores/auth.store';
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

function AdminLayout() {
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Admin sidebar, header, etc. */}
      <Outlet />
    </div>
  );
}