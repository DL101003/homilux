// src/routes/_user.tsx
import { createFileRoute, Outlet, redirect } from '@tanstack/react-router';
import { useAuthStore } from '@/features/auth/stores/auth.store';

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

function UserLayout() {
  return (
    <div className="min-h-screen">
      {/* User navigation, header, etc. */}
      <Outlet />
    </div>
  );
}