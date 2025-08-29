// src/features/bookings/hooks/useBookings.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { bookingApi } from '../api/booking.api';
import { toast } from 'sonner';
import { useNavigate } from '@tanstack/react-router';
import type { components } from '@/types/api';

type BookingCreationRequest = components['schemas']['BookingCreationRequest'];
type BookingStatusUpdateRequest = components['schemas']['BookingStatusUpdateRequest'];

export const useMyBookings = (page = 0, size = 10) => {
  return useQuery({
    queryKey: ['bookings', 'me', page, size],
    queryFn: () => bookingApi.getMyBookings({ page, size }),
  });
};

export const useAllBookings = (page = 0, size = 10) => {
  return useQuery({
    queryKey: ['bookings', 'all', page, size],
    queryFn: () => bookingApi.getAllBookings({ page, size }),
  });
};

export const useBooking = (id: number) => {
  return useQuery({
    queryKey: ['bookings', id],
    queryFn: () => bookingApi.getBookingById(id),
    enabled: !!id,
  });
};

export const useCreateBooking = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: BookingCreationRequest) => bookingApi.createBooking(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['bookings'] });
      toast.success('Booking created successfully!');
      navigate({ to: '/bookings/$id', params: { id: data.id!.toString() } });
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to create booking');
    },
  });
};

export const useUpdateBookingStatus = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: BookingStatusUpdateRequest }) =>
      bookingApi.updateBookingStatus(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bookings'] });
      toast.success('Booking status updated!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to update status');
    },
  });
};

export const useDeleteBooking = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: bookingApi.deleteBooking,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bookings'] });
      toast.success('Booking deleted successfully!');
      navigate({ to: '/bookings' });
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to delete booking');
    },
  });
};