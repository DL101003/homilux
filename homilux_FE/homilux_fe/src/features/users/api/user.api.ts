// src/features/users/api/user.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type UserResponse = components['schemas']['UserResponse'];
type RegisterRequest = components['schemas']['RegisterRequest'];
type ChangePasswordRequest = components['schemas']['ChangePasswordRequest'];
type ResultPaginationDto = components['schemas']['ResultPaginationDto'];
type Pageable = components['schemas']['Pageable'];

export const userApi = {
  getAllUsers: async (params: Pageable & { spec?: any }): Promise<ResultPaginationDto> => {
    const response = await axiosClient.get('/users', { params });
    return response.data;
  },

  getUserById: async (id: number): Promise<UserResponse> => {
    const response = await axiosClient.get(`/users/${id}`);
    return response.data;
  },

  createUser: async (data: RegisterRequest): Promise<UserResponse> => {
    const response = await axiosClient.post('/users', data);
    return response.data;
  },

  updateUser: async (id: number, data: RegisterRequest): Promise<UserResponse> => {
    const response = await axiosClient.put(`/users/${id}`, data);
    return response.data;
  },

  deleteUser: async (id: number): Promise<void> => {
    await axiosClient.delete(`/users/${id}`);
  },

  getCurrentUser: async (): Promise<UserResponse> => {
    const response = await axiosClient.get('/users/me');
    return response.data;
  },

  updateCurrentUser: async (data: RegisterRequest): Promise<UserResponse> => {
    const response = await axiosClient.put('/users/me', data);
    return response.data;
  },

  changePassword: async (data: ChangePasswordRequest): Promise<string> => {
    const response = await axiosClient.post('/users/me/change-password', data);
    return response.data;
  },
};