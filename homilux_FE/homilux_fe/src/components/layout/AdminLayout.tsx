// src/components/layout/AdminLayout.tsx
import { Outlet, useNavigate } from '@tanstack/react-router';
import { Sidebar } from './Sidebar';
import { useAuthStore } from '@/features/auth/stores/auth.store';
import { Button } from '@/components/ui/button';
import { LogOut, User } from 'lucide-react';
import { useLogout } from '@/features/auth/hooks/useAuth';

export const AdminLayout = () => {
  const user = useAuthStore((state) => state.user);
  const { mutate: logout } = useLogout();

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar />
      <div className="flex-1 flex flex-col">
        <header className="bg-white shadow-sm border-b">
          <div className="flex items-center justify-between px-6 py-3">
            <h2 className="text-xl font-semibold">Admin Dashboard</h2>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <User className="h-5 w-5" />
                <span className="text-sm font-medium">{user?.fullName}</span>
              </div>
              <Button variant="ghost" size="sm" onClick={() => logout()}>
                <LogOut className="h-4 w-4 mr-2" />
                Logout
              </Button>
            </div>
          </div>
        </header>
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};