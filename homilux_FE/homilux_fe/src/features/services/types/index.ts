// src/features/services/types/index.ts
import type { components } from '@/types/api';

export type RentalServiceResponse = components['schemas']['RentalServiceResponse'];
export type RentalServiceRequest = components['schemas']['RentalServiceRequest'];

export type ServiceType = NonNullable<RentalServiceResponse['type']>;