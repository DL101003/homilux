// src/features/payments/api/payment.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type PaymentResponse = components['schemas']['PaymentResponse'];
type PaymentRequest = components['schemas']['PaymentRequest'];

export const paymentApi = {
  createPayment: async (
    bookingId: number,
    data: PaymentRequest
  ): Promise<PaymentResponse> => {
    const response = await axiosClient.post(
      `/bookings/${bookingId}/payments`,
      data
    );
    return response.data;
  },

  getPaymentsForBooking: async (bookingId: number): Promise<PaymentResponse[]> => {
    const response = await axiosClient.get(`/bookings/${bookingId}/payments`);
    return response.data;
  },

  getPaymentById: async (paymentId: number): Promise<PaymentResponse> => {
    const response = await axiosClient.get(`/payments/${paymentId}`);
    return response.data;
  },

  deletePayment: async (paymentId: number): Promise<void> => {
    await axiosClient.delete(`/payments/${paymentId}`);
  },

  createVNPayPayment: async (bookingId: number): Promise<string> => {
    const response = await axiosClient.post(
      `/payments/create-vnpay?bookingId=${bookingId}`
    );
    return response.data;
  },

  handleVNPayCallback: async (params: Record<string, string>): Promise<string> => {
    const queryParams = new URLSearchParams(params).toString();
    const response = await axiosClient.get(`/payments/callback?${queryParams}`);
    return response.data;
  },
};