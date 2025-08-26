// src/features/auth/hooks/useAuth.ts
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { toast } from 'sonner';
import { authApi } from '../api/auth.api';
import { useAuthStore } from '../stores/auth.store';

export const useLogin = () => {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      setAuth(data);
      queryClient.invalidateQueries({ queryKey: ['user'] });
      toast.success('Login successful!');
      
      // Navigate based on role
      if (data.role) {
        navigate({ to: '/admin/dashboard' });
      } else {
        navigate({ to: '/' });
      }
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Login failed');
    },
  });
};

export const useRegister = () => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: authApi.register,
    onSuccess: () => {
      toast.success('Registration successful! Please login.');
      navigate({ to: '/login' });
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Registration failed');
    },
  });
};

export const useLogout = () => {
  const navigate = useNavigate();
  const clearAuth = useAuthStore((state) => state.clearAuth);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authApi.logout,
    onSuccess: () => {
      clearAuth();
      queryClient.clear();
      toast.success('Logged out successfully');
      navigate({ to: '/login' });
    },
    onError: () => {
      // Even if logout fails, clear local state
      clearAuth();
      queryClient.clear();
      navigate({ to: '/login' });
    },
  });
};

export const useCurrentUser = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const setUser = useAuthStore((state) => state.setUser);

  return useQuery({
    queryKey: ['user', 'current'],
    queryFn: async () => {
      const data = await authApi.fetchAccount();
      setUser(data);
      return data;
    },
    enabled: isAuthenticated,
    staleTime: 5 * 60 * 1000, // 5 minutes
    retry: 1,
  });
};

// Hook to check permissions
export const usePermission = () => {
  const { hasPermission, permissions, hasRole, isAdmin } = useAuthStore();

  return {
    hasPermission,
    permissions: permissions(),
    hasRole: hasRole(),
    isAdmin: isAdmin(),
    canAccess: (apiPath: string, method: string) => hasPermission(apiPath, method),
    canAccessModule: (module: string) => {
      const perms = permissions();
      return perms.some((p) => p.module === module);
    },
  };
};