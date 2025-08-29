// src/features/files/hooks/useFileUpload.ts
import { useMutation } from '@tanstack/react-query';
import { fileApi } from '../api/file.api';
import { toast } from 'sonner';

export const useFileUpload = () => {
  return useMutation({
    mutationFn: ({ files, folder }: { files: File[]; folder: string }) =>
      fileApi.uploadFiles(files, folder),
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to upload files');
    },
  });
};