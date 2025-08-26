import type { components } from '@/types/api'

export type User = components['schemas']['FetchAccount']
export type LoginRequest = components['schemas']['LoginRequest']
export type LoginResponse = components['schemas']['LoginResponse']
export type RegisterRequest = components['schemas']['RegisterRequest']
export type RefreshTokenResponse = components['schemas']['RefreshTokenResponse']
export type RoleResponse = components['schemas']['RoleResponse']
export type PermissionResponse = components['schemas']['PermissionResponse']

export interface AuthState {
  user: User | null
  accessToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  isAdmin: boolean // Thêm flag để check admin
  login: (credentials: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  refreshAccount: () => Promise<void>
  setAccessToken: (token: string) => void
  clearAuth: () => void
  hasPermission: (permission: string) => boolean
  hasModule: (module: string) => boolean
  canAccess: (path: string, method: string) => boolean
}

export enum UserType {
  CUSTOMER = 'CUSTOMER', // role = null
  ADMIN = 'ADMIN' // role != null
}

export interface PermissionCheck {
  module: string
  apiPath: string
  method: string
  name: string
}