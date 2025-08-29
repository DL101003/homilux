// src/features/reviews/components/CreateReviewForm.tsx
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Textarea } from '@/components/ui/textarea';
import { useCreateReview } from '../hooks/useReviews';
import { Star, Upload, X, Loader2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';

const reviewSchema = z.object({
  rating: z.number().min(1).max(5),
  comment: z.string().min(10, 'Comment must be at least 10 characters'),
});

type ReviewFormData = z.infer<typeof reviewSchema>;

interface CreateReviewFormProps {
  bookingId: number;
  onSuccess?: () => void;
}

export const CreateReviewForm = ({ bookingId, onSuccess }: CreateReviewFormProps) => {
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [previewUrls, setPreviewUrls] = useState<string[]>([]);
  const { mutate: createReview, isPending } = useCreateReview(bookingId);

  const form = useForm<ReviewFormData>({
    resolver: zodResolver(reviewSchema),
    defaultValues: {
      rating: 5,
      comment: '',
    },
  });

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    const validFiles = files.filter(file => {
      const isValid = ['image/jpeg', 'image/jpg', 'image/png'].includes(file.type);
      if (!isValid) {
        toast.error(`${file.name} is not a valid image format`);
      }
      return isValid;
    });

    if (selectedFiles.length + validFiles.length > 5) {
      toast.error('Maximum 5 images allowed');
      return;
    }

    setSelectedFiles(prev => [...prev, ...validFiles]);
    
    // Create preview URLs
    validFiles.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrls(prev => [...prev, reader.result as string]);
      };
      reader.readAsDataURL(file);
    });
  };

  const removeFile = (index: number) => {
    setSelectedFiles(prev => prev.filter((_, i) => i !== index));
    setPreviewUrls(prev => prev.filter((_, i) => i !== index));
  };

  const onSubmit = (data: ReviewFormData) => {
    createReview(
      {
        request: {
          rating: data.rating.toString() as any, // Convert to byte string
          comment: data.comment,
        },
        files: selectedFiles.length > 0 ? selectedFiles : undefined,
      },
      {
        onSuccess: () => {
          form.reset();
          setSelectedFiles([]);
          setPreviewUrls([]);
          onSuccess?.();
        },
      }
    );
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="rating"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Rating</FormLabel>
              <FormControl>
                <div className="flex space-x-1">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => field.onChange(star)}
                      className="focus:outline-none"
                      disabled={isPending}
                    >
                      <Star
                        className={cn(
                          'h-8 w-8 transition-colors',
                          star <= field.value
                            ? 'fill-yellow-400 text-yellow-400'
                            : 'text-gray-300'
                        )}
                      />
                    </button>
                  ))}
                </div>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="comment"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Your Review</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Share your experience..."
                  className="min-h-[120px]"
                  {...field}
                  disabled={isPending}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Image Upload */}
        <div className="space-y-4">
          <FormLabel>Photos (Optional)</FormLabel>
          
          {/* Preview Images */}
          {previewUrls.length > 0 && (
            <div className="grid grid-cols-3 gap-4">
              {previewUrls.map((url, index) => (
                <div key={index} className="relative group">
                  <img
                    src={url}
                    alt={`Preview ${index + 1}`}
                    className="w-full h-24 object-cover rounded-lg"
                  />
                  <button
                    type="button"
                    onClick={() => removeFile(index)}
                    className="absolute top-1 right-1 p-1 bg-red-500 text-white rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                    disabled={isPending}
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              ))}
            </div>
          )}

          {/* Upload Button */}
          {selectedFiles.length < 5 && (
            <div>
              <input
                type="file"
                id="image-upload"
                accept="image/jpeg,image/jpg,image/png"
                multiple
                onChange={handleFileSelect}
                className="hidden"
                disabled={isPending}
              />
              <label
                htmlFor="image-upload"
                className={cn(
                  'flex items-center justify-center w-full p-4 border-2 border-dashed rounded-lg cursor-pointer hover:border-primary transition-colors',
                  isPending && 'opacity-50 cursor-not-allowed'
                )}
              >
                <Upload className="h-5 w-5 mr-2" />
                <span>Upload Images (Max 5)</span>
              </label>
            </div>
          )}
        </div>

        <Button type="submit" disabled={isPending} className="w-full">
          {isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
          {isPending ? 'Submitting review...' : 'Submit Review'}
        </Button>
      </form>
    </Form>
  );
};