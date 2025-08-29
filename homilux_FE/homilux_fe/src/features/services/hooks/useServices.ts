// src/features/services/hooks/useServices.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { serviceApi } from '../api/service.api';
import { toast } from 'sonner';
import type { components } from '@/types/api';

type RentalServiceRequest = components['schemas']['RentalServiceRequest'];

export const useServices = () => {
  return useQuery({
    queryKey: ['services'],
    queryFn: serviceApi.getAllServices,
  });
};

export const useService = (id: number) => {
  return useQuery({
    queryKey: ['services', id],
    queryFn: () => serviceApi.getServiceById(id),
    enabled: !!id,
  });
};

export const useCreateService = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: serviceApi.createService,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['services'] });
      toast.success('Service created successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to create service');
    },
  });
};

export const useUpdateService = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: RentalServiceRequest }) =>
      serviceApi.updateService(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['services'] });
      toast.success('Service updated successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to update service');
    },
  });
};

export const useDeleteService = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: serviceApi.deleteService,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['services'] });
      toast.success('Service deleted successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to delete service');
    },
  });
};