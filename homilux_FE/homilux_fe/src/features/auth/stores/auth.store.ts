// src/features/auth/stores/auth.store.ts
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { immer } from 'zustand/middleware/immer';
import type { components } from '@/types/api';

type User = components['schemas']['FetchAccount'];
type LoginResponse = components['schemas']['LoginResponse'];
type Permission = components['schemas']['PermissionResponse'];

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  
  // Computed values
  isAdmin: () => boolean;
  hasRole: () => boolean;
  permissions: () => Permission[];
  hasPermission: (apiPath: string, method: string) => boolean;
  
  // Actions
  setAuth: (data: LoginResponse) => void;
  setUser: (user: User) => void;
  setAccessToken: (token: string) => void;
  logout: () => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    immer((set, get) => ({
      user: null,
      accessToken: null,
      isAuthenticated: false,

      // Computed values
      isAdmin: () => {
        const state = get();
        return state.user?.role?.name === 'ADMIN';
      },

      hasRole: () => {
        const state = get();
        return state.user?.role !== null && state.user?.role !== undefined;
      },

      permissions: () => {
        const state = get();
        return state.user?.role?.permissionIds || [];
      },

      hasPermission: (apiPath: string, method: string) => {
        const state = get();
        const permissions = state.permissions();
        return permissions.some(
          (p) => p.apiPath === apiPath && p.method === method
        );
      },

      setAuth: (data) =>
        set((state) => {
          state.accessToken = data.access_token || '';
          state.user = {
            id: data.id,
            email: data.email,
            fullName: data.fullName,
            role: data.role,
          };
          state.isAuthenticated = true;
        }),

      setUser: (user) =>
        set((state) => {
          state.user = user;
        }),

      setAccessToken: (token) =>
        set((state) => {
          state.accessToken = token;
        }),

      logout: () =>
        set((state) => {
          state.user = null;
          state.accessToken = null;
          state.isAuthenticated = false;
        }),

      clearAuth: () =>
        set((state) => {
          state.user = null;
          state.accessToken = null;
          state.isAuthenticated = false;
        }),
    })),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        accessToken: state.accessToken,
        isAuthenticated: state.isAuthenticated,
        user: state.user,
      }),
    }
  )
);