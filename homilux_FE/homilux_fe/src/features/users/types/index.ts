// src/features/users/types/index.ts
import type { components } from '@/types/api';

export type UserResponse = components['schemas']['UserResponse'];
export type RegisterRequest = components['schemas']['RegisterRequest'];
export type ChangePasswordRequest = components['schemas']['ChangePasswordRequest'];

export type AuthProvider = NonNullable<UserResponse['authProvider']>;