// src/features/event-types/api/eventType.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type EventTypeResponse = components['schemas']['EventTypeResponse'];
type EventTypeRequest = components['schemas']['EventTypeRequest'];

export const eventTypeApi = {
  getAllEventTypes: async (): Promise<EventTypeResponse[]> => {
    const response = await axiosClient.get('/event-types');
    return response.data;
  },

  getEventTypeById: async (id: number): Promise<EventTypeResponse> => {
    const response = await axiosClient.get(`/event-types/${id}`);
    return response.data;
  },

  createEventType: async (data: EventTypeRequest): Promise<EventTypeResponse> => {
    const response = await axiosClient.post('/event-types', data);
    return response.data;
  },

  updateEventType: async (
    id: number,
    data: EventTypeRequest
  ): Promise<EventTypeResponse> => {
    const response = await axiosClient.put(`/event-types/${id}`, data);
    return response.data;
  },

  deleteEventType: async (id: number): Promise<void> => {
    await axiosClient.delete(`/event-types/${id}`);
  },
};