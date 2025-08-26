// src/features/auth/types/index.ts
import type { components } from '@/types/api';

export type LoginRequest = components['schemas']['LoginRequest'];
export type LoginResponse = components['schemas']['LoginResponse'];
export type RegisterRequest = components['schemas']['RegisterRequest'];
export type RefreshTokenResponse = components['schemas']['RefreshTokenResponse'];
export type FetchAccount = components['schemas']['FetchAccount'];
export type UserResponse = components['schemas']['UserResponse'];
export type RoleResponse = components['schemas']['RoleResponse'];
export type PermissionResponse = components['schemas']['PermissionResponse'];

export type AuthProvider = 'LOCAL' | 'GOOGLE';