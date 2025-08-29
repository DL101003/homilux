// src/features/services/api/service.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type RentalServiceResponse = components['schemas']['RentalServiceResponse'];
type RentalServiceRequest = components['schemas']['RentalServiceRequest'];

export const serviceApi = {
  getAllServices: async (): Promise<RentalServiceResponse[]> => {
    const response = await axiosClient.get('/services');
    return response.data;
  },

  getServiceById: async (id: number): Promise<RentalServiceResponse> => {
    const response = await axiosClient.get(`/services/${id}`);
    return response.data;
  },

  createService: async (data: RentalServiceRequest): Promise<RentalServiceResponse> => {
    const response = await axiosClient.post('/services', data);
    return response.data;
  },

  updateService: async (
    id: number,
    data: RentalServiceRequest
  ): Promise<RentalServiceResponse> => {
    const response = await axiosClient.put(`/services/${id}`, data);
    return response.data;
  },

  deleteService: async (id: number): Promise<void> => {
    await axiosClient.delete(`/services/${id}`);
  },
};