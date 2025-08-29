// src/components/layout/Sidebar.tsx
import { Link, useLocation } from '@tanstack/react-router';
import { cn } from '@/lib/utils';
import { usePermission } from '@/features/auth/hooks/useAuth';
import {
  LayoutDashboard,
  Users,
  Calendar,
  Shield,
  Settings,
  Package,
  FileText,
  DollarSign,
} from 'lucide-react';

const menuItems = [
  {
    title: 'Dashboard',
    href: '/admin/dashboard',
    icon: LayoutDashboard,
    module: null,
  },
  {
    title: 'Bookings',
    href: '/admin/bookings',
    icon: Calendar,
    module: 'BOOKINGS',
  },
  {
    title: 'Users',
    href: '/admin/users',
    icon: Users,
    module: 'USERS',
  },
  {
    title: 'Roles',
    href: '/admin/roles',
    icon: Shield,
    module: 'ROLES',
  },
  {
    title: 'Services',
    href: '/admin/services',
    icon: Package,
    module: 'SERVICES',
  },
  {
    title: 'Event Types',
    href: '/admin/event-types',
    icon: FileText,
    module: 'EVENT_TYPES',
  },
  {
    title: 'Payments',
    href: '/admin/payments',
    icon: DollarSign,
    module: 'PAYMENTS',
  },
];

export const Sidebar = () => {
  const location = useLocation();
  const { canAccessModule } = usePermission();

  const filteredMenuItems = menuItems.filter((item) => {
    if (!item.module) return true;
    return canAccessModule(item.module);
  });

  return (
    <div className="w-64 bg-gray-900 text-white">
      <div className="p-6">
        <h1 className="text-2xl font-bold">Homilux Admin</h1>
      </div>
      <nav className="mt-6">
        {filteredMenuItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.href;
          
          return (
            <Link
              key={item.href}
              to={item.href}
              className={cn(
                'flex items-center px-6 py-3 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-gray-800 text-white border-l-4 border-primary'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              )}
            >
              <Icon className="h-5 w-5 mr-3" />
              {item.title}
            </Link>
          );
        })}
      </nav>
    </div>
  );
};