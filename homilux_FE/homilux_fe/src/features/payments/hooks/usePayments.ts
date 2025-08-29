// src/features/payments/hooks/usePayments.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { paymentApi } from '../api/payment.api';
import { toast } from 'sonner';
import type { components } from '@/types/api';

type PaymentRequest = components['schemas']['PaymentRequest'];

export const usePaymentsForBooking = (bookingId: number) => {
  return useQuery({
    queryKey: ['payments', 'booking', bookingId],
    queryFn: () => paymentApi.getPaymentsForBooking(bookingId),
    enabled: !!bookingId,
  });
};

export const useCreatePayment = (bookingId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: PaymentRequest) => paymentApi.createPayment(bookingId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payments', 'booking', bookingId] });
      queryClient.invalidateQueries({ queryKey: ['bookings', bookingId] });
      toast.success('Payment recorded successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to record payment');
    },
  });
};

export const useDeletePayment = (bookingId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: paymentApi.deletePayment,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payments'] });
      queryClient.invalidateQueries({ queryKey: ['bookings', bookingId] });
      toast.success('Payment deleted successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to delete payment');
    },
  });
};

export const useVNPayPayment = () => {
  return useMutation({
    mutationFn: paymentApi.createVNPayPayment,
    onSuccess: (paymentUrl) => {
      // Redirect to VNPay payment page
      window.location.href = paymentUrl;
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to create VNPay payment');
    },
  });
};