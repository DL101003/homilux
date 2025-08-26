// src/features/auth/api/auth.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type LoginRequest = components['schemas']['LoginRequest'];
type LoginResponse = components['schemas']['LoginResponse'];
type RegisterRequest = components['schemas']['RegisterRequest'];
type RefreshTokenResponse = components['schemas']['RefreshTokenResponse'];
type FetchAccount = components['schemas']['FetchAccount'];

export const authApi = {
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axiosClient.post<LoginResponse>('/auth/login', data);
    console.log(response.data);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<string> => {
    const response = await axiosClient.post<string>('/auth/register', data);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await axiosClient.post('/auth/logout');
  },

  fetchAccount: async (): Promise<FetchAccount> => {
    const response = await axiosClient.get<FetchAccount>('/auth/account');
    return response.data;
  },

  refreshToken: async (): Promise<RefreshTokenResponse> => {
    const response = await axiosClient.post<RefreshTokenResponse>('/auth/refresh');
    return response.data;
  },
};