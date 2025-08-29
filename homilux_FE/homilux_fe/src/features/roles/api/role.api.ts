// src/features/roles/api/role.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type RoleResponse = components['schemas']['RoleResponse'];
type RoleRequest = components['schemas']['RoleRequest'];
type PermissionResponse = components['schemas']['PermissionResponse'];

export const roleApi = {
  getAllRoles: async (): Promise<RoleResponse[]> => {
    const response = await axiosClient.get('/roles');
    return response.data;
  },

  getRoleById: async (id: number): Promise<RoleResponse> => {
    const response = await axiosClient.get(`/roles/${id}`);
    return response.data;
  },

  createRole: async (data: RoleRequest): Promise<RoleResponse> => {
    const response = await axiosClient.post('/roles', data);
    return response.data;
  },

  updateRole: async (id: number, data: RoleRequest): Promise<RoleResponse> => {
    const response = await axiosClient.put(`/roles/${id}`, data);
    return response.data;
  },

  deleteRole: async (id: number): Promise<void> => {
    await axiosClient.delete(`/roles/${id}`);
  },

  getAllPermissions: async (): Promise<PermissionResponse[]> => {
    const response = await axiosClient.get('/permissions');
    return response.data;
  },
};