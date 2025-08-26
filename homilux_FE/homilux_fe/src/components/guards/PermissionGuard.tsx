// src/components/guards/PermissionGuard.tsx
import type { ReactNode } from 'react';
import { useAuthStore } from '@/features/auth/stores/auth.store';

interface PermissionGuardProps {
  children: ReactNode;
  apiPath?: string;
  method?: string;
  module?: string;
  fallback?: ReactNode;
}

export const PermissionGuard = ({
  children,
  apiPath,
  method = 'GET',
  module,
  fallback = null,
}: PermissionGuardProps) => {
  const { hasPermission, permissions } = useAuthStore();

  // If checking specific API permission
  if (apiPath && method) {
    if (!hasPermission(apiPath, method)) {
      return <>{fallback}</>;
    }
  }

  // If checking module permission
  if (module) {
    const hasModulePermission = permissions().some(
      (p) => p.module === module
    );
    if (!hasModulePermission) {
      return <>{fallback}</>;
    }
  }

  return <>{children}</>;
};