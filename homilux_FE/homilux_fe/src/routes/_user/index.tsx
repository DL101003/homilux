// src/routes/_user/index.tsx
import { createFileRoute } from '@tanstack/react-router';
import { useAuthStore } from '@/features/auth/stores/auth.store';

export const Route = createFileRoute('/_user/')({
  component: UserHomePage,
});

function UserHomePage() {
  const user = useAuthStore((state) => state.user);

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-6">
        Welcome back, {user?.fullName || 'Guest'}!
      </h1>
      
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* User specific content */}
        <div className="p-6 bg-white rounded-lg shadow">
          <h2 className="text-xl font-semibold mb-4">My Bookings</h2>
          {/* Booking list */}
        </div>
        
        <div className="p-6 bg-white rounded-lg shadow">
          <h2 className="text-xl font-semibold mb-4">Upcoming Events</h2>
          {/* Event list */}
        </div>
        
        <div className="p-6 bg-white rounded-lg shadow">
          <h2 className="text-xl font-semibold mb-4">Recent Reviews</h2>
          {/* Review list */}
        </div>
      </div>
    </div>
  );
}