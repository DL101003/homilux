// src/features/reviews/api/review.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type ReviewResponse = components['schemas']['ReviewResponse'];
type ReviewRequest = components['schemas']['ReviewRequest'];
type ResultPaginationDto = components['schemas']['ResultPaginationDto'];

export const reviewApi = {
  createReview: async (
    bookingId: number,
    request: ReviewRequest,
    files?: File[]
  ): Promise<ReviewResponse> => {
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));
    
    if (files && files.length > 0) {
      files.forEach(file => {
        formData.append('files', file);
      });
    }

    const response = await axiosClient.post<ReviewResponse>(
      `/bookings/${bookingId}/reviews`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },

  getReviewForBooking: async (bookingId: number): Promise<ReviewResponse> => {
    const response = await axiosClient.get(`/bookings/${bookingId}/reviews`);
    return response.data;
  },

  getAllPublicReviews: async (params: {
    page?: number;
    size?: number;
  }): Promise<ResultPaginationDto> => {
    const response = await axiosClient.get('/reviews', { params });
    return response.data;
  },
};