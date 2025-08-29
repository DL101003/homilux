// src/features/bookings/types/index.ts
import type { components } from '@/types/api';

export type BookingResponse = components['schemas']['BookingResponse'];
export type BookingCreationRequest = components['schemas']['BookingCreationRequest'];
export type BookingStatusUpdateRequest = components['schemas']['BookingStatusUpdateRequest'];
export type BookedServiceRequest = components['schemas']['BookedServiceRequest'];
export type BookedServiceResponse = components['schemas']['BookedServiceResponse'];

export type BookingStatus = NonNullable<BookingResponse['status']>;
export type LocationType = NonNullable<BookingResponse['locationType']>;