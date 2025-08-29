// src/features/bookings/api/booking.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type BookingResponse = components['schemas']['BookingResponse'];
type BookingCreationRequest = components['schemas']['BookingCreationRequest'];
type BookingStatusUpdateRequest = components['schemas']['BookingStatusUpdateRequest'];
type ResultPaginationDto = components['schemas']['ResultPaginationDto'];
type Pageable = components['schemas']['Pageable'];

export const bookingApi = {
  getMyBookings: async (params: Pageable): Promise<ResultPaginationDto> => {
    const response = await axiosClient.get('/bookings/me', { params });
    return response.data;
  },

  getAllBookings: async (params: Pageable & { spec?: any }): Promise<ResultPaginationDto> => {
    const response = await axiosClient.get('/bookings', { params });
    return response.data;
  },

  getBookingById: async (id: number): Promise<BookingResponse> => {
    const response = await axiosClient.get(`/bookings/${id}`);
    return response.data;
  },

  createBooking: async (data: BookingCreationRequest): Promise<BookingResponse> => {
    const response = await axiosClient.post('/bookings', data);
    return response.data;
  },

  updateBookingStatus: async (
    id: number,
    data: BookingStatusUpdateRequest
  ): Promise<BookingResponse> => {
    const response = await axiosClient.patch(`/bookings/${id}/status`, data);
    return response.data;
  },

  deleteBooking: async (id: number): Promise<void> => {
    await axiosClient.delete(`/bookings/${id}`);
  },
};