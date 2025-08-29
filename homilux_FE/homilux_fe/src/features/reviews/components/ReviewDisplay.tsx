// src/features/reviews/components/ReviewDisplay.tsx
import { Star } from 'lucide-react';
import { cn, formatDate } from '@/lib/utils';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import type { components } from '@/types/api';

type ReviewResponse = components['schemas']['ReviewResponse'];

interface ReviewDisplayProps {
  review: ReviewResponse;
}

export const ReviewDisplay = ({ review }: ReviewDisplayProps) => {
  const rating = parseInt(review.rating || '0');

  return (
    <div className="space-y-4 p-4 border rounded-lg">
      <div className="flex items-start justify-between">
        <div className="flex items-center space-x-3">
          <Avatar>
            <AvatarFallback>
              {review.user?.fullName?.charAt(0) || 'U'}
            </AvatarFallback>
          </Avatar>
          <div>
            <p className="font-medium">{review.user?.fullName || 'Anonymous'}</p>
            <p className="text-sm text-muted-foreground">
              {formatDate(review.createdAt)}
            </p>
          </div>
        </div>
        <div className="flex">
          {[1, 2, 3, 4, 5].map((star) => (
            <Star
              key={star}
              className={cn(
                'h-4 w-4',
                star <= rating
                  ? 'fill-yellow-400 text-yellow-400'
                  : 'text-gray-300'
              )}
            />
          ))}
        </div>
      </div>

      <p className="text-gray-700">{review.comment}</p>

      {review.images && review.images.length > 0 && (
        <div className="grid grid-cols-3 gap-2">
          {review.images.map((image) => (
            <img
              key={image.id}
              src={`${import.meta.env.VITE_API_URL}/storage/reviews/${image.imageUrl}`}
              alt="Review"
              className="w-full h-24 object-cover rounded-lg cursor-pointer hover:opacity-90"
              onClick={() => window.open(image.imageUrl, '_blank')}
            />
          ))}
        </div>
      )}
    </div>
  );
};