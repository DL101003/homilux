// src/features/event-types/hooks/useEventTypes.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { eventTypeApi } from '../api/eventType.api';
import { toast } from 'sonner';
import type { components } from '@/types/api';

type EventTypeRequest = components['schemas']['EventTypeRequest'];

export const useEventTypes = () => {
  return useQuery({
    queryKey: ['eventTypes'],
    queryFn: eventTypeApi.getAllEventTypes,
  });
};

export const useEventType = (id: number) => {
  return useQuery({
    queryKey: ['eventTypes', id],
    queryFn: () => eventTypeApi.getEventTypeById(id),
    enabled: !!id,
  });
};

export const useCreateEventType = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: eventTypeApi.createEventType,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['eventTypes'] });
      toast.success('Event type created successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to create event type');
    },
  });
};

export const useUpdateEventType = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: EventTypeRequest }) =>
      eventTypeApi.updateEventType(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['eventTypes'] });
      toast.success('Event type updated successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to update event type');
    },
  });
};

export const useDeleteEventType = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: eventTypeApi.deleteEventType,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['eventTypes'] });
      toast.success('Event type deleted successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to delete event type');
    },
  });
};