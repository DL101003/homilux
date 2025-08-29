// src/features/files/api/file.api.ts
import { axiosClient } from '@/lib/axios';
import type { components } from '@/types/api';

type UploadFileDto = components['schemas']['UploadFileDto'];

export const fileApi = {
  uploadFiles: async (files: File[], folder: string): Promise<UploadFileDto[]> => {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append('files', file);
    });

    const response = await axiosClient.post<UploadFileDto[]>(
      `/files?folder=${encodeURIComponent(folder)}`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },
};