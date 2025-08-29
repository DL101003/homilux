// src/features/reviews/hooks/useReviews.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { reviewApi } from '../api/review.api';
import { toast } from 'sonner';
import type { components } from '@/types/api';

type ReviewRequest = components['schemas']['ReviewRequest'];

export const useCreateReview = (bookingId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ request, files }: { request: ReviewRequest; files?: File[] }) =>
      reviewApi.createReview(bookingId, request, files),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews', 'booking', bookingId] });
      queryClient.invalidateQueries({ queryKey: ['bookings', bookingId] });
      toast.success('Review submitted successfully!');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to submit review');
    },
  });
};

export const useBookingReview = (bookingId: number) => {
  return useQuery({
    queryKey: ['reviews', 'booking', bookingId],
    queryFn: () => reviewApi.getReviewForBooking(bookingId),
    enabled: !!bookingId,
    retry: false, // Don't retry if review doesn't exist
  });
};

export const usePublicReviews = (page = 0, size = 10) => {
  return useQuery({
    queryKey: ['reviews', 'public', page, size],
    queryFn: () => reviewApi.getAllPublicReviews({ page, size }),
  });
};